package controller;

import model.Book;
import model.Review;
import model.User;

public final class ReviewController implements UserObserver {

    private final BookController bookController;
    private User currentUser;

    public ReviewController(User user, BookController bookController) {
        this.currentUser = user;
        this.bookController = bookController;
    }

    @Override
    public void onUserChanged(User newUser) {
        this.currentUser = newUser;
    }

    @Override
    public User getUser() {
        return currentUser;
    }

    public Review create(Book book, String comment, Double rank) {
        if (book == null || currentUser == null) {
            System.out.println("É necessário estar logado para criar uma review.");
            return null;
        }

        Review review = new Review.Builder()
                .setComment(comment)
                .setScore(rank)
                .setAuthor(currentUser)
                .build();

        book.addReview(review);
        saveAll();
        return review;
    }

    public void update(Book book, Review review, String newComment, Double newRank) {
        if (book == null || review == null || !book.getReviews().contains(review)) return;
        if (currentUser == null || !reviewAuthorIsCurrentUser(review)) {
            System.out.println("Você só pode editar suas próprias reviews.");
            return;
        }

        if (newComment != null) review.setComment(newComment);
        if (newRank != null) review.setScore(newRank);

        saveAll();
    }

    public void delete(Book book, Review review) {
        if (book == null || review == null) return;
        if (currentUser == null || !reviewAuthorIsCurrentUser(review)) {
            System.out.println("Você só pode remover suas próprias reviews.");
            return;
        }

        book.removeReview(review);
        saveAll();
    }

    public void listAll(Book book) {
        if (book == null) return;

        System.out.println("Reviews de \"" + book.getName() + "\":");
        if (book.getReviews().isEmpty()) {
            System.out.println(" (Nenhuma review)");
        } else {
            book.getReviews().forEach(r -> {
                String marker = (currentUser != null && reviewAuthorIsCurrentUser(r)) ? " (sua)" : "";
                System.out.println(" - " + r + marker);
            });
        }
    }

    private boolean reviewAuthorIsCurrentUser(Review review) {
        return currentUser != null && review != null &&
                currentUser.getUsername().equals(review.getAuthor().getUsername());
    }

    // ---------- Overloads usando IDs ----------
    public Review create(int bookId, String comment, Double rank) {
        Book book = bookController.findById(bookId);
        return create(book, comment, rank);
    }

    public void update(int bookId, Review review, String newComment, Double newRank) {
        Book book = bookController.findById(bookId);
        update(book, review, newComment, newRank);
    }

    public void delete(int bookId, Review review) {
        Book book = bookController.findById(bookId);
        delete(book, review);
    }

    public void listAll(int bookId) {
        Book book = bookController.findById(bookId);
        if (book == null) {
            System.out.println("Livro não encontrado.");
        } else {
            listAll(book);
        }
    }

    public void saveAll() {
        bookController.saveAll();
    }

    public void loadAll() {
        bookController.loadAll();

        int maxId = bookController.listAll().stream()
                .flatMap(book -> book.getReviews().stream())
                .mapToInt(Review::getId)
                .max()
                .orElse(0);

        Review.resetIdCounter(maxId + 1);
    }

    public Review findById(Book book, int reviewId) {
        if (book == null) return null;
        return book.getReviews().stream()
                .filter(r -> r.getId() == reviewId)
                .findFirst()
                .orElse(null);
    }

    public void deleteById(Book book, int reviewId) {
        Review review = findById(book, reviewId);
        if (review != null) delete(book, review);
    }

    public Review findById(int bookId, int reviewId) {
        Book book = bookController.findById(bookId);
        return findById(book, reviewId);
    }

    public void deleteById(int bookId, int reviewId) {
        Book book = bookController.findById(bookId);
        deleteById(book, reviewId);
    }

    public void setUser(User user) {
        this.currentUser = user;
    }
}
