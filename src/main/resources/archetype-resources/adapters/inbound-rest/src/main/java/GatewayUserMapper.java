package ${package}.adapters.inbound.rest.security;

import ${package}.adapters.inbound.rest.dto.GatewayUserResponse;
import org.springframework.security.oauth2.jwt.Jwt;

public final class GatewayUserMapper {

    private GatewayUserMapper() {
    }

    public static GatewayUserResponse toResponse(Jwt jwt) {
        if (jwt == null) {
            return new GatewayUserResponse("", "", "", "");
        }
        return new GatewayUserResponse(
                jwt.getSubject(),
                claim(jwt, "preferred_username", jwt.getSubject()),
                claim(jwt, "email", ""),
                claim(jwt, "name", claim(jwt, "preferred_username", jwt.getSubject()))
        );
    }

    private static String claim(Jwt jwt, String name, String fallback) {
        Object value = jwt.getClaims().get(name);
        if (value instanceof String text && !text.isBlank()) {
            return text;
        }
        return fallback;
    }
}
