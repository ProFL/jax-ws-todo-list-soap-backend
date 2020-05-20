package projeto_1.task;

import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import projeto_1.exceptions.ForbiddenException;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.task.beans.Task;
import projeto_1.user.auth.AuthModule;
import projeto_1.user.auth.exceptions.UnauthorizedException;
import projeto_1.user.beans.User;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@WebService(endpointInterface = "projeto_1.task.TaskService")
public class TaskServiceImpl implements TaskService {
    private final AuthModule authModule;
    private final TaskRepository repo;

    @Resource
    WebServiceContext ctx;

    @Inject
    public TaskServiceImpl(AuthModule authModule, TaskRepository taskRepository) {
        this.authModule = authModule;
        this.repo = taskRepository;
    }

    @Override
    public Task[] findMyTasks() throws UnauthorizedException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext());
        return this.repo.findByOwnerId(me.getId());
    }

    @Override
    public Task markIsCompleted(int id, boolean completed)
            throws UnauthorizedException, ForbiddenException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext());
        Task task = this.repo.findById(id);
        if (task.getOwnerId() != me.getId()) {
            throw new ForbiddenException("You do not own this task!");
        }
        task.setCompleted(completed);
        return this.repo.replaceOne(task);
    }

    @Override
    public Task createTask(String name, String description) throws UnauthorizedException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext());
        return this.repo.createOne(new Task(me.getId(), name, description));
    }

    @Override
    public Task replaceTask(int id, String name, String description) throws UnauthorizedException, ForbiddenException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext());
        Task task = this.repo.findById(id);
        if (task.getOwnerId() != me.getId()) {
            throw new ForbiddenException("You do not own this task!");
        }
        task.setName(name);
        task.setDescription(description);
        return this.repo.replaceOne(task);
    }

    @Override
    public void deleteTask(int id) throws UnauthorizedException, ForbiddenException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext());
        Task task = this.repo.findById(id);
        if (task.getOwnerId() != me.getId()) {
            throw new ForbiddenException("You do not own this task!");
        }
        this.repo.deleteOne(id);
    }
}
