package projeto_1.labels;

import jakarta.annotation.Resource;
import jakarta.jws.WebService;
import jakarta.xml.ws.WebServiceContext;
import projeto_1.auth.AuthModule;
import projeto_1.auth.exceptions.UnauthorizedException;
import projeto_1.exceptions.ForbiddenException;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.labels.beans.Label;
import projeto_1.labels.exceptions.DuplicateLabelException;
import projeto_1.labels.exceptions.LabelNotFoundException;
import projeto_1.labels_tasks.LabelsTasksRepository;
import projeto_1.task.beans.Task;
import projeto_1.user.beans.User;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
@WebService(endpointInterface = "projeto_1.labels.LabelService")
public class LabelServiceImpl implements LabelService {
    private final AuthModule authModule;
    private final LabelRepository repository;
    private final LabelsTasksRepository lblTskRepo;

    @Resource
    WebServiceContext ctx;

    @Inject
    public LabelServiceImpl(AuthModule authModule, LabelRepository repository, LabelsTasksRepository lblTskRepo) {
        this.authModule = authModule;
        this.repository = repository;
        this.lblTskRepo = lblTskRepo;
    }

    private Label getLabelIOwn(int id) throws UnauthorizedException, InternalServerErrorException, LabelNotFoundException, ForbiddenException {
        User me = this.authModule.getAuthenticatedUser(ctx.getMessageContext());
        Label label = this.repository.findById(id);
        if (label == null) {
            throw new LabelNotFoundException(id);
        }
        if (label.getOwnerId() != me.getId()) {
            throw new ForbiddenException("You do not own this label!");
        }
        return label;
    }

    @Override
    public Label[] findMyLabels() throws UnauthorizedException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(ctx.getMessageContext());
        return this.repository.findByOwnerId(me.getId());
    }

    @Override
    public Task[] findLabeledTasks(int id) throws UnauthorizedException, InternalServerErrorException, LabelNotFoundException, ForbiddenException {
        getLabelIOwn(id);
        return this.lblTskRepo.findByLabelId(id);
    }

    @Override
    public Label createLabel(String name, String color) throws UnauthorizedException, DuplicateLabelException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(ctx.getMessageContext());
        try {
            return this.repository.createOne(new Label(me.getId(), name, color));
        } catch (InternalServerErrorException ignore) {
            throw new DuplicateLabelException(name, color);
        }
    }

    @Override
    public Label replaceLabel(int id, String name, String color)
            throws DuplicateLabelException, UnauthorizedException, ForbiddenException, LabelNotFoundException,
            InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext());
        getLabelIOwn(id);
        try {
            return this.repository.replaceOne(new Label(id, me.getId(), name, color));
        } catch (InternalServerErrorException ignored) {
            throw new DuplicateLabelException(name, color);
        }
    }

    @Override
    public void deleteLabel(int id) throws UnauthorizedException, ForbiddenException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(this.ctx.getMessageContext());
        Label label = this.repository.findById(id);
        if (label == null) {
            return;
        }
        if (label.getOwnerId() != me.getId()) {
            throw new ForbiddenException("You do not own this label!");
        }
        this.repository.deleteOne(id);
    }
}
