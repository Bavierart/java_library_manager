package controller;

import model.CrudObjectInterface;
import model.User;

public abstract class UserBoundElementsController<T extends CrudObjectInterface> implements Controller<T> {
    protected User user;

    protected UserBoundElementsController(User user) {
        this.user = user;
        new java.io.File("data").mkdirs();
    }

    @Override
    public void saveAll() {

    }

    @Override
    public void loadAll() {

    }

}
