package projeto_1.labels;

import jakarta.annotation.Resource;
import jakarta.xml.ws.WebServiceContext;
import projeto_1.auth.AuthModule;
import projeto_1.auth.exceptions.UnauthorizedException;
import projeto_1.exceptions.ForbiddenException;
import projeto_1.exceptions.InternalServerErrorException;
import projeto_1.labels.beans.Label;
import projeto_1.labels.exceptions.DuplicateLabelException;
import projeto_1.labels.exceptions.LabelNotFoundException;
import projeto_1.task.beans.Task;
import projeto_1.user.beans.User;

import javax.inject.Inject;

public class LabelServiceImpl implements LabelService {
    private final AuthModule authModule;
    private final LabelRepository repository;

    @Resource
    WebServiceContext ctx;

    @Inject
    public LabelServiceImpl(AuthModule authModule, LabelRepository repository) {
        this.authModule = authModule;
        this.repository = repository;
    }

    @Override
    public Label[] findMyLabels() throws UnauthorizedException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(ctx.getMessageContext());
        return this.repository.findByOwnerId(me.getId());
    }

    @Override
    public Task[] findLabeledTasks(int id) throws UnauthorizedException, InternalServerErrorException {
        User me = this.authModule.getAuthenticatedUser(ctx.getMessageContext());
        // TODO
        throw new InternalServerErrorException("Method not yet implemented");
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
        User me = this.authModule.getAuthenticatedUser(ctx.getMessageContext());
        Label label = this.repository.findById(id);
        if (label == null) {
            throw new LabelNotFoundException(id);
        }
        if (label.getOwnerId() != me.getId()) {
            throw new ForbiddenException("You do not own this label!");
        }
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
