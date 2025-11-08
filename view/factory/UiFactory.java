package view.factory;

import view.*;

public interface UiFactory {
    BookView createBookView();
    ShelfView createShelfView();
    CategoryView createCategoryView();
    ReviewView createReviewView();
    MainView createMainView();
    UserView createUserView();
}
