package com.backbase.model;

import io.fabric8.kubernetes.api.builder.Function;
import io.fabric8.kubernetes.client.CustomResourceDoneable;

public class StaticResourceDoneable extends CustomResourceDoneable<StaticResource> {

    public StaticResourceDoneable(StaticResource resource, Function<StaticResource, StaticResource> function) {
        super(resource, function);
    }
}

