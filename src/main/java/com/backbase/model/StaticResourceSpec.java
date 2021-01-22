package com.backbase.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.api.model.KubernetesResource;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.ArrayList;
import java.util.List;


@RegisterForReflection
@JsonDeserialize(
    using = JsonDeserializer.None.class
)
public class StaticResourceSpec implements KubernetesResource {

    // repository: https://repo.backbase.com
    // repousername: user
    // repopassword: pass
    // statics:
    //   - /backbase-6-release/com/backbase/cxp/editorial-collection/b3554/editorial-collection-b3554.zip
    //   - /expert-release-local/com/backbase/web-sdk/collection/collection-bb-web-sdk/1.11.0/collection-bb-web-sdk-1.11.0.zip
    //   - /backbase-6-release/com/backbase/cxp/experience-manager/b3554/experience-manager-b3554.zip
    // auth: auth
    // provisioning: provisioning
    // password: admin
    // username: admin

    @JsonProperty("statics")
    private List<String> statics = new ArrayList<>();
    @JsonProperty("repository")
    private String repository;
    @JsonProperty("repousername")
    private String repousername;
    @JsonProperty("repopassword")
    private String repopassword;
    @JsonProperty("auth")
    private String auth;
    @JsonProperty("provisioning")
    private String provisioning;
    @JsonProperty("password")
    private String password;
    @JsonProperty("username")
    private String username;
    // getters/setters
    public List<String> getStatics() {
        return statics;
    }

    public void setStatics(List<String> statics) {
        this.statics = statics;
    }

    public String getRepository() {
        return repository;
    }

    public void setRepository(String repository) {
        this.repository = repository;
    }

    public String getRepousername() {
        return repousername;
    }

    public void setRepousername(String repousername) {
        this.repousername = repousername;
    }

    public String getRepopassword() {
        return repopassword;
    }

    public void setRepopassword(String repopassword) {
        this.repopassword = repopassword;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getProvisioning() {
        return provisioning;
    }

    public void setProvisioning(String provisioning) {
        this.provisioning = provisioning;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }



}

