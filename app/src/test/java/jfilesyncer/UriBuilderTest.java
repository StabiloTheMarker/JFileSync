package jfilesyncer;

import jfilesyncer.util.UriBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class UriBuilderTest {
  @Test
  public void shouldCreateUriForHttpWithoutPaths() {
    var builder = new UriBuilder();
    var uri = builder
        .scheme(UriBuilder.Scheme.HTTP)
        .domain("google.com")
        .path("foo/bar")
        .addParam("foo", "1").build();
    assertThat(uri.toString()).isEqualTo("http://google.com/foo/bar?foo=1");

  }
}
