package projeto_1.user;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.beans.User;
import projeto_1.user.exceptions.DuplicateUserException;
import projeto_1.user.exceptions.UserNotFoundException;

@WebService
@SOAPBinding(style = Style.RPC)
public interface UserService {
        @WebMethod
        User createUser(@WebParam(name = "name") String name, @WebParam(name = "email") String email,
                        @WebParam(name = "password") String password)
                        throws DuplicateUserException, InternalServerErrorException;

        @WebMethod
        User updateUser(@WebParam(name = "id") int id, @WebParam(name = "name") String name,
                        @WebParam(name = "newPassword") String password)
                        throws UserNotFoundException, InternalServerErrorException;
}