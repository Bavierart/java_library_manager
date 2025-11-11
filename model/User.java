package model;

import java.io.Serializable;
import java.util.ArrayList;

public final class User implements Serializable, CrudObjectInterface {
    private static final long serialVersionUID = 1L;
    private static int nextId = 1;

    private final int id;
    private String username;
    private String password;
    private final ArrayList<Shelf> shelves = new ArrayList<>();

    private User(int id, String username, String password) {
        this.id = id;        setUsername(username);
        setPassword(password);
    }

    // === Builder ===
    public static class Builder {
        private String username = "SemNome";
        private String password = "";
        private Integer id = null;

        public Builder setUsername(String username) {
            this.username = (username == null || username.isBlank()) ? "SemNome" : username.trim();
            return this;
        }

        public Builder setPassword(String password) {
            this.password = (password == null) ? "" : password;
            return this;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public User build() {
            int finalId = (id != null) ? id : nextId++;
            return new User(finalId, username, password);
        }
    }

    public static void resetIdCounter(int next) { nextId = next; }
    public static int getNextId() { return nextId; }
    public int getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public ArrayList<Shelf> getShelves() { return shelves; }

    public void setUsername(String username) {
        this.username = (username == null || username.isBlank()) ? "SemNome" : username.trim();
    }

    public void setPassword(String password) {
        this.password = (password == null) ? "" : password;
    }

    public void addShelf(Shelf shelf) {
        if (shelf != null && !shelves.contains(shelf)) shelves.add(shelf);
    }

    public boolean removeShelf(Shelf shelf) {
        return shelves.remove(shelf);
    }

    @Override
    public String toString() {
        return "ID: " + id +
                "\nNome: " + username +
                "\nSenhas: (oculto)" +
                "\nShelves: " + shelves.size();
    }
}
