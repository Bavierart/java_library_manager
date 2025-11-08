package view.factory;

import controller.AppController;
import view.*;
import view.gui.*;

/**
 * Fábrica para criar a interface de usuário baseada em Swing (GUI).
 */
public final class GuiFactory extends AbstractFactory {

    @Override
    public BookView createBookView() {
        return new BookViewGui();
    }

    @Override
    public ShelfView createShelfView() {
        return new ShelfViewGui();
    }

    @Override
    public CategoryView createCategoryView() {
        return new CategoryViewGui();
    }

    @Override
    public ReviewView createReviewView() {
        return new ReviewViewGui();
    }

    @Override
    public UserView createUserView() {
        return new UserViewGui();
    }

    @Override
    public MainView createMainView() {

        BookViewGui bv = (BookViewGui) createBookView();
        CategoryViewGui cv = (CategoryViewGui) createCategoryView();
        UserViewGui uv = (UserViewGui) createUserView();
        ReviewViewGui rv = (ReviewViewGui) createReviewView();
        ShelfViewGui sv = (ShelfViewGui) createShelfView();
        AppController ac = AppController.getController(); //

        // registra os observadores necessários para atualização de usuário
        ac.registerObserver(rv); //
        ac.registerObserver(sv); //

        MainViewGui mainView = new MainViewGui(bv, cv, uv, rv, sv, ac);

        // registra o main view como observador de user
        ac.registerObserver(mainView);

        return mainView;
    }
}