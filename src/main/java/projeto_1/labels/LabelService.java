package projeto_1.labels;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import projeto_1.auth.exceptions.UnauthorizedException;
import projeto_1.exceptions.ForbiddenException;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.labels.beans.Label;
import projeto_1.labels.exceptions.DuplicateLabelException;
import projeto_1.labels.exceptions.LabelNotFoundException;
import projeto_1.task.beans.Task;

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface LabelService {
    @WebMethod
    Label[] findMyLabels() throws UnauthorizedException, InternalServerErrorException;

    @WebMethod
    Task[] findLabeledTasks(@WebParam(name = "id") int id) throws UnauthorizedException, InternalServerErrorException, LabelNotFoundException, ForbiddenException;

    @WebMethod
    Label createLabel(@WebParam(name = "name") String name, @WebParam(name = "color") String color)
            throws UnauthorizedException, DuplicateLabelException, InternalServerErrorException;

    @WebMethod
    Label replaceLabel(
            @WebParam(name = "id") int id,
            @WebParam(name = "name") String name,
            @WebParam(name = "color") String color
    ) throws UnauthorizedException, DuplicateLabelException, InternalServerErrorException, ForbiddenException, LabelNotFoundException;

    @WebMethod
    void deleteLabel(int id) throws UnauthorizedException, ForbiddenException, InternalServerErrorException;
}
