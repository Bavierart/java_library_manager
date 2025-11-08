package controller;

import model.Book;
import model.Category;

public final class BookController extends GlobalElementsController<Book> {
    public BookController() {
        super("data/books.dat");
    }

    @Override
    protected Book build(Object... args) {
        String name = (String) args[0];
        Double price = (Double) args[1];
        String author = (String) args[2];
        String publisher = (String) args[3];
        Integer pages = (Integer) args[4];

        return new Book.Builder()
                .setName(name)
                .setPrice(price)
                .setAuthor(author)
                .setPublisher(publisher)
                .setPages(pages)
                .build();
    }

    @Override
    protected void applyUpdate(Book book, Object... args) {
        String newName = (String) args[0];
        Double newPrice = (Double) args[1];
        String newAuthor = (String) args[2];
        String newPublisher = (String) args[3];
        Integer newPages = (Integer) args[4];

        if (newName != null) book.setName(newName);
        if (newPrice != null) book.setPrice(newPrice);
        if (newAuthor != null) book.setAuthor(newAuthor);
        if (newPublisher != null) book.setPublisher(newPublisher);
        if (newPages != null) book.setPages(newPages);
    }

    @Override
    protected void resetIdCounter(int nextId) {
        Book.resetIdCounter(nextId);
    }

    @Override
    public void loadAll() {
        super.loadAll();
    }

    @Override
    public boolean deleteById(int id) {
        Book obj = crudAux.findById(id);
        assert obj != null;
        obj.getReviews().clear();
        obj.getCategories().clear();
        return crudAux.delete(obj);
    }
}
