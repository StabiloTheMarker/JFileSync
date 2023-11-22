package jfilesyncer.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record DriveFile(
    String originalFilename,
    String selfLink,
    String modifiedDate,
    boolean ownedByMe,
    String title,
    String fileExtension,
    String kind,
    String downloadUrl,
    String id,
    String version,
    List<ParentReference> parents) {}
