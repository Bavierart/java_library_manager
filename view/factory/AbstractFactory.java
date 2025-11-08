package view.factory;

import controller.AppController;

public abstract class AbstractFactory implements UiFactory {

    private final AppController controller = AppController.getController();

    /** Method that returns the app controller singleton (intermediary) **/
    public AppController getController() {
        return controller;
    }
}
