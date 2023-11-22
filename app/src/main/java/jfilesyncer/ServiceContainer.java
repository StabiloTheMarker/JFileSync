package jfilesyncer;

import jfilesyncer.services.CredentialsService;
import jfilesyncer.services.GDriveService;
import jfilesyncer.services.GoogleOauth2Service;

public class ServiceContainer {
  private GDriveService gDriveService;
  private GoogleOauth2Service googleOauth2Service;
  private CredentialsService credentialsService;

  public GDriveService getgDriveService() {
    return gDriveService;
  }

  public GoogleOauth2Service getGoogleOauth2Service() {
    return googleOauth2Service;
  }

  public CredentialsService getCredentialsService() {
    return credentialsService;
  }

  public ServiceContainer() {
    this.credentialsService = new CredentialsService();
    this.googleOauth2Service = new GoogleOauth2Service(credentialsService);
    this.gDriveService = new GDriveService(googleOauth2Service);
  }
}
