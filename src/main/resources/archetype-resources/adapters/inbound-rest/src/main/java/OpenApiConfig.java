#set($d = '$')
package ${package}.adapters.inbound.rest;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.Scopes;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    OpenAPI openAPI(
            @Value("${d}{springdoc.oauth2.authorization-url}") String authorizationUrl,
            @Value("${d}{springdoc.oauth2.token-url}") String tokenUrl
    ) {
        return new OpenAPI()
                .info(new Info()
                        .title("${rootArtifactId} API")
                        .version("${version}")
                        .description("API for ${rootArtifactId}. Use the Authorize button to log in with ZITADEL when calling secured endpoints from Swagger UI."))
                .components(new Components()
                        .addSecuritySchemes("zitadel", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .description("ZITADEL OAuth2 authorization-code login. spring-gateway-base relays the same user token to this service.")
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(authorizationUrl)
                                                .tokenUrl(tokenUrl)
                                                .scopes(new Scopes()
                                                        .addString("openid", "OpenID Connect")
                                                        .addString("profile", "Profile")
                                                        .addString("email", "Email"))))));
    }
}
