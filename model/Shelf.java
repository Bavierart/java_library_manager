package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Shelf implements Serializable, CrudObjectInterface {
    private static final long serialVersionUID = 1L;
    private int id;

    private final List<Book> storedBooks = new ArrayList<>();
    private String name;

    public Shelf(String name) {
        this.name = name == null ? "" : name.trim();
    }

    public List<Book> getStoredBooks() {
        return Collections.unmodifiableList(storedBooks);
    }

    public void addBook(Book book) {
        if (book != null && !storedBooks.contains(book)) {
            storedBooks.add(book);
        }
    }

    public boolean removeBook(Book book) {
        return storedBooks.remove(book);
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? "" : name.trim();
    }

    @Override
    public String toString() {
        return String.format("Shelf{id=%s, name='%s', size=%d}", id, name, storedBooks.size());
    }

}
