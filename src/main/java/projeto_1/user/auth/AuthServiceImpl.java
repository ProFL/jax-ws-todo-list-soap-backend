package projeto_1.user.auth;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.google.inject.Inject;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.UserRepository;
import projeto_1.user.auth.exceptions.PasswordMismatchException;
import projeto_1.user.auth.exceptions.UnauthorizedException;
import projeto_1.user.beans.Token;
import projeto_1.user.beans.User;
import projeto_1.user.exceptions.UserNotFoundException;

@WebService(endpointInterface = "projeto_1.user.auth.AuthService")
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepo;
    private final TokenModule tokenModule;

    @Resource
    WebServiceContext ctx;

    @Inject
    public AuthServiceImpl(UserRepository userRepo, TokenModule tokenModule) {
        this.userRepo = userRepo;
        this.tokenModule = tokenModule;
    }

    @Override
    public User whoAmI() throws UnauthorizedException {
        return this.tokenModule.getAuthenticatedUser(this.ctx);
    }

    @Override
    public Token signIn(String email, String password)
            throws UserNotFoundException, PasswordMismatchException, InternalServerErrorException {
        User user = userRepo.findByEmail(email);

        if (user == null) {
            throw new UserNotFoundException(email);
        }

        if (BCrypt.verifyer().verify(password.toCharArray(), user.getPassword().toCharArray()).verified) {
            try {
                return this.tokenModule.signToken(String.valueOf(user.getId()));
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new InternalServerErrorException("Failed to sign token");
            }
        }

        throw new PasswordMismatchException(email);
    }
}