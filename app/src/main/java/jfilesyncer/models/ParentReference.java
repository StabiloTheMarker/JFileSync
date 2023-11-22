package jfilesyncer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ParentReference(
    String selfLink, String id, boolean isRoot, String kind, String parentLink) {}
