package view.cli;

import controller.AppController;
import controller.CategoryController;
import controller.BookController;
import model.Book;
import model.Category;
import view.CategoryView;

import java.util.List;
import java.util.Scanner;

public final class CategoryViewText implements CategoryView {
    private final CategoryController categoryController = AppController.getController().categories();
    private final BookController bookController = AppController.getController().books();
    private final Scanner scanner = new Scanner(System.in);


    @Override
    public void showMenu() {
        while (true) {
            System.out.println("\n=== Menu de Categorias ===");
            System.out.println("1) Listar categorias");
            System.out.println("2) Criar categoria");
            System.out.println("3) Editar categoria");
            System.out.println("4) Remover categoria");
            System.out.println("5) Adicionar livro a categoria");
            System.out.println("6) Remover livro de categoria");
            System.out.println("7) Listar livros da categoria");
            System.out.println("8) Adicionar subcategoria");
            System.out.println("9) Remover subcategoria");
            System.out.println("10) Listar subcategorias");
            System.out.println("0) Voltar");
            System.out.print("> ");

            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1" -> listCategories();
                case "2" -> createCategory();
                case "3" -> editCategory();
                case "4" -> removeCategory();
                case "5" -> addBookToCategory();
                case "6" -> removeBookFromCategory();
                case "7" -> listBooksOfCategory();
                case "8" -> addSubCategory();
                case "9" -> removeSubCategory();
                case "10" -> listSubCategories();
                case "0" -> { return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }


    private void listCategories() {
        List<Category> categories = categoryController.listAll();
        if (categories.isEmpty()) {
            System.out.println("Nenhuma categoria cadastrada.");
            return;
        }
        for (Category c : categories) {
            System.out.println("-----------");
            System.out.println(c);
        }
    }

    private void createCategory() {
        System.out.print("Nome da categoria: ");
        String name = scanner.nextLine().trim();
        Category c = categoryController.create(name);
        System.out.println("Categoria criada: " + c);
    }

    private void editCategory() {
        System.out.print("ID da categoria: ");
        int id = parseIntInput();
        if (id == -1) return;

        Category c = categoryController.findById(id);
        if (c == null) {
            System.out.println("Categoria não encontrada.");
            return;
        }

        System.out.print("Novo nome (enter para manter): ");
        String newName = scanner.nextLine();
        if (!newName.isBlank()) {
            categoryController.update(id, newName);
            System.out.println("Categoria atualizada.");
        } else {
            System.out.println("Sem alterações.");
        }
    }

    private void removeCategory() {
        System.out.print("ID da categoria: ");
        int id = parseIntInput();
        if (id == -1) return;
        boolean ok = categoryController.deleteById(id);
        System.out.println(ok ? "Removida." : "Não encontrada.");
    }

    private void addBookToCategory() {
        System.out.print("ID da categoria: ");
        int catId = parseIntInput();
        Category cat = categoryController.findById(catId);
        if (cat == null) { System.out.println("Categoria não encontrada."); return; }

        System.out.print("ID do livro: ");
        int bookId = parseIntInput();
        Book book = bookController.findById(bookId);
        if (book == null) { System.out.println("Livro não encontrado."); return; }

        boolean added = categoryController.addBookToCategory(cat, book);
        System.out.println(added ? "Livro adicionado à categoria." : "Falha ou já adicionado.");
    }

    private void removeBookFromCategory() {
        System.out.print("ID da categoria: ");
        int catId = parseIntInput();
        Category cat = categoryController.findById(catId);
        if (cat == null) { System.out.println("Categoria não encontrada."); return; }

        System.out.print("ID do livro: ");
        int bookId = parseIntInput();
        Book book = bookController.findById(bookId);
        if (book == null) { System.out.println("Livro não encontrado."); return; }

        boolean removed = categoryController.removeBookFromCategory(cat, book);
        System.out.println(removed ? "Livro removido da categoria." : "Falha ao remover.");
    }

    private void listBooksOfCategory() {
        System.out.print("ID da categoria: ");
        int id = parseIntInput();
        Category cat = categoryController.findById(id);
        if (cat == null) {
            System.out.println("Categoria não encontrada.");
            return;
        }
        List<Book> books = categoryController.listBooksInCategory(cat);
        if (books.isEmpty()) {
            System.out.println("Nenhum livro nesta categoria.");
        } else {
            System.out.println("Livros na categoria '" + cat.getName() + "':");
            for (Book b : books) {
                System.out.println(" - " + b.getName() + " (ID " + b.getId() + ")");
            }
        }
    }

    private int parseIntInput() {
        try {
            return Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Entrada inválida.");
            return -1;
        }
    }

    private Category findParent() {
        System.out.print("ID da categoria pai: ");
        int parentId = parseIntInput();
        Category parent = categoryController.findById(parentId);
        if (parent == null) { System.out.println("Categoria não encontrada."); return null; }
        return parent;
    }

    private void addSubCategory() {
        Category parent = findParent();
        if (parent==null) return;
        System.out.print("Nome da subcategoria: ");
        String name = scanner.nextLine().trim();
        boolean added = categoryController.addSubCategory(parent, name);
        System.out.println(added ? "Subcategoria adicionada." : "Falha ao adicionar subcategoria.");
    }

    private void removeSubCategory() {
        Category parent = findParent();
        if (parent==null) return;

        System.out.print("ID da subcategoria: ");
        int subId = parseIntInput();
        boolean removed = categoryController.removeSubCategory(parent, subId);
        System.out.println(removed ? "Subcategoria removida." : "Falha ao remover subcategoria.");
    }

    private void listSubCategories() {
        Category cat = findParent();
        if (cat==null) return;
        List<Category> subs = categoryController.listSubCategories(cat);
        if (subs.isEmpty()) {
            System.out.println("Nenhuma subcategoria.");
        } else {
            System.out.println("Subcategorias de '" + cat.getName() + "':");
            for (Category s : subs) {
                System.out.println(" - " + s.getName() + " (ID " + s.getId() + ")");
            }
        }
    }


    @Override
    public void setVisible(boolean visible) {
        if (visible) showMenu();
    }

}
