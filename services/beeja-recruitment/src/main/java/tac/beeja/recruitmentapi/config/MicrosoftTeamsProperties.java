package tac.beeja.recruitmentapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "microsoft.teams")
@Getter
@Setter
public class MicrosoftTeamsProperties {

  private String clientId;
  private String clientSecret;
  private String tenantId;
  private String serviceAccountEmail;
  private String authority;
  private String scope;
}
