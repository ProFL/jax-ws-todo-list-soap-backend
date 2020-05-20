package projeto_1.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import jakarta.xml.ws.handler.MessageContext;
import projeto_1.config.ConfigProvider;
import projeto_1.user.UserRepository;
import projeto_1.auth.beans.Token;
import projeto_1.auth.exceptions.UnauthorizedException;
import projeto_1.user.beans.User;

import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.Map;

@Singleton
public class AuthModule extends AbstractModule {
    private final String issuer = "todo-list";
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    private final UserRepository userRepo;

    @Inject
    public AuthModule(@ConfigProvider.SecretKey String secretKey, UserRepository userRepo) {
        this.algorithm = Algorithm.HMAC256(secretKey);
        this.verifier = JWT.require(this.algorithm).withIssuer(this.issuer).build();
        this.userRepo = userRepo;
    }

    public Token signToken(String subject) {
        return new Token(JWT.create()
                .withIssuer(this.issuer)
                .withSubject(subject)
                .sign(this.algorithm));
    }

    public User getAuthenticatedUser(MessageContext ctx) throws UnauthorizedException {
        try {
            Map headers = (Map) ctx.get(MessageContext.HTTP_REQUEST_HEADERS);
            LinkedList<String> authorizationHeaders = (LinkedList<String>) headers.get("Authorization");
            if (authorizationHeaders == null) {
                throw new UnauthorizedException("Authorization header is missing");
            }
            String authorization = authorizationHeaders.get(0);
            if (authorization == null) {
                throw new UnauthorizedException("Authorization header value is missing");
            }
            String authType = "JWT ";
            if (!authorization.startsWith(authType)) {
                throw new UnauthorizedException("Wrong authorization type, JWT expected");
            }
            String token = authorization.substring(authType.length());
            try {
                DecodedJWT decoded = this.verifier.verify(token);
                User user = this.userRepo.findById(Integer.parseInt(decoded.getSubject()));
                if (user == null) {
                    throw new UnauthorizedException("User no longer exists");
                }
                return user;
            } catch (JWTVerificationException ignored) {
                throw new UnauthorizedException("Bad token provided");
            }
        } catch (UnauthorizedException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new UnauthorizedException();
    }
}
