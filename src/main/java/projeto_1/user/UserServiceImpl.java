package projeto_1.user;

import jakarta.jws.WebService;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.beans.User;
import projeto_1.user.exceptions.DuplicateUserException;
import projeto_1.user.exceptions.UserNotFoundException;

@WebService(endpointInterface = "projeto_1.user.UserService")
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public User createUser(String name, String email, String password)
            throws DuplicateUserException, InternalServerErrorException {
        User existingUser = null;
        try {
            existingUser = this.repository.findByEmail(email);
        } catch (UserNotFoundException e) {
            try {
                return this.repository.createOne(new User(name, email, password));
            } catch (UserNotFoundException e1) {
                e1.printStackTrace();
                throw new InternalServerErrorException("Created user was not found");
            }
        }
        if (existingUser != null) {
            throw new DuplicateUserException(email);
        }
        throw new InternalServerErrorException("Unexpected user creation result");
    }

    @Override
    public User updateUser(int id, String name, String newPassword)
            throws UserNotFoundException, InternalServerErrorException {
        User user = this.repository.findById(id);
        user.setName(name);
        user.setPassword(newPassword);
        return this.repository.replaceOne(user);
    }
}