package view.cli;

import controller.*;
import model.Book;
import model.Review;
import model.User;
import view.ReviewView;

import java.util.Scanner;

public final class ReviewViewText implements ReviewView, UserObserver {

    private final ReviewController ReviewController = AppController.getController().reviews();
    private final BookController bookController = AppController.getController().books();
    private final Scanner scanner = new Scanner(System.in);
    private User user = ReviewController.getUser();
    private Book currentBook;

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n=== Menu de Reviews ===");
            System.out.println("1) Selecionar Livro");
            System.out.println("2) Listar Reviews do Livro");
            System.out.println("3) Criar Review");
            System.out.println("4) Editar Review");
            System.out.println("5) Remover Review");
            System.out.println("0) Voltar");
            System.out.print("> ");

            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1" -> selectBook();
                case "2" -> listReviews();
                case "3" -> createReview();
                case "4" -> editReview();
                case "5" -> removeReview();
                case "0" -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    /** Intermediate method for book selection **/
    private void selectBook() {
        System.out.print("Digite o ID do livro: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Book b = bookController.findById(id);
            if (b == null) {
                System.out.println("Livro não encontrado.");
            } else {
                currentBook = b;
                System.out.println("Livro selecionado: " + b.getName());
            }
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    /** Shows textual representation of all book reviews **/
    private void listReviews() {
        if (!ensureBookSelected()) return;
        ReviewController.listAll(currentBook);
    }

    /** Review creation **/
    private void createReview() {
        if (!ensureBookSelected()) return;
        if (user == null) {
            System.out.println("Você precisa estar logado para criar uma review.");
            return;
        }

        System.out.print("Comentário: ");
        String comment = scanner.nextLine();

        System.out.print("Score (0-5): ");
        double rank;
        try {
            rank = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Score inválido, usando 0.");
            rank = 0d;
        }

        Review r = ReviewController.create(currentBook, comment, rank);
        if (r != null)
            System.out.println("Review criada: " + r);
    }

    /** Review editing **/
    private void editReview() {
        if (!ensureBookSelected()) return;
        User user = ReviewController.getUser();
        if (user == null) {
            System.out.println("Você precisa estar logado para editar reviews.");
            return;
        }

        if (currentBook.getReviews().isEmpty()) {
            System.out.println("Nenhuma review neste livro.");
            return;
        }

        System.out.print("ID da review: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
            return;
        }

        Review review = ReviewController.findById(currentBook, id);
        if (review == null) {
            System.out.println("Review não encontrada.");
            return;
        }

        if (!user.getUsername().equals(review.getAuthor().getUsername())) {
            System.out.println("Você só pode editar suas próprias reviews.");
            return;
        }

        System.out.print("Novo comentário (enter para manter): ");
        String comment = scanner.nextLine();
        System.out.print("Novo Score (0-5, enter para manter): ");
        String rStr = scanner.nextLine();

        Double newRank = null;
        if (!rStr.isBlank()) {
            try {
                newRank = Double.parseDouble(rStr);
            } catch (NumberFormatException e) {
                System.out.println("Score inválido, ignorando.");
            }
        }

        ReviewController.update(currentBook, review,
                comment.isBlank() ? null : comment,
                newRank);
        System.out.println("Review atualizada!");
    }

    /** Review removal **/
    private void removeReview() {
        if (!ensureBookSelected()) return;
        User user = ReviewController.getUser();
        if (user == null) {
            System.out.println("Você precisa estar logado para remover reviews.");
            return;
        }

        if (currentBook.getReviews().isEmpty()) {
            System.out.println("Nenhuma review neste livro.");
            return;
        }

        System.out.print("ID da review para remover: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            Review r = ReviewController.findById(currentBook, id);

            if (r == null) {
                System.out.println("Review não encontrada.");
                return;
            }

            if (!user.getUsername().equals(r.getAuthor().getUsername())) {
                System.out.println("Você só pode remover suas próprias reviews.");
                return;
            }

            ReviewController.delete(currentBook, r);
            System.out.println("Review removida!");
        } catch (NumberFormatException e) {
            System.out.println("ID inválido.");
        }
    }

    /** private method ensuring that the book is selected **/
    private boolean ensureBookSelected() {
        if (currentBook == null) {
            System.out.println("Nenhum livro selecionado. Use a opção 1 primeiro.");
            return false;
        }
        return true;
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) showMenu();
    }

    @Override
    public void onUserChanged(User newUser) {
        this.user = newUser;
        if (newUser != null)
            System.out.println("[ReviewView] Usuário logado: " + newUser.getUsername());
        else
            System.out.println("[ReviewView] Usuário deslogado.");
    }


    @Override
    public User getUser() {
        return user;
    }
}
