package view.factory;

import controller.AppController;
import view.*;
import view.cli.*;

public final class TextUiFactory extends AbstractFactory{

    @Override
    public BookView createBookView() {
        return new BookViewText();
    }

    @Override
    public ShelfView createShelfView() {
        return new ShelfViewText();
    }

    @Override
    public CategoryView createCategoryView() {
        return new CategoryViewText();
    }

    @Override
    public ReviewView createReviewView() {
        return new ReviewViewText();
    }

    @Override
    public UserView createUserView() {
        return new UserViewText();
    }

    @Override
    public MainView createMainView() {
        BookViewText bv = (BookViewText) createBookView();
        CategoryViewText cv = (CategoryViewText) createCategoryView();
        UserViewText uv = (UserViewText) createUserView();
        ReviewViewText rv = (ReviewViewText) createReviewView();
        ShelfViewText sv = (ShelfViewText) createShelfView();
        AppController ac = AppController.getController();
        ac.registerObserver(rv);
        ac.registerObserver(sv);
        return new MainViewText(bv, cv, uv, rv, sv, ac);
    }

}