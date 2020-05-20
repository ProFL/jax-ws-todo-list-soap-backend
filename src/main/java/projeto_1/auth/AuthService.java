package projeto_1.auth;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.auth.beans.Token;
import projeto_1.auth.exceptions.PasswordMismatchException;
import projeto_1.auth.exceptions.UnauthorizedException;
import projeto_1.user.beans.User;
import projeto_1.user.exceptions.UserNotFoundException;

@WebService
@SOAPBinding(style = Style.RPC)
public interface AuthService {
    @WebMethod
    User whoAmI() throws UnauthorizedException, InternalServerErrorException;

    @WebMethod
    Token signIn(@WebParam(name = "email") String email, @WebParam(name = "password") String password)
            throws UserNotFoundException, PasswordMismatchException, InternalServerErrorException;
}
