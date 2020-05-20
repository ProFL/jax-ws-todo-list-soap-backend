package projeto_1.user;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.auth.exceptions.UnauthorizedException;
import projeto_1.user.beans.User;
import projeto_1.user.exceptions.DuplicateUserException;

@WebService
@SOAPBinding(style = Style.RPC)
public interface UserService {
    @WebMethod
    User createUser(@WebParam(name = "name") String name,
                    @WebParam(name = "email") String email,
                    @WebParam(name = "password") String password)
            throws DuplicateUserException, InternalServerErrorException;

    @WebMethod
    User replaceUser(@WebParam(name = "name") String name,
                     @WebParam(name = "email") String email,
                     @WebParam(name = "password") String password)
            throws UnauthorizedException, DuplicateUserException, InternalServerErrorException;
}