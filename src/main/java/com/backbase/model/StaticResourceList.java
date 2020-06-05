package com.backbase.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.fabric8.kubernetes.client.CustomResourceList;

@JsonSerialize
public class StaticResourceList extends CustomResourceList<StaticResource> {

}

