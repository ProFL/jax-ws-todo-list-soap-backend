package projeto_1.user.auth;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

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
