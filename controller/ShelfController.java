package controller;

import model.Book;
import model.Shelf;
import model.User;

import java.util.ArrayList;
import java.util.List;

public final class ShelfController extends UserBoundElementsController<Shelf> implements UserObserver {

    public ShelfController(User user) {
        super(user);
    }

    @Override
    public void onUserChanged(User newUser) {
        this.user = newUser;
    }

    @Override
    public Shelf create(Object... args) {
        if (this.user == null) return null; // Proteção
        if (args.length == 0 || !(args[0] instanceof String name)) return null;

        Shelf shelf = new Shelf(name);
        user.addShelf(shelf); // Modifica o usuário em memória
        return shelf;
    }

    @Override
    public void update(int id, Object... args) {
        if (this.user == null) return; // Proteção
        if (args.length == 0 || !(args[0] instanceof String newName)) return;

        Shelf shelf = findById(id);
        if (shelf != null) shelf.setName(newName); // Modifica o usuário em memória
    }

    @Override
    public List<Shelf> listAll() {
        if (this.user == null) return new ArrayList<>(); // Proteção
        return new ArrayList<>(user.getShelves());
    }

    @Override
    public Shelf findById(int id) {
        if (this.user == null) return null; // Proteção

        return user.getShelves().stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean deleteById(int id) {
        if (this.user == null) return false; // Proteção

        Shelf shelf = findById(id);
        return shelf != null && user.removeShelf(shelf); // Modifica o usuário em memória
    }


    public void addBook(int shelfId, Book book) {
        if (this.user == null) return; // Proteção

        Shelf shelf = findById(shelfId);
        if (shelf != null) shelf.addBook(book);
    }
    /**
     * Retorna a lista de livros de uma estante específica.
     */
    public List<Book> getBooks(int shelfId) {
        Shelf shelf = findById(shelfId);
        if (shelf != null) {
            return shelf.getStoredBooks(); // Retorna a lista de livros
        }
        return new ArrayList<>(); // Retorna lista vazia se a estante não for encontrada
    }

    /**
     * Remove um livro de uma estante usando o ID do livro.
     */
    public boolean removeBookById(int shelfId, int bookId) {
        Shelf shelf = findById(shelfId);
        if (shelf == null) return false;

        // Encontra o livro dentro da estante pelo ID
        Book bookToRemove = shelf.getStoredBooks().stream()
                .filter(b -> b.getId() == bookId)
                .findFirst()
                .orElse(null);

        if (bookToRemove != null) {
            // Assumindo que seu model Shelf tem um método removeBook(Book b)
            return shelf.removeBook(bookToRemove);
        }
        return false;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}