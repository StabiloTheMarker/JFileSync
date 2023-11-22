package jfilesyncer.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jfilesyncer.models.AccessToken;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.Arrays;

public class CredentialsService {
  private  final String FILE_PATH = ".credentials";

  public  void saveCredentials(AccessToken credentials) throws IOException {
    var objectMapper = new ObjectMapper();
    var file = new File(FILE_PATH);
    try (var stream = new FileOutputStream(file)) {
      stream.write(objectMapper.writeValueAsBytes(credentials));
    }
  }

  @Nullable
  public  AccessToken loadCredentials() throws IOException {
    var objectMapper = new ObjectMapper();
    var file = new File(FILE_PATH);
    if (!file.isFile()) {
      return null;
    }
    return objectMapper.readValue(file, AccessToken.class);
  }

  public  boolean credentialsExist() {
    return new File(FILE_PATH).isFile();
  }
}
