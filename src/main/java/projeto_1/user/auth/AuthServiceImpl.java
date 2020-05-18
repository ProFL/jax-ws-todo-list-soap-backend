package projeto_1.user.auth;

import javax.jws.WebService;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.UserServiceImpl;
import projeto_1.user.auth.exceptions.PasswordMismatchException;
import projeto_1.user.beans.Token;
import projeto_1.user.beans.User;
import projeto_1.user.exceptions.UserNotFoundException;

@WebService(endpointInterface = "projeto_1.user.auth.AuthService")
public class AuthServiceImpl implements AuthService {
    private final UserServiceImpl userService;

    public AuthServiceImpl(UserServiceImpl userService) {
        this.userService = userService;
    }

    @Override
    public Token createToken(String email, String password)
            throws UserNotFoundException, PasswordMismatchException, InternalServerErrorException {
        User user = this.userService.findUser(email);

        if (user == null) {
            throw new UserNotFoundException(email);
        }

        if (password == user.getPassword()) {
            try {
                Algorithm algorithm = Algorithm.HMAC256("safe-secret-key");
                String token = JWT.create().withIssuer("todo-list").withSubject(String.valueOf(user.getId()))
                        .sign(algorithm);
                return new Token(token);
            } catch (Exception exception) {
                exception.printStackTrace();
                throw new InternalServerErrorException("Falha na criação do token");
            }
        }

        throw new PasswordMismatchException(email);
    }
}