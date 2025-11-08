package controller;

import model.User;
import java.util.ArrayList;

public final class AppController {

    private final BookController bookController = new BookController();
    private final ReviewController reviewController = new ReviewController(null, bookController);
    private final ShelfController shelfController = new ShelfController(null);
    private final CategoryController categoryController = new CategoryController();
    private final UserController userController = new UserController();
    private User user;

    private transient final ArrayList<UserObserver> observers = new ArrayList<>();
    private static final AppController instance = new AppController();
    public static AppController getController() { return instance; }

    private AppController() {
        registerObserver(reviewController);
        registerObserver(shelfController);
    }

    public BookController books() { return bookController; }
    public ReviewController reviews() { return reviewController; }
    public ShelfController shelves() { return shelfController; }
    public CategoryController categories() { return categoryController; }
    public UserController user() { return userController; }

    public void setUser(User user) {
        this.user = user;
        notifyUserChange(user);
    }

    public User getUser() {
        return user;
    }

    public void registerObserver(UserObserver observer) {
        if (!observers.contains(observer))
            observers.add(observer);
    }

    public void unregisterObserver(UserObserver observer) {
        observers.remove(observer);
    }

    public void notifyUserChange(User newUser) {
        for (UserObserver obs : observers) {
            obs.onUserChanged(newUser);
        }
    }

    public void saveAll() {
        userController.saveAll();
        bookController.saveAll();
        reviewController.saveAll();
        categoryController.saveAll();
        shelfController.saveAll();
    }

    public void loadAll() {
        userController.loadAll();
        bookController.loadAll();
        reviewController.loadAll();
        categoryController.loadAll();
        shelfController.loadAll();
    }
}
