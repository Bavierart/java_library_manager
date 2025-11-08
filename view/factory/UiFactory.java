package view.factory;

import view.*;

/** Interface genérica para criação das views **/
public interface UiFactory {
    BookView createBookView();
    ShelfView createShelfView();
    CategoryView createCategoryView();
    ReviewView createReviewView();
    MainView createMainView();
    UserView createUserView();
}
