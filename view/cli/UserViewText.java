package view.cli;

import controller.AppController;
import controller.UserController;
import model.User;
import view.UserView;

import java.util.Scanner;

public final class UserViewText implements UserView {

    private final UserController userController = AppController.getController().user();
    private final AppController appController = AppController.getController();
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void showMenu() {
        while (true) {
            User loggedUser = appController.getUser();

            System.out.println("\n--- Usuários ---");
            System.out.println(loggedUser != null
                    ? "Usuário atual: " + loggedUser.getUsername()
                    : "Nenhum usuário logado.");

            System.out.println("1) Registrar");
            System.out.println("2) Login");
            System.out.println("3) Logout");
            System.out.println("0) Voltar");
            System.out.print("> ");
            String opt = scanner.nextLine().trim();

            switch (opt) {
                case "1" -> register();
                case "2" -> login();
                case "3" -> logout();
                case "0" -> { return; }
                default -> System.out.println("Opção inválida");
            }
        }
    }

    /** User creation logic **/
    private void register() {
        System.out.print("Nome de usuário: ");
        String name = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String pass = scanner.nextLine().trim();

        if (userController.usernameExists(name)) {
            System.out.println("Usuário já existe!");
            return;
        }

        userController.create(name, pass);
        System.out.println("Usuário criado com sucesso!");
    }

    /** Access to user options and observer notification trigger **/
    private void login() {
        if (appController.getUser() != null) {
            System.out.println("Já há um usuário logado. Faça logout antes.");
            return;
        }

        System.out.print("Usuário: ");
        String name = scanner.nextLine().trim();
        System.out.print("Senha: ");
        String pass = scanner.nextLine().trim();

        User u = userController.login(name, pass);
        if (u != null) {
            appController.setUser(u);
            appController.notifyUserChange(u);
            System.out.println("Login realizado! Bem-vindo, " + u.getUsername());
        } else {
            System.out.println("Usuário ou senha incorretos.");
        }
    }

    /** Losing access to user options (automatic observer trigger) **/
    private void logout() {
        if (appController.getUser() == null) {
            System.out.println("Nenhum usuário logado.");
        } else {
            System.out.println("Usuário " + appController.getUser().getUsername() + " desconectado.");
            appController.setUser(null);
        }
    }


    @Override
    public void setVisible(boolean visible) {}
}
