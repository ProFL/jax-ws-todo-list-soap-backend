package projeto_1.user;

import com.google.inject.Inject;
import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.auth.AuthModule;
import projeto_1.user.auth.exceptions.UnauthorizedException;
import projeto_1.user.beans.User;
import projeto_1.user.exceptions.DuplicateUserException;

import javax.inject.Singleton;

@Singleton
@WebService(endpointInterface = "projeto_1.user.UserService")
public class UserServiceImpl implements UserService {
    private final AuthModule authModule;
    private final UserRepository repository;

    @Resource
    WebServiceContext ctx;

    @Inject
    public UserServiceImpl(UserRepository repository, AuthModule authModule) {
        this.repository = repository;
        this.authModule = authModule;
    }

    @Override
    public User createUser(String name, String email, String password)
            throws DuplicateUserException, InternalServerErrorException {
        User existingUser = this.repository.findByEmail(email);
        if (existingUser != null) {
            throw new DuplicateUserException(email);
        }
        return this.repository.createOne(new User(name, email, password));
    }

    @Override
    public User replaceUser(String name, String email, String password)
            throws UnauthorizedException, DuplicateUserException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(ctx.getMessageContext());
        int myId = me.getId();

        User emailUser = this.repository.findByEmail(email);
        if (emailUser != null && myId != emailUser.getId()) {
            throw new DuplicateUserException(email);
        }

        User newMe = new User(myId, name, email, password);
        return this.repository.replaceOne(newMe);
    }
}