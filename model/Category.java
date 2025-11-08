package model;

import java.io.Serializable;
import java.util.ArrayList;

public final class Category extends AbstractCrudObject implements Serializable, CrudObjectInterface {

    private static int nextId = 1;
    private String name;
    private final ArrayList<Category> subCategories = new ArrayList<>();
    private final ArrayList<Book> books = new ArrayList<>();

    private Category(int id, String name) {
        super(id);
        setName(name);
    }

    //Builder pattern
    public static class Builder {
        private Integer id = null;
        private String name = "Sem nome";

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = (name == null || name.isBlank()) ? "Sem nome" : name.trim();
            return this;
        }

        public Category build() {
            int finalId = (id != null) ? id : nextId++;
            return new Category(finalId, name);
        }
    }

    public static void resetIdCounter(int next) { nextId = next; }

    public String getName() { return name; }

    public void setName(String name) {
        this.name = (name == null || name.isBlank()) ? "Sem nome" : name.trim();
    }

    public ArrayList<Book> getBooks() {
        return books;
    }

    public boolean addBook(Book book) {
        if (book == null) return false;
        if (!books.contains(book)) {
            books.add(book);
            book.addCategory(this);
            return true;
        }
        return false;
    }

    public boolean removeBook(Book book) {
        if (book == null) return false;
        if (books.remove(book)) {
            book.removeCategory(this);
            return true;
        }
        return false;
    }

    public ArrayList<Category> getSubCategories() {
        return new ArrayList<>(subCategories);
    }

    public boolean addSubCategory(Category sub) {
        if (sub == null || sub == this || subCategories.contains(sub)) return false;
        return subCategories.add(sub);
    }

    public boolean removeSubCategory(Category sub) {
        if (sub == null) return false;
        return subCategories.remove(sub);
    }

    public void clearSubCategories() {
        subCategories.clear();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Categoria ")
                .append(id)
                .append(": ").append(name);

        if (!subCategories.isEmpty()) {
            sb.append("\n  Subcategorias: ");
            for (Category sub : subCategories) {
                sb.append(sub.getName()).append(" (ID ").append(sub.getId()).append("), ");
            }
            sb.setLength(sb.length() - 2);
        }

        if (!books.isEmpty()) {
            sb.append("\n  Livros: ");
            for (Book b : books) {
                sb.append(b.getName()).append(" (ID ").append(b.getId()).append("), ");
            }
            sb.setLength(sb.length() - 2);
        }

        return sb.toString();
    }
}
