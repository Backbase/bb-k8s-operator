package com.backbase.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.fabric8.kubernetes.client.CustomResource;

@JsonDeserialize
public class StaticResource extends CustomResource {

    private StaticResourceSpec spec;
    private StaticResourceStatus status;
    // getters/setters

    public StaticResourceSpec getSpec() {
        return spec;
    }

    public void setSpec(StaticResourceSpec spec) {
        this.spec = spec;
    }

    public StaticResourceStatus getStatus() {
        return status;
    }

    public void setStatus(StaticResourceStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        String name = getMetadata() != null ? getMetadata().getName() : "unknown";
        String version = getMetadata() != null ? getMetadata().getResourceVersion() : "unknown";
        return "name=" + name + " version=" + version + " value=" + spec;
    }
}

