package projeto_1.task;

import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.jws.soap.SOAPBinding;
import jakarta.jws.soap.SOAPBinding.Style;
import projeto_1.auth.exceptions.UnauthorizedException;
import projeto_1.exceptions.ForbiddenException;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.labels.beans.Label;
import projeto_1.labels.exceptions.LabelNotFoundException;
import projeto_1.task.beans.Task;
import projeto_1.task.exceptions.AlreadyLabeledException;
import projeto_1.task.exceptions.TaskNotFoundException;

@WebService
@SOAPBinding(style = Style.RPC)
public interface TaskService {
    @WebMethod
    Task[] findMyTasks() throws UnauthorizedException, InternalServerErrorException;

    @WebMethod
    Label[] findTaskLabels(@WebParam(name = "id") int id) throws UnauthorizedException, InternalServerErrorException,
            TaskNotFoundException, ForbiddenException;

    @WebMethod
    Label[] labelTask(@WebParam(name = "id") int id, @WebParam(name="labelId") int labelId)
            throws InternalServerErrorException, ForbiddenException, UnauthorizedException, TaskNotFoundException,
            LabelNotFoundException, AlreadyLabeledException;

    @WebMethod
    Label[] unlabelTask(@WebParam(name = "id") int id, @WebParam(name="labelId") int labelId)
            throws TaskNotFoundException, InternalServerErrorException, ForbiddenException, UnauthorizedException,
            LabelNotFoundException;

    @WebMethod
    Task markIsCompleted(@WebParam(name = "id") int id, @WebParam(name = "completed") boolean completed)
            throws UnauthorizedException, ForbiddenException, TaskNotFoundException, InternalServerErrorException;

    @WebMethod
    Task createTask(@WebParam(name = "name") String name,
                    @WebParam(name = "description") String description)
            throws UnauthorizedException, InternalServerErrorException;

    @WebMethod
    Task replaceTask(@WebParam(name = "id") int id,
                     @WebParam(name = "name") String name,
                     @WebParam(name = "description") String description)
            throws UnauthorizedException, ForbiddenException, TaskNotFoundException, InternalServerErrorException;

    @WebMethod
    void deleteTask(@WebParam(name = "id") int id) throws UnauthorizedException, ForbiddenException,
            InternalServerErrorException;
}
