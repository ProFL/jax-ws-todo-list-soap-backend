package projeto_1.user.auth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;
import projeto_1.config.ConfigProvider;
import projeto_1.user.UserRepository;
import projeto_1.user.auth.exceptions.UnauthorizedException;
import projeto_1.user.beans.Token;
import projeto_1.user.beans.User;

import javax.inject.Singleton;
import java.util.LinkedList;
import java.util.Map;

@Singleton
public class TokenModule extends AbstractModule {
    private final String issuer = "todo-list";
    private final Algorithm algorithm;
    private final JWTVerifier verifier;

    private final UserRepository userRepo;

    @Inject
    public TokenModule(@ConfigProvider.SecretKey String secretKey, UserRepository userRepo) {
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

    public User getAuthenticatedUser(WebServiceContext ctx) throws UnauthorizedException {
        try {
            Map headers = (Map) ctx.getMessageContext().get(MessageContext.HTTP_REQUEST_HEADERS);
            String authorization = ((LinkedList<String>) headers.get("Authorization")).get(0);
            System.out.println(authorization);
            if (authorization == null) {
                throw new UnauthorizedException();
            }
            String token = authorization.substring(authorization.indexOf(' ') + 1);
            try {
                DecodedJWT decoded = this.verifier.verify(token);
                User user = this.userRepo.findById(Integer.parseInt(decoded.getSubject()));
                if (user == null) {
                    throw new UnauthorizedException();
                }
                return user;
            } catch (JWTVerificationException ignored) {
                throw new UnauthorizedException();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        throw new UnauthorizedException();
    }
}
