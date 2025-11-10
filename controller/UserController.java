package controller;

import model.User;

import java.util.ArrayList;

public final class UserController extends GlobalElementsController<User> {
    public UserController() {
        super( "data/users.dat");
    }

    @Override
    public void saveAll() {
        // 2. Salve a LISTA de usuários que está DENTRO do crudAux.
        DataManager.save(crudAux.getAllObjects(), "users.dat");
    }

    @Override
    public void loadAll() {
        ArrayList<User> loadedList = DataManager.load("users.dat");

        if (loadedList == null) {
            loadedList = new ArrayList<>();
        }

        crudAux.setObjects(loadedList);


        int maxId = 0;
        for (User u : loadedList) {
            if (u.getId() > maxId) maxId = u.getId();
        }
        User.resetIdCounter(maxId + 1);
    }

    @Override
    protected User build(Object... args) {
        String username = (String) args[0];
        String password = (String) args[1];

        return new User.Builder()
                .setUsername(username)
                .setPassword(password)
                .build();
    }

    @Override
    protected void applyUpdate(User user, Object... args) {
        String newUsername = (String) args[0];
        String newPassword = (String) args[1];

        if (newUsername != null) user.setUsername(newUsername);
        if (newPassword != null) user.setPassword(newPassword);
    }

    @Override
    protected void resetIdCounter(int nextId) {
        User.resetIdCounter(nextId);
    }

    // --- métodos específicos ---
    public User login(String username, String password) {
        return crudAux.getAllObjects().stream()
                .filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password))
                .findFirst()
                .orElse(null);
    }

    public boolean usernameExists(String username) {
        return crudAux.getAllObjects().stream()
                .anyMatch(u -> u.getUsername().equalsIgnoreCase(username));
    }
}
