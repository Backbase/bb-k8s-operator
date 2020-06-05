package com.backbase.operator;

import com.backbase.model.StaticResource;
import com.backbase.model.StaticResourceDoneable;
import com.backbase.model.StaticResourceList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.Watcher;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.quarkus.runtime.StartupEvent;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.math.BigInteger;
import java.net.Authenticator;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonPointer;
import javax.json.JsonString;

public class StaticResourceWatcher {

    @Inject
    KubernetesClient defaultClient;

    @Inject
    NonNamespaceOperation<StaticResource, StaticResourceList, StaticResourceDoneable, Resource<StaticResource, StaticResourceDoneable>> crClient;

    public static final String IMPORT_ZIP = "/tmp/import.zip";
    public static final String LOGIN = "http://%s:8080/api/auth/login";
    public static final String PROVISIONING = "http://%s:8080/api/provisioning/importing/packages";

    void onStartup(@Observes final StartupEvent event) {
        System.out.println("Startup");
        crClient.watch(new Watcher<StaticResource>() {
            @Override
            public void eventReceived(final Action action, final StaticResource resource) {
                System.out.println("Event " + action.name());
                if (action == Action.ADDED) {
                    final String app = resource.getMetadata().getName();
                    final String provisioning_service = resource.getSpec().getProvisioning();
                    final String repo = resource.getSpec().getRepository();
                    final String repo_username = resource.getSpec().getRepousername();
                    final String repo_password = resource.getSpec().getRepopassword();
                    final String username = resource.getSpec().getUsername();
                    final String password = resource.getSpec().getPassword();
                    final List<String> statics = resource.getSpec().getStatics();
                    final Map<String, String> labels = new HashMap<>();
                    labels.put("app", app);

//                    final String serviceURL = defaultClient.services().inNamespace(defaultClient.getNamespace())
//                            .withName(provisioning_service).get().toString();
//                    System.out.println("Service URL " + serviceURL);

                    for (String url : statics
                    ) {

                        //Download the zip
                        Path localFile = Paths.get(IMPORT_ZIP);
                        if (Files.exists(localFile)) {
                            File file = new File(localFile.toString());
                            file.delete();
                        }

                        //Download zips
                        HttpRequest requestDownload = HttpRequest.newBuilder()
                            .GET()
                            .uri(URI.create(
                                repo + url))
                            .build();

                        HttpClient httpClient = getHttpClient(repo_username, repo_password);

                        HttpResponse<Path> responseDownload = null;
                        try {
                            responseDownload = httpClient.send(requestDownload, BodyHandlers.ofFile(
                                Paths.get(IMPORT_ZIP)));
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println(responseDownload.statusCode());

                        //Login
                        // json formatted data
                        String json = new StringBuilder()
                            .append("{")
                            .append("\"username\":\"" + username + "\",")
                            .append("\"password\":\"" + password + "\"")
                            .append("}").toString();

                        // add json header
                        HttpRequest request = HttpRequest.newBuilder()
                            .POST(BodyPublishers.ofString(json))
                            .uri(URI.create(String.format(LOGIN, provisioning_service)))
                            .header("Content-Type", "application/json")
                            .build();

                        HttpResponse<String> response = null;
                        try {
                            response = httpClient.send(request, BodyHandlers.ofString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        // print response body
                        String xsrf_token = ((CookieManager) httpClient.cookieHandler().get()).getCookieStore()
                            .getCookies().get(0)
                            .getValue();

                        System.out.println(xsrf_token);

                        JsonObject jsonObject = Json.createReader(new StringReader(response.body())).readObject();
                        JsonPointer jsonPointer = Json.createPointer("/access_token");
                        JsonString jsonString = (JsonString)jsonPointer.getValue(jsonObject);
                        String token = jsonString.getString();

                        System.out.println(token);

                        //Provision
                        String boundary = new BigInteger(256, new Random()).toString();
                        localFile = Paths.get(IMPORT_ZIP);
                        Map<Object, Object> data = new LinkedHashMap<>();
                        data.put("package", localFile);

                        HttpRequest requestProvisioning = null;
                        try {
                            requestProvisioning = HttpRequest.newBuilder()
                                .uri(URI.create(String.format(PROVISIONING, provisioning_service)))
                                .setHeader("X-XSRF-TOKEN", xsrf_token)
                                .setHeader("Authorization", "Bearer " + token)
                                .header("Content-Type", "multipart/form-data;boundary=" + boundary)
                                .POST(ofMimeMultipartData(data, boundary))
                                .build();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        HttpResponse<String> responseProvisioning = null;
                        try {
                            responseProvisioning = httpClient
                                .send(requestProvisioning, BodyHandlers.ofString());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        System.out.println(responseProvisioning.statusCode());

                    }

                }
            }

            @Override
            public void onClose(final KubernetesClientException e) {
            }
        });
    }

    private static BodyPublisher ofMimeMultipartData(Map<Object, Object> data,
        String boundary) throws IOException {
        var byteArrays = new ArrayList<byte[]>();
        byte[] separator = ("--" + boundary + "\r\nContent-Disposition: form-data; name=")
            .getBytes(StandardCharsets.UTF_8);
        for (Map.Entry<Object, Object> entry : data.entrySet()) {
            byteArrays.add(separator);

            if (entry.getValue() instanceof Path) {
                var path = (Path) entry.getValue();
                String mimeType = Files.probeContentType(path);
                byteArrays.add(("\"" + entry.getKey() + "\"; filename=\"" + path.getFileName()
                    + "\"\r\nContent-Type: " + mimeType + "\r\n\r\n").getBytes(StandardCharsets.UTF_8));
                byteArrays.add(Files.readAllBytes(path));
                byteArrays.add("\r\n".getBytes(StandardCharsets.UTF_8));
            } else {
                byteArrays.add(("\"" + entry.getKey() + "\"\r\n\r\n" + entry.getValue() + "\r\n")
                    .getBytes(StandardCharsets.UTF_8));
            }
        }
        byteArrays.add(("--" + boundary + "--").getBytes(StandardCharsets.UTF_8));
        return BodyPublishers.ofByteArrays(byteArrays);
    }

    private static HttpClient getHttpClient(String username, String password) {

        HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .authenticator(new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(
                        username,
                        password.toCharArray());
                }

            })
            .cookieHandler(new CookieManager(null, CookiePolicy.ACCEPT_ALL))
            .connectTimeout(Duration.ofSeconds(10))
            .build();

        return httpClient;
    }
}
