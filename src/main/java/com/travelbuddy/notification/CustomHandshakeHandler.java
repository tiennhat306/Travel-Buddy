package com.travelbuddy.notification;

import com.travelbuddy.auth.token.InvalidBearerTokenException;
import com.travelbuddy.auth.token.jwt.InvalidJWTException;
import com.travelbuddy.auth.token.jwt.JWTProcessor;
import com.travelbuddy.auth.token.jwt.VerifiedJWT;
import com.travelbuddy.user.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CustomHandshakeHandler implements HandshakeInterceptor {
    private final JWTProcessor jwtProcessor;
    private final UserService userService;

    private Integer validateAndExtractUserIdFromToken(String token) {
        try {
            VerifiedJWT verifiedJWT = jwtProcessor.getVerifiedJWT(token);
            String username = verifiedJWT.getUsername();
            return userService.getUserIdByEmailOrUsername(username);
        }catch (InvalidJWTException e) {
            throw new InvalidBearerTokenException(e.getMessage());
        }
    }

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        String token = request.getURI().getQuery();
        System.out.println(token);
        if (token != null) {
            int reuqestUserId = Integer.parseInt(token.substring(7, token.indexOf('&')));
            token = token.substring(token.indexOf('&') + 7);
            System.out.println(token);
            System.out.println(reuqestUserId);
            Integer userId = validateAndExtractUserIdFromToken(token); // Xác thực và lấy userId
            attributes.put("userId", userId);
            if (userId != null && userId.equals(reuqestUserId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    }
}
