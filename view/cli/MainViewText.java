package view.cli;

import controller.AppController;
import model.User;
import view.MainView;

import java.util.Scanner;

public final class MainViewText implements MainView {

    private final Scanner scanner = new Scanner(System.in);
    private final BookViewText bookView;
    private final CategoryViewText categoryView;
    private final UserViewText userView;
    private final ReviewViewText reviewView;
    private final ShelfViewText shelfViewText;
    private final AppController appController;

    /** Constructor acts as a receiver for all necessary sub-views, it is an access class **/
    public MainViewText(BookViewText bookView, CategoryViewText categoryView, UserViewText userView,
                        ReviewViewText reviewView, ShelfViewText shelfViewText, AppController appController) {
        this.bookView = bookView;
        this.categoryView = categoryView;
        this.userView = userView;
        this.reviewView = reviewView;
        this.shelfViewText = shelfViewText;
        this.appController = appController;
    }

    @Override
    public void showMenu() {
        while (true) {
            User logged = appController.getUser();

            System.out.println("\n--- Biblioteca (CLI) ---");
            System.out.println(logged != null
                    ? "Usuário atual: " + logged.getUsername()
                    : "Nenhum usuário logado.");
            System.out.println("1) Usuários");
            System.out.println("2) Livros");
            System.out.println("3) Categorias");
            System.out.println("4) Reviews");
            System.out.println("5) Estantes");
            System.out.println("6) Salvar estado");
            System.out.println("7) Carregar estado");
            System.out.println("0) Logout");
            System.out.println("-) Sair");
            System.out.print("> ");

            String opt = scanner.nextLine().trim();
            switch (opt) {
                case "1" -> userView.showMenu();
                case "2" -> bookView.showMenu();
                case "3" -> categoryView.showMenu();
                case "4" -> reviewView.showMenu();
                case "5" -> shelfViewText.showMenu();
                case "6" -> {
                    appController.saveAll();
                    System.out.println("Estado salvo");
                }
                case "7" -> {
                    appController.loadAll();
                    System.out.println("Estado carregado");
                }
                case "0" -> {
                    if (appController.getUser() != null) appController.setUser(null);
                }
                case "-" -> { System.out.println("Saindo..."); return; }
                default -> System.out.println("Opção inválida.");
            }
        }
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) showMenu();
    }
}
