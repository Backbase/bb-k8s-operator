package com.backbase.client;

import com.backbase.model.StaticResource;
import com.backbase.model.StaticResourceDoneable;
import com.backbase.model.StaticResourceList;
import io.fabric8.kubernetes.api.model.apiextensions.CustomResourceDefinition;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.NonNamespaceOperation;
import io.fabric8.kubernetes.client.dsl.Resource;
import io.fabric8.kubernetes.internal.KubernetesDeserializer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.inject.Singleton;

public class KubernetesClientProducer {

    @Produces
    @Singleton
    @Named("namespace")
    String findMyCurrentNamespace() throws IOException {
        return new
            String(Files.readAllBytes(Paths.get("/var/run/secrets/kubernetes.io/serviceaccount/namespace")));
    }

    @Produces
    @Singleton
    KubernetesClient makeDefaultClient(@Named("namespace") String namespace) {
        return new DefaultKubernetesClient().inNamespace(namespace);
    }

    @Produces
    @Singleton
    NonNamespaceOperation<StaticResource, StaticResourceList, StaticResourceDoneable, Resource<StaticResource, StaticResourceDoneable>>
    makeCustomHelloResourceClient(KubernetesClient defaultClient, @Named("namespace") String namespace) {

        KubernetesDeserializer.registerCustomKind("mykubernetes.acme.org/v1beta2", "Static", StaticResource.class);

        CustomResourceDefinition crd = defaultClient.customResourceDefinitions()
        .list()
        .getItems()
        .stream()
        .filter(d -> "statics.mykubernetes.acme.org".equals(d.getMetadata().getName()))
        .findAny()
            .orElseThrow(() -> new RuntimeException("Deployment error: Custom resource definition mykubernetes.acme.org/v1beta2 not found."));
            
        return defaultClient.customResources(crd, StaticResource.class, StaticResourceList.class, StaticResourceDoneable.class).inNamespace(namespace);

    }

}

