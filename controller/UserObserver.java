package controller;

import model.User;

public interface UserObserver {
    void onUserChanged(User newUser);
    User getUser();
}
