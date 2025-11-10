package view.factory;

import controller.AppController;
import view.*;

public abstract class AbstractFactory {

    private final AppController controller = AppController.getController();

    /** Method that returns the app controller singleton (intermediary) **/
    public AppController getController() {
        return controller;
    }

    public abstract BookView createBookView();
    public abstract ShelfView createShelfView();
    public abstract CategoryView createCategoryView();
    public abstract ReviewView createReviewView();
    public abstract MainView createMainView();
    public abstract UserView createUserView();
}
