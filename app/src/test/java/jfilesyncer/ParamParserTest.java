package jfilesyncer;

import jfilesyncer.util.UriQueryParamParser;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;

import static org.assertj.core.api.Assertions.assertThat;

public class ParamParserTest {
    @Test
    public void shouldParseParams() throws URISyntaxException {
        var uri = new URI("http://localhost:8080?foo=1&bar=2");
        var params = UriQueryParamParser.fromURI(uri);
        assertThat(params.get("foo")).isEqualTo("1");
        assertThat(params.get("bar")).isEqualTo("2");
    }

    @Test
    public void shouldWorkWhenNoParamsExist() throws URISyntaxException {
        var uri = new URI("http://localhost:8080");
        var params = UriQueryParamParser.fromURI(uri);
        assertThat(params.isEmpty()).isTrue();
    }
}
