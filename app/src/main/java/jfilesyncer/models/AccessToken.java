package jfilesyncer.models;

import com.fasterxml.jackson.annotation.JsonProperty;


public record AccessToken(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("expires_in") Integer expiresIn,
    @JsonProperty("refresh_token") String refreshToken,
    String scope,
    @JsonProperty("token_type") String tokenType) {}
