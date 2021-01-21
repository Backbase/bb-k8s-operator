package com.backbase.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.api.model.Namespaced;
import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Version;

@Version("v1")
@Group("mykubernetes.acme.org")
@JsonSerialize
public class StaticResource extends CustomResource<StaticResourceSpec, StaticResourceStatus> implements Namespaced {
}


