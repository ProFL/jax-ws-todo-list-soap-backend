package projeto_1.user;

import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import projeto_1.user.beans.User;
import projeto_1.user.exceptions.DuplicateUserException;
import projeto_1.user.exceptions.UserNotFoundException;

@WebService(endpointInterface = "projeto_1.user.UserService")
public class UserServiceImpl implements UserService {
    private List<User> users = new ArrayList<>();

    public User findUser(int id) {
        for (User u : users) {
            if (id == u.getId())
                return u;
        }
        return null;
    }

    public User findUser(String email) {
        for (User u : users) {
            if (email == u.getEmail())
                return u;
        }
        return null;
    }

    @Override
    public User createUser(String name, String email, String password) throws DuplicateUserException {
        final User user = new User();
        user.setId(users.size());
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        if (users.contains(user)) {
            throw new DuplicateUserException(email);
        }

        this.users.add(user);
        return user;
    }

    @Override
    public User updateUser(int id, String name, String newPassword) throws UserNotFoundException {
        final User user = this.findUser(id);
        if (user == null) {
            throw new UserNotFoundException(id);
        }

        user.setName(name);
        user.setPassword(newPassword);
        users.set(id, user);

        return user;
    }
}