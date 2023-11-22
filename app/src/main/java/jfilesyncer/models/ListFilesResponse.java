package jfilesyncer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record ListFilesResponse(
    String nextPageToken,
    String kind,
    String etag,
    String selfLink,
    boolean incompleteSearch,
    String nextLink,
    List<DriveFile> items) {}
