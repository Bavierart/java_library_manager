package view.factory;

import controller.AppController;

public abstract class AbstractFactory implements UiFactory {

    private final AppController controller = AppController.getController();

    public AppController getController() {
        return controller;
    }
}
