package view.textual;

import controller.AppController;
import view.BookView;
import controller.BookController;
import model.Book;

import java.util.List;
import java.util.Scanner;

public final class BookViewText implements BookView {
    private final BookController bookController = AppController.getController().books();
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n--- Livros (CLI) ---");
            System.out.println("1) Listar livros");
            System.out.println("2) Criar livro");
            System.out.println("3) Editar livro");
            System.out.println("4) Remover livro");
            System.out.println("0) Voltar");
            System.out.print("> ");
            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1": list(); break;
                case "2": create(); break;
                case "3": edit(); break;
                case "4": remove(); break;
                case "0": return;
                default: System.out.println("Opção inválida");
            }
        }
    }

    /** gets the list of all books  **/
    private void list() {
        List<Book> books = bookController.listAll();
        if (books.isEmpty()) { System.out.println("Nenhum livro."); return; }
        for (Book b : books) {
            System.out.println("-----------");
            System.out.println(b);
            System.out.println("-----------");
        }
    }

    /** creates a book **/
    private void create() {
        System.out.print("Nome: ");
        String name = scanner.nextLine();
        System.out.print("Autor: ");
        String author = scanner.nextLine();
        System.out.print("Editora: ");
        String publisher = scanner.nextLine();
        System.out.print("Número de páginas: ");
        Integer pages;
        try {
            pages = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            pages = 0;
        }
        System.out.print("Preço (ex: 12.5): ");
        double price;
        try {
            price = Double.parseDouble(scanner.nextLine());
        } catch (Exception e) {
            price = 0;
        }

        Book b = bookController.create(name, price, author, publisher, pages);
        System.out.println("Criado: " + b);
    }

    /** edits a book **/
    private void edit() {
        System.out.print("ID do livro: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido.");
            return;
        }

        Book b = bookController.findById(id);
        if (b == null) { System.out.println("Livro não encontrado"); return; }

        System.out.print("Novo nome (enter para manter): ");
        String name = scanner.nextLine();
        System.out.print("Novo preço (enter para manter): ");
        String p = scanner.nextLine();
        System.out.print("Novo autor (enter para manter): ");
        String author = scanner.nextLine();
        System.out.print("Nova editora (enter para manter): ");
        String publisher = scanner.nextLine();
        System.out.print("Novo número de páginas (enter para manter): ");
        String pages = scanner.nextLine();

        try {
            bookController.update(
                    id,
                    name.isBlank() ? null : name,
                    p.isBlank() ? null : Double.parseDouble(p),
                    author.isBlank() ? null : author,
                    publisher.isBlank() ? null : publisher,
                    pages.isBlank() ? null : Integer.parseInt(pages)
            );
        } catch (Exception e) {
            System.out.println("Erro ao atualizar: " + e.getMessage());
        }

        System.out.println("Atualizado.");
    }

    /** removes a book **/
    private void remove() {
        System.out.print("ID do livro: ");
        int id;
        try {
            id = Integer.parseInt(scanner.nextLine());
        } catch (Exception e) {
            System.out.println("ID inválido.");
            return;
        }
        boolean ok = bookController.deleteById(id);
        System.out.println(ok ? "Removido." : "Não encontrado.");
    }

    @Override
    public void setVisible(boolean visible) { if (visible) showMenu(); }
}
