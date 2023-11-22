package jfilesyncer.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import jfilesyncer.models.AccessToken;
import jfilesyncer.models.DriveFile;
import jfilesyncer.models.ListFilesResponse;
import jfilesyncer.util.HttpClientGetter;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.logging.Logger;

public class GDriveService {
  private GoogleOauth2Service googleOauth2Service;

  private final Logger logger = Logger.getLogger(GDriveService.class.getName());
  private AccessToken credentials = null;

  public GDriveService(GoogleOauth2Service googleOauth2Service) {
    this.googleOauth2Service = googleOauth2Service;
    if (!googleOauth2Service.credentialsAvailable()) {
      googleOauth2Service.loadCredentials();
    }
    credentials = googleOauth2Service.getCredentials();
  }

  public List<DriveFile> getAllFiles() {
    var url = "https://www.googleapis.com/drive/v2/files";
    try (var client = HttpClientGetter.getHttpClientWithTrustAllContext()) {
      var request =
          HttpRequest.newBuilder(new URI(url))
              .header("Authorization", "Bearer " + credentials.accessToken())
              .GET()
              .build();
      var response = client.send(request, HttpResponse.BodyHandlers.ofString());
      logger.info("File Request: Got Response with code " + response.statusCode());
      var body = response.body();
      logger.info("Body of request is " + body);
      var parsed = new ObjectMapper().readValue(body, ListFilesResponse.class);
      return parsed.items();
    } catch (URISyntaxException | IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
