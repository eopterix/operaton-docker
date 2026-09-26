package org.operaton.bpm.extension.keycloak.run.plugin;

import java.util.Collections;

import jakarta.inject.Inject;

import org.operaton.bpm.webapp.impl.security.auth.ContainerBasedAuthenticationFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.firewall.HttpFirewall;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.web.context.request.RequestContextListener;
import org.springframework.web.filter.ForwardedHeaderFilter;

//#neu :
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import java.util.Arrays;
import java.util.List;


//# whatever
import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Operaton Web application SSO configuration for usage with KeycloakIdentityProviderPlugin.
 */

        // .headers(headers -> headers
        //     .contentSecurityPolicy(csp -> csp.policyDirectives(
        //        "default-src 'self'; " + 
        //        "connect-src 'self' http://localhost:8081")
        //    )
        //)
                // pathPattern.matcher("/api/*/*/*/*/logout")




@ConditionalOnMissingClass("org.springframework.test.context.junit.jupiter.SpringExtension")
@EnableWebSecurity
@Configuration
public class WebAppSecurityConfig {

  @Inject
  private KeycloakLogoutHandler keycloakLogoutHandler;

/*
 @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().configurationSource(corsConfigurationSource());
    }

    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowOrigins = Arrays.asList("*");
        configuration.setAllowedOrigins(allowOrigins);
        configuration.setAllowedMethods(Collections.singletonList("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
*/



  @Bean
  public FilterRegistrationBean processCorsFilter() {
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    CorsConfiguration config = new CorsConfiguration();
    //config.setAllowCredentials(true);
    config.addAllowedOrigin("*");
    config.addAllowedHeader("*");
    config.addAllowedMethod("*");
    source.registerCorsConfiguration("/**", config);

    FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
    bean.setOrder(0);
    return bean;
  }





@Bean
public FilterRegistrationBean<CorsFilter> platformCorsFilter() {
    CorsConfiguration config = new CorsConfiguration();
    // Erlaube Keycloak und die eigene App explizit
    config.setAllowedOrigins(Arrays.asList("http://localhost:8081", "http://localhost:8080"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
    config.setAllowedHeaders(Arrays.asList("*"));
    config.setExposedHeaders(Arrays.asList("Location", "Authorization"));
    config.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    // Gilt global für absolut alle Pfade (inklusive des versteckten Logout-Endpunkts)
    source.registerCorsConfiguration("/**", config);

    FilterRegistrationBean<CorsFilter> bean = new FilterRegistrationBean<>(new CorsFilter(source));
    // HIGHEST_PRECEDENCE sorgt dafür, dass dieser Filter noch VOR allen Operaton- und Spring-Security-Filtern läuft!
    bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return bean;
}





//    @Bean
//public FilterRegistrationBean<jakarta.servlet.Filter> csrfPreventionFilter() {
//      FilterRegistrationBean<jakarta.servlet.Filter> registration = new FilterRegistrationBean<>();
//      // Wir holen uns den Filter-Namen, den Operaton intern registriert
//      registration.setName("CsrfPreventionFilter");
//      // Indem wir hier die Registrierung überschreiben und 'setEnabled(false)' setzen, 
//      // wird der Filter von Spring Boot NIEMALS gestartet:
//      registration.setEnabled(false);
//      return registration;
//  }














  @Bean
  @Order(2)
  public SecurityFilterChain httpSecurity(HttpSecurity http) throws Exception {
    PathPatternRequestMatcher.Builder pathPattern = PathPatternRequestMatcher.withDefaults();
    return http
        //.csrf(csrf -> csrf.ignoringRequestMatchers(pathPattern.matcher("/auth/**"),pathPattern.matcher("/app/**"), pathPattern.matcher("/api/**"), pathPattern.matcher("/engine-rest/**")))
        //.csrf(csrf -> csrf.ignoringRequestMatchers(pathPattern.matcher("/api/**"), pathPattern.matcher("/engine-rest/**")))
        .csrf(csrf -> csrf.disable())
   
        .headers(headers -> headers
          .contentSecurityPolicy(csp -> csp
                    .policyDirectives(
                       "default-src 'self' http://localhost:8081; " +
                        "connect-src 'self' http://localhost:8081; " +
                        "form-action 'self' http://localhost:8081; " +
                        "script-src 'self' 'unsafe-inline'; " +
                        "style-src 'self' 'unsafe-inline'; " +
                        "frame-src 'self' http://localhost:8081; " +
                        "img-src 'self' data: http://localhost:8081; " +
                        "font-src 'self' http://localhost:8081"
                    )
//            .policyDirectives("default-src * 'unsafe-inline' 'unsafe-eval' data: blob:")
//            .policyDirectives(
//                "default-src 'self'; " +
//                "connect-src 'self' http://localhost:8081; " +
//                "script-src 'self' http://localhost:8081; " +
//                "form-action 'self' http://localhost:8081; "
//                "frame-src 'self' http://localhost:8081; " +
//                "img-src 'self' data:; " +
//                "style-src 'self' 'unsafe-inline'; " +
//                "script-src 'self' 'unsafe-inline' 'unsafe-eval'"
//            )
          )
        )
        .authorizeHttpRequests(authorize -> authorize.requestMatchers(pathPattern.matcher("/assets/**"), pathPattern.matcher("/app/**"),
            pathPattern.matcher("/api/**"), pathPattern.matcher("/lib/**")).authenticated().anyRequest().permitAll())
        .oauth2Login(withDefaults())
        
        
        .logout(logout -> logout.logoutRequestMatcher(pathPattern.matcher("/**/logout"))
            .logoutSuccessHandler(keycloakLogoutHandler))
        .build();
  }
  // /api/admin/auth/user/default/logout

  @SuppressWarnings({ "rawtypes", "unchecked" })
  @Bean
  public FilterRegistrationBean containerBasedAuthenticationFilter() {

    FilterRegistrationBean filterRegistration = new FilterRegistrationBean();
    filterRegistration.setFilter(new ContainerBasedAuthenticationFilter());
    filterRegistration.setInitParameters(Collections.singletonMap("authentication-provider",
        "org.operaton.bpm.extension.keycloak.run.plugin.KeycloakAuthenticationProvider"));
    filterRegistration.setOrder(201); // make sure the filter is registered after the Spring Security Filter Chain
    filterRegistration.addUrlPatterns("/app/*");
    return filterRegistration;
  }

  // The ForwardedHeaderFilter is required to correctly assemble the redirect URL for OAUth2 login.
  // Without the filter, Spring generates an HTTP URL even though the container route is accessed through HTTPS.
  @Bean
  public FilterRegistrationBean<ForwardedHeaderFilter> forwardedHeaderFilter() {
    FilterRegistrationBean<ForwardedHeaderFilter> filterRegistrationBean = new FilterRegistrationBean<>();
    filterRegistrationBean.setFilter(new ForwardedHeaderFilter());
    filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
    return filterRegistrationBean;
  }

  @Bean
  @Order(0)
  public RequestContextListener requestContextListener() {
    return new RequestContextListener();
  }

  // Modify firewall in order to allow request details for child groups
  @Bean
  public HttpFirewall getHttpFirewall() {
    StrictHttpFirewall strictHttpFirewall = new StrictHttpFirewall();
    strictHttpFirewall.setAllowUrlEncodedPercent(true);
    strictHttpFirewall.setAllowUrlEncodedSlash(true);
    return strictHttpFirewall;
  }
}