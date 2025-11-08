package view.gui;

import controller.AppController;
import controller.UserController;
import model.User;
import view.UserView;

import javax.swing.*;
import java.awt.*;

/**
 * Implementação da UserView usando Java Swing.
 * [cite_start]Substitui o menu de console de usuário[cite: 173].
 */
public final class UserViewGui extends JDialog implements UserView {

    private final UserController userController = AppController.getController().user();
    private final AppController appController = AppController.getController();

    private final JLabel statusLabel;
    private final JButton registerButton;
    private final JButton loginButton;
    private final JButton logoutButton;

    public UserViewGui() {
        setTitle("Usuários");
        setSize(350, 250);
        setLocationRelativeTo(null); // Centralizar
        setModal(true);
        setLayout(new BorderLayout(10, 10));

        // --- Status (Norte) ---
        statusLabel = new JLabel("Nenhum usuário logado.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(statusLabel, BorderLayout.NORTH);

        // --- Botões (Centro) ---
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));

        registerButton = new JButton("1) Registrar");
        loginButton = new JButton("2) Login");
        logoutButton = new JButton("3) Logout");

        buttonPanel.add(registerButton);
        buttonPanel.add(loginButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);

        // --- Ações dos Botões ---
        registerButton.addActionListener(e -> register());
        loginButton.addActionListener(e -> login());
        logoutButton.addActionListener(e -> logout());
    }

    /**
     * Atualiza o estado da janela (label e botões) com base no usuário logado.
     * Chamado sempre que a janela se torna visível.
     */
    private void updateStatus() {
        User loggedUser = appController.getUser();
        if (loggedUser != null) {
            statusLabel.setText("Usuário atual: " + loggedUser.getUsername());
            loginButton.setEnabled(false);
            registerButton.setEnabled(false);
            logoutButton.setEnabled(true);
        } else {
            statusLabel.setText("Nenhum usuário logado.");
            loginButton.setEnabled(true);
            registerButton.setEnabled(true);
            logoutButton.setEnabled(false);
        }
    }

    /**
     * Exibe um diálogo para registrar um novo usuário.
     * [cite_start]Equivalente a register() [cite: 182-185].
     */
    private void register() {
        // Painel customizado para o JOptionPane
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(new JLabel("Nome de usuário:"));
        panel.add(usernameField);
        panel.add(new JLabel("Senha:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Registrar",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            if (name.isEmpty() || pass.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Usuário e senha não podem estar em branco.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (userController.usernameExists(name)) { // [cite: 183]
                JOptionPane.showMessageDialog(this, "Usuário já existe!", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            userController.create(name, pass);
            JOptionPane.showMessageDialog(this, "Usuário criado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Exibe um diálogo para fazer login.
     * [cite_start]Equivalente a login() [cite: 186-189].
     */
    private void login() {
        // Reutiliza o mesmo layout do painel de registro
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField usernameField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        panel.add(new JLabel("Usuário:"));
        panel.add(usernameField);
        panel.add(new JLabel("Senha:"));
        panel.add(passwordField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Login",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String name = usernameField.getText().trim();
            String pass = new String(passwordField.getPassword()).trim();

            User u = userController.login(name, pass);
            if (u != null) {
                appController.setUser(u);
                appController.notifyUserChange(u);
                JOptionPane.showMessageDialog(this, "Login realizado! Bem-vindo, " + u.getUsername(), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                setVisible(false); // Fecha a janela de login após o sucesso
            } else {
                JOptionPane.showMessageDialog(this, "Usuário ou senha incorretos.", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Executa o logout do usuário atual.
     * [cite_start]Equivalente a logout() [cite: 190-191].
     */
    private void logout() {
        User currentUser = appController.getUser();
        if (currentUser != null) {
            JOptionPane.showMessageDialog(this, "Usuário " + currentUser.getUsername() + " desconectado.", "Logout", JOptionPane.INFORMATION_MESSAGE);
            appController.setUser(null);
            // A notificação (notifyUserChange) deve ser disparada pelo setUser(null) no AppController
            updateStatus(); // Atualiza esta janela
        } else {
            // Este botão não deveria estar clicável se ninguém estiver logado, mas por segurança:
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    public void showMenu() {
        setVisible(true);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            updateStatus();
        }
        super.setVisible(visible);
    }
}