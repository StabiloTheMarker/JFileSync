package jfilesyncer.util;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class UriQueryParamParser {
  public static Map<String, String> fromURI(URI uri) {
    var query = uri.getQuery();
    var paramMap = new HashMap<String, String>();
    if (query != null && !query.isEmpty()) {
      Arrays.stream(query.split("&"))
          .forEach(
              s -> {
                var split = s.split("=");
                paramMap.put(split[0], split[1]);
              });
      return paramMap;
    }
    return Map.of();
  }
}
