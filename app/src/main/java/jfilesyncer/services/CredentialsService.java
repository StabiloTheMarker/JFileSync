package jfilesyncer.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jfilesyncer.models.AccessToken;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Arrays;

public class CredentialsService {
  private static final String FILE_PATH = ".credentials";

  public static void saveCredentials(AccessToken credentials) throws IOException {
    var objectMapper = new ObjectMapper();
    var file = new File(FILE_PATH);
    try (var stream = new FileOutputStream(file)) {
      stream.write(objectMapper.writeValueAsBytes(credentials));
    }
  }

  @Nullable
  public static AccessToken loadCredentials() throws IOException {
    var objectMapper = new ObjectMapper();
    var file = new File(FILE_PATH);
    if (!file.isFile()) {
      return null;
    }
    return objectMapper.readValue(file, AccessToken.class);
  }

  public static boolean credentialsExist() {
    return false;
  }
}
