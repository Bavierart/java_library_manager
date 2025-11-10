package view.textual;

import controller.AppController;
import controller.BookController;
import controller.ShelfController;
import controller.UserObserver;
import model.Book;
import model.Shelf;
import model.User;
import view.ShelfView;

import java.util.List;
import java.util.Scanner;

public final class ShelfViewText implements ShelfView, UserObserver {
    private final ShelfController shelfController = AppController.getController().shelves();
    private final BookController bookController = AppController.getController().books();
    private final Scanner scanner = new Scanner(System.in);
    private User user = shelfController.getUser();

    @Override
    public void showMenu() {
        if (user == null) {
            System.out.println("Nenhum usuário logado. Acesso negado.");
            return;
        }

        while (true) {
            System.out.println("\n--- Estantes de " + user.getUsername() + " ---");
            System.out.println("1) Listar estantes");
            System.out.println("2) Criar estante");
            System.out.println("3) Remover estante");
            System.out.println("4) Ver detalhes da estante"); // Nova opção
            // A opção "Adicionar livro" foi movida para "Ver detalhes"
            System.out.println("0) Voltar");
            System.out.print("> ");
            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1" -> list();
                case "2" -> create();
                case "3" -> remove();
                case "4" -> viewShelfDetails(); // Nova chamada
                case "0" -> { return; }
                default -> System.out.println("Opção inválida");
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {

    }

    private void list() {
        var shelves = shelfController.listAll();
        if (shelves.isEmpty()) {
            System.out.println("Nenhuma estante criada ainda.");
            return;
        }
        shelves.forEach(s -> System.out.println(" - " + s.getName()));
    }

    private void create() {
        System.out.print("Nome da estante: ");
        String name = scanner.nextLine().trim();
        shelfController.create(name);
        System.out.println("Estante criada com sucesso.");
    }

    private void remove() {
        System.out.print("ID da estante: ");
        try {
            int id = Integer.parseInt(scanner.nextLine());
            boolean ok = shelfController.deleteById(id);
            System.out.println(ok ? "Removida." : "Não encontrada.");
        } catch (Exception e) {
            System.out.println("ID inválido.");
        }
    }

    private void viewShelfDetails() {
        System.out.print("Digite o ID da estante para ver os detalhes: ");
        int shelfId;
        try {
            shelfId = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido.");
            return;
        }

        Shelf shelf = shelfController.findById(shelfId);
        if (shelf == null) {
            System.out.println("Estante não encontrada.");
            return;
        }

        // Sub-menu de detalhes da estante
        while (true) {
            System.out.println("\n--- Detalhes da Estante: " + shelf.getName() + " (ID: " + shelfId + ") ---");
            List<Book> booksInShelf = shelfController.getBooks(shelfId);
            if (booksInShelf.isEmpty()) {
                System.out.println(" (Estante vazia)");
            } else {
                System.out.println("Livros na estante:");
                booksInShelf.forEach(b -> System.out.println("  - ID: " + b.getId() + " - " + b.getName()));
            }
            System.out.println("--------------------");
            System.out.println("1) Adicionar livro a esta estante");
            System.out.println("2) Remover livro desta estante");
            System.out.println("0) Voltar para o menu de estantes");
            System.out.print("> ");
            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1" -> addBookToThisShelf(shelfId);
                case "2" -> removeBookFromThisShelf(shelfId);
                case "0" -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    /** Adding new books to a shelf via its id;
     * @param shelfId is used to access it**/
    private void addBookToThisShelf(int shelfId) {
        System.out.println("\nLivros disponíveis no sistema:");
        var allBooks = bookController.listAll();
        if (allBooks.isEmpty()) {
            System.out.println("Nenhum livro cadastrado no sistema.");
            return;
        }
        allBooks.forEach(b -> System.out.println(" - ID: " + b.getId() + " - " + b.getName()));

        System.out.print("Digite o ID do Livro para ADICIONAR: ");
        int bookId;
        try {
            bookId = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido.");
            return;
        }

        Book book = bookController.findById(bookId);
        if (book == null) {
            System.out.println("Livro não encontrado no sistema.");
            return;
        }

        shelfController.addBook(shelfId, book);
        System.out.println("Livro adicionado.");
    }

    /** Also accessing via id, it removes a book from the shelf **/
    private void removeBookFromThisShelf(int shelfId) {
        System.out.print("Digite o ID do Livro para REMOVER (da lista acima): ");
        int bookId;
        try {
            bookId = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido.");
            return;
        }

        boolean ok = shelfController.removeBookById(shelfId, bookId);
        if (ok) {
            System.out.println("Livro removido.");
        } else {
            System.out.println("Falha ao remover. Livro não encontrado nesta estante.");
        }
    }

    @Override
    public void onUserChanged(User newUser) {
        this.user = newUser;
        if (newUser != null)
            System.out.println("[ShelfView] Usuário logado: " + newUser.getUsername());
        else
            System.out.println("[ShelfView] Usuário deslogado.");
    }


    @Override
    public User getUser() {
        return user;
    }
}
