package projeto_1.task;

import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import projeto_1.auth.AuthModule;
import projeto_1.auth.exceptions.UnauthorizedException;
import projeto_1.exceptions.ForbiddenException;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.labels.LabelRepository;
import projeto_1.labels.beans.Label;
import projeto_1.labels.exceptions.LabelNotFoundException;
import projeto_1.labels_tasks.LabelsTasksRepository;
import projeto_1.labels_tasks.beans.LabelsTasks;
import projeto_1.task.beans.Task;
import projeto_1.task.exceptions.AlreadyLabeledException;
import projeto_1.task.exceptions.TaskNotFoundException;
import projeto_1.user.beans.User;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.sql.SQLException;

@Singleton
@WebService(endpointInterface = "projeto_1.task.TaskService")
public class TaskServiceImpl implements TaskService {
    private final AuthModule authModule;
    private final TaskRepository repo;
    private final LabelRepository lblRepo;
    private final LabelsTasksRepository lblTskRepo;

    @Resource
    WebServiceContext ctx;

    @Inject
    public TaskServiceImpl(AuthModule authModule, TaskRepository taskRepository, LabelRepository lblRepo, LabelsTasksRepository lblTskRepo) {
        this.authModule = authModule;
        this.repo = taskRepository;
        this.lblRepo = lblRepo;
        this.lblTskRepo = lblTskRepo;
    }

    private Label isLabelOwner(int userId, int labelId)
            throws InternalServerErrorException, ForbiddenException, LabelNotFoundException {
        Label label = this.lblRepo.findById(labelId);
        if (label == null) {
            throw new LabelNotFoundException(labelId);
        }
        if (userId != label.getId()) {
            throw new ForbiddenException("Label not owned");
        }
        return label;
    }

    private Task isTaskOwner(int userId, int taskId)
            throws InternalServerErrorException, TaskNotFoundException, ForbiddenException {
        Task task = this.repo.findById(taskId);
        if (task == null) {
            throw new TaskNotFoundException(taskId);
        }
        if (userId != task.getOwnerId()) {
            throw new ForbiddenException("Task not owned");
        }
        return task;
    }

    @Override
    public Task[] findMyTasks() throws UnauthorizedException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext());
        return this.repo.findByOwnerId(me.getId());
    }

    @Override
    public Label[] findTaskLabels(int id) throws UnauthorizedException, ForbiddenException, TaskNotFoundException,
            InternalServerErrorException {
        this.isTaskOwner(this.authModule.getAuthenticatedUser(this.ctx.getMessageContext()).getId(), id);
        return this.lblTskRepo.findByTaskId(id);
    }

    @Override
    public Label[] labelTask(int id, int labelId) throws InternalServerErrorException, ForbiddenException,
            UnauthorizedException, TaskNotFoundException, LabelNotFoundException, AlreadyLabeledException {
        int userId = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext()).getId();
        this.isTaskOwner(userId, id);
        this.isLabelOwner(userId, labelId);
        try {
            this.lblTskRepo.createOne(new LabelsTasks(id, labelId));
        } catch (InternalServerErrorException e) {
            if (e.innerException instanceof SQLException) {
                throw new AlreadyLabeledException(id, labelId);
            }
            throw e;
        }
        return this.lblTskRepo.findByTaskId(id);
    }

    @Override
    public Label[] unlabelTask(int id, int labelId) throws TaskNotFoundException, InternalServerErrorException,
            ForbiddenException, UnauthorizedException, LabelNotFoundException {
        int userId = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext()).getId();
        this.isTaskOwner(userId, id);
        this.isLabelOwner(userId, labelId);
        this.lblTskRepo.deleteOne(new LabelsTasks(id, labelId));
        return this.lblTskRepo.findByTaskId(id);
    }

    @Override
    public Task markIsCompleted(int id, boolean completed)
            throws UnauthorizedException, ForbiddenException, TaskNotFoundException, InternalServerErrorException {
        Task task = isTaskOwner(this.authModule.getAuthenticatedUser(this.ctx.getMessageContext()).getId(), id);
        task.setCompleted(completed);
        return this.repo.replaceOne(task);
    }

    @Override
    public Task createTask(String name, String description) throws UnauthorizedException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext());
        return this.repo.createOne(new Task(me.getId(), name, description));
    }

    @Override
    public Task replaceTask(int id, String name, String description)
            throws UnauthorizedException, ForbiddenException, TaskNotFoundException, InternalServerErrorException {
        Task task = isTaskOwner(this.authModule.getAuthenticatedUser(this.ctx.getMessageContext()).getId(), id);
        task.setName(name);
        task.setDescription(description);
        return this.repo.replaceOne(task);
    }

    @Override
    public void deleteTask(int id) throws UnauthorizedException, ForbiddenException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext());
        Task task = this.repo.findById(id);
        if (task == null) {
            return;
        }
        if (task.getOwnerId() != me.getId()) {
            throw new ForbiddenException("You do not own this task!");
        }
        this.repo.deleteOne(id);
    }
}
