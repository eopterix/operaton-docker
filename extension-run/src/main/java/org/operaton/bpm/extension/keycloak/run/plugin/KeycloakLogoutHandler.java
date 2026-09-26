package org.operaton.bpm.extension.keycloak.run.plugin;

import java.io.IOException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//import java.util.logging.Logger;
//import java.util.logging.Level;
//import java.util.logging.LogManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

//import org.springframework.web.util.UriComponentsBuilder;

/**
 * Keycloak Logout Handler.
 */
@Service
public class KeycloakLogoutHandler implements LogoutSuccessHandler {

  /**
   * This class' logger.
   */
  private static final Logger LOG = LoggerFactory.getLogger(KeycloakLogoutHandler.class);
  //private static final Logger logger = Logger.getLogger(KeycloakLogoutHandler.class.getName());

  /**
   * Redirect strategy.
   */
  private final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

  /**
   * Keycloak's logout URI.
   */
  private String oauth2UserLogoutUri;

  /**
   * Default constructor.
   *
   * @param oauth2UserAuthorizationUri configured keycloak authorization URI
   */
  public KeycloakLogoutHandler(@Value("${spring.security.oauth2.client.provider.keycloak.authorization-uri:}") String oauth2UserAuthorizationUri) {
    if (!ObjectUtils.isEmpty(oauth2UserAuthorizationUri)) {
      // in order to get the valid logout uri: simply replace "/auth" at the end of the user authorization uri with "/logout"
      this.oauth2UserLogoutUri = oauth2UserAuthorizationUri.replace("openid-connect/auth", "openid-connect/logout");
    }
  }



  /**
   * {@inheritDoc}
   */

/*
@Override
public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
    throws IOException {
  if (!ObjectUtils.isEmpty(oauth2UserLogoutUri)) {
    // 1. Berechne die Redirect-URI (wie in deinem Originalcode)
    String requestUrl = request.getRequestURL().toString();
    String redirectUri = requestUrl.substring(0, requestUrl.indexOf("/api"));

    String logoutUrl = oauth2UserLogoutUri + "?post_logout_redirect_uri=" + redirectUri + "&id_token_hint="
        + ((OidcUser) authentication.getPrincipal()).getIdToken().getTokenValue();

    // 2. WICHTIG: Lokale Spring-Session ungültig machen
    if (request.getSession(false) != null) {
        request.getSession().invalidate();
    }

    // 3. Lösung für den CORS/AJAX-Fehler der Webapp:
    // Wir senden KEINEN HTTP 302 (redirectStrategy.sendRedirect), da das JS das blockiert.
    // Wir senden stattdessen HTTP 401 (Unauthorized) und übergeben die Ziel-URL im Location-Header.
    // Das veranlasst das Operaton-Frontend dazu, das gesamte Fenster zu Keycloak zu navigieren.
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setHeader("Location", logoutUrl);
    
    // Optional: Die URL zur Sicherheit in den Body schreiben, falls das Skript dort liest
    response.setContentType("text/plain");
    response.getWriter().write(logoutUrl);
    response.getWriter().flush();
    
    LOG.info("AJAX-Logout abgefangen. Weiterleitung des Hauptfensters erzwungen zu: {}", logoutUrl);
    //response.sendRedirect(request.getContextPath() + 
          
  }
}
*/


  @Override
  public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    if (!ObjectUtils.isEmpty(oauth2UserLogoutUri)) {
      // Calculate redirect URI for Keycloak, something like http://<host:port>/operaton
      String requestUrl = request.getRequestURL().toString();
      LOG.info("Request URI: {}", requestUrl);
      String redirectUri = requestUrl.substring(0, requestUrl.indexOf("/api"));
      LOG.info("Redirect URI: {}", redirectUri);
      // Complete logout URL

      String logoutUrl = oauth2UserLogoutUri + "?post_logout_redirect_uri=" + redirectUri + "&id_token_hint="
          + ((OidcUser) authentication.getPrincipal()).getIdToken().getTokenValue();


      // Do logout by redirecting to Keycloak logout
      LOG.info("Redirecting to logout URL {}", logoutUrl);
      LOG.info("Request {}", request);
      LOG.info("Response {}", response);

      redirectStrategy.sendRedirect(request, response, logoutUrl);
    }
  }



}
