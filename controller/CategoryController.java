package controller;

import model.Book;
import model.Category;

import java.util.ArrayList;
import java.util.List;

public final class CategoryController extends GlobalElementsController<Category> {

    public CategoryController() {
        super("data/category.dat");
    }

    @Override
    protected Category build(Object... args) {
        String name = (String) args[0];
        return new Category.Builder()
                .setName(name)
                .build();
    }

    @Override
    protected void applyUpdate(Category category, Object... args) {
        String newName = (String) args[0];
        if (newName != null) category.setName(newName);
    }

    @Override
    protected void resetIdCounter(int nextId) {
        Category.resetIdCounter(nextId);
    }

    // ======== Métodos de Livros ========

    public boolean addBookToCategory(Category category, Book book) {
        if (category == null || book == null) return false;
        return category.addBook(book);
    }

    public boolean removeBookFromCategory(Category category, Book book) {
        if (category == null || book == null) return false;
        return category.removeBook(book);
    }

    public List<Book> listBooksInCategory(Category category) {
        return category.getBooks();
    }

    // ======== Métodos de Subcategorias ========

    public boolean addSubCategory(Category parent, String subName) {
        if (parent == null) return false;
        Category sub = new Category.Builder()
                .setName(subName)
                .build();
        crudAux.addObject(sub);
        return parent.addSubCategory(sub);
    }

    public boolean removeSubCategory(Category parent, int subId) {
        if (parent == null) return false;
        Category sub = findById(subId);
        if (sub == null) return false;
        boolean removed = parent.removeSubCategory(sub);
        if (removed) crudAux.delete(sub);
        return removed;
    }

    public List<Category> listSubCategories(Category category) {
        return category.getSubCategories();
    }

    @Override
    public boolean deleteById(int id) {
        Category category = findById(id);
        if (category == null) return false;

        for (Book b : new ArrayList<>(category.getBooks())) {
            category.removeBook(b);
        }

        for (Category sub : new ArrayList<>(category.getSubCategories())) {
            deleteById(sub.getId());
        }

        return super.deleteById(id);
    }
}
