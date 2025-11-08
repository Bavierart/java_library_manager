package view.factory;

import controller.AppController;
import view.*;
import view.gui.*; // Importa as futuras classes da GUI

/**
 * Fábrica para criar a interface de usuário baseada em Swing (GUI).
 */
public final class GuiFactory extends AbstractFactory {

    @Override
    public BookView createBookView() {
        // Assume que view.gui.BookViewGui existe e implementa BookView
        return new BookViewGui();
    }

    @Override
    public ShelfView createShelfView() {
        // Assume que view.gui.ShelfViewGui existe e implementa ShelfView
        return new ShelfViewGui();
    }

    @Override
    public CategoryView createCategoryView() {
        // Assume que view.gui.CategoryViewGui existe e implementa CategoryView
        return new CategoryViewGui();
    }

    @Override
    public ReviewView createReviewView() {
        // Assume que view.gui.ReviewViewGui existe e implementa ReviewView
        return new ReviewViewGui();
    }

    @Override
    public UserView createUserView() {
        // Assume que view.gui.UserViewGui existe e implementa UserView
        return new UserViewGui();
    }

    @Override
    public MainView createMainView() {
        // 1. Cria instâncias de todas as sub-views (assim como a TextUiFactory faz)
        //    Fazendo o cast para os tipos concretos da GUI, espelhando o
        BookViewGui bv = (BookViewGui) createBookView();
        CategoryViewGui cv = (CategoryViewGui) createCategoryView();
        UserViewGui uv = (UserViewGui) createUserView();
        ReviewViewGui rv = (ReviewViewGui) createReviewView();
        ShelfViewGui sv = (ShelfViewGui) createShelfView();

        // 2. Obtém o controlador principal
        AppController ac = AppController.getController(); //

        // 3. Registra os observadores de usuário (espelhando o )
        //    ReviewViewGui e ShelfViewGui devem implementar UserObserver
        ac.registerObserver(rv); //
        ac.registerObserver(sv); //

        // 4. Cria a MainViewGui
        MainViewGui mainView = new MainViewGui(bv, cv, uv, rv, sv, ac);

        // 5. A MainViewGui também precisa saber sobre mudanças de usuário
        //    para atualizar seu status, então a registramos também.
        ac.registerObserver(mainView);

        return mainView;
    }
}