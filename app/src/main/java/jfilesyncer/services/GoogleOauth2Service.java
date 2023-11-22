package jfilesyncer.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import jfilesyncer.models.AccessToken;
import jfilesyncer.util.HttpClientGetter;
import jfilesyncer.util.UriBuilder;
import jfilesyncer.util.UriQueryParamParser;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.awt.*;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Logger;

public class GoogleOauth2Service {

  private final CredentialsService credentialsService;
  private final String REDIRECT_URI = "redirect_uri";
  private final String CLIENT_ID = "client_id";
  private final String GRANT_TYPE = "grant_type";
  private final String REDIRECT_PATH = "/oauth2/callback";

  private final String RESPONSE_TYPE = "response_type";
  private final String SCOPE = "scope";
  private final Logger logger = Logger.getLogger(GoogleOauth2Service.class.getName());
  private final int PORT = 8080;

  private boolean loadingCredentials = false;

  private AccessToken credentials = null;

  public GoogleOauth2Service(CredentialsService credentialsService) {
    this.credentialsService = credentialsService;
  }

  public AccessToken getCredentials() {
    return credentials;
  }

  public boolean credentialsAvailable() {
    return credentials != null;
  }

  public void loadCredentials() {
    try {
      if (credentialsService.credentialsExist()) {
        credentials = credentialsService.loadCredentials();
        return;
      }
      credentials = fetchAccessToken();
      logger.info("Fetched new AccessToken");
      credentialsService.saveCredentials(credentials);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public AccessToken fetchAccessToken() {
    try {
      var queue = new ArrayBlockingQueue<AccessToken>(1);
      startServerThread(queue);
      var baseAuthenticationUri = getBaseAuthenticationUri();
      logger.info("Starting with url " + baseAuthenticationUri);
      Desktop.getDesktop().browse(baseAuthenticationUri);
      logger.info("Started Google Oauth Process. Waiting for server thread to finish");
      return queue.take();
    } catch (IOException | InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  private URI getBaseAuthenticationUri() {
    return new UriBuilder()
        .scheme(UriBuilder.Scheme.HTTPS)
        .domain("accounts.google.com")
        .path("o/oauth2/auth")
        .addParam(
            CLIENT_ID, "456804413549-o0ul05pih5rcltg627q956m4f1dcd9rd.apps.googleusercontent.com")
        .addParam(RESPONSE_TYPE, "code")
        .addParam(REDIRECT_URI, getRedirectUrl())
            .addParam(SCOPE, "https://www.googleapis.com/auth/drive")
        .build();
  }

  private AccessToken getAccessTokenResponse(HttpResponse<String> response)
      throws JsonProcessingException {
    var body = response.body();
    return new ObjectMapper().readValue(body, AccessToken.class);
  }

  private void createRandomAnswer(HttpExchange httpExchange) throws IOException {
    httpExchange.sendResponseHeaders(200, "You can close this tab".length());
    httpExchange.getResponseBody().write("You can close this tab".getBytes());
  }

  private HttpRequest getAuthRequest(Map<String, String> requestParams) {
    return HttpRequest.newBuilder()
        .uri(getAuthUri(requestParams))
        .POST(HttpRequest.BodyPublishers.noBody())
        .build();
  }

  private URI getAuthUri(Map<String, String> requestParams) {
    return new UriBuilder()
        .scheme(UriBuilder.Scheme.HTTPS)
        .domain("oauth2.googleapis.com")
        .path("/token")
        .setParams(requestParams)
        .addParam(REDIRECT_URI, getRedirectUrl())
        .addParam(GRANT_TYPE, "authorization_code")
        .addParam(
            CLIENT_ID, "456804413549-o0ul05pih5rcltg627q956m4f1dcd9rd.apps.googleusercontent.com")
        .addParam("client_secret", "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")
        .build();
  }

  private String getRedirectUrl() {
    return "http://localhost:8080" + REDIRECT_PATH;
  }

  private void startServerThread(BlockingQueue<AccessToken> queue) throws IOException {
    var server = HttpServer.create(new InetSocketAddress(PORT), 0);
    server.createContext(
        REDIRECT_PATH, httpExchange -> handleRedirect(httpExchange, queue, server));
    var thread = new Thread(server::start);
    thread.start();
  }

  private void handleRedirect(
      HttpExchange httpExchange, BlockingQueue<AccessToken> queue, HttpServer server)
      throws IOException {
    logger.info(String.format("Uri is %s", httpExchange.getRequestURI().toString()));
    var requestParams = UriQueryParamParser.fromURI(httpExchange.getRequestURI());
    logger.info(String.format("Params are %s", requestParams.toString()));
    if (Objects.equals(httpExchange.getRequestMethod(), "GET")) {
      createRandomAnswer(httpExchange);
      try (var client = HttpClientGetter.getHttpClientWithTrustAllContext()) {
        var request = getAuthRequest(requestParams);
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());
        var accessTokenObj = getAccessTokenResponse(response);
        queue.put(accessTokenObj);
        logger.info(
            String.format(
                "Received Response with code %s and body %s",
                response.statusCode(), accessTokenObj));
        server.stop(1);

      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }
  }
}
