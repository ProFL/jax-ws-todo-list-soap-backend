package projeto_1.user.auth;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.user.auth.exceptions.PasswordMismatchException;
import projeto_1.user.beans.Token;
import projeto_1.user.exceptions.UserNotFoundException;

@WebService
@SOAPBinding(style = Style.RPC)
public interface AuthService {
    @WebMethod
    Token createToken(@WebParam(name = "email") String email, @WebParam(name = "password") String password)
            throws UserNotFoundException, PasswordMismatchException, InternalServerErrorException;
}
