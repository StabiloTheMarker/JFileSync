package jfilesyncer.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

public class UriBuilder {
  public enum Scheme {
    HTTP,
    HTTPS
  }

  private String domain = null;
  private Scheme scheme = null;
  private String path = null;

  private final Map<String, String> queryParams = new HashMap<>();

  public URI build() {
    validate();
    var sBuilder = new StringBuilder();
    sBuilder.append(this.scheme == Scheme.HTTP ? "http://" : "https://");
    sBuilder.append(this.domain);
    if (path != null) sBuilder.append(path.charAt(0) == '/' ? "" : "/").append(path);
    if (!queryParams.isEmpty()) sBuilder.append("?");
    queryParams.forEach(
        (key, value) -> {
          sBuilder.append(key).append("=").append(value);
        });
    try {
      return new URI(sBuilder.toString());
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  private void validate() {
    if (domain == null) throw new IllegalStateException("domain must not be null");
    if (scheme == null) throw new IllegalStateException("scheme must not be null");
  }

  public UriBuilder scheme(Scheme scheme) {
    this.scheme = scheme;
    return this;
  }

  public UriBuilder domain(String domain) {
    this.domain = domain;
    return this;
  }

  public UriBuilder path(String path) {
    this.path = path;
    return this;
  }

  public UriBuilder addParam(String key, String value) {
    this.queryParams.put(key, value);
    return this;
  }
}
