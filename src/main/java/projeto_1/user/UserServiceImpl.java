package projeto_1.user;

import com.google.inject.Inject;
import jakarta.jws.WebService;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.beans.User;
import projeto_1.user.exceptions.DuplicateUserException;
import projeto_1.user.exceptions.UserNotFoundException;

import javax.inject.Singleton;

@Singleton
@WebService(endpointInterface = "projeto_1.user.UserService")
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Inject
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
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
    public User updateUser(int id, String name, String newPassword) throws UserNotFoundException, InternalServerErrorException {
        User user = this.repository.findById(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }
        user.setName(name);
        user.setPassword(newPassword);
        return this.repository.replaceOne(user);
    }
}