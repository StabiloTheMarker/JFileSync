package jfilesyncer;

import jfilesyncer.util.UriBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class UriBuilderTest {
  @Test
  public void shouldCreateUriForHttpWithoutPaths() {
    var builder = new UriBuilder();
    var uri =
        builder
            .scheme(UriBuilder.Scheme.HTTP)
            .domain("google.com")
            .path("foo/bar")
            .addParam("zoo", "3")
            .addParam("foo", "1")
            .build();
    assertThat(uri.toString()).isEqualTo("http://google.com/foo/bar?foo=1&zoo=3");
  }

  @Test
  public void shouldThrowExceptionWhenDomainIsNull() {
    assertThrows(
        IllegalStateException.class,
        () -> {
          new UriBuilder().build();
        });
  }
}
