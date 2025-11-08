package view.gui;

import controller.AppController;
import controller.UserObserver; // Importa a interface do observador
import model.User;
import view.*; // Importa as interfaces (MainView, BookView, etc.)

import javax.swing.*;
import java.awt.*;

/**
 * Implementação da MainView usando Java Swing.
 * Esta é a janela principal da aplicação.
 * Ela também observa mudanças no usuário para atualizar seu estado.
 */
public final class MainViewGui extends JFrame implements MainView, UserObserver {

    // Dependências (controlador e outras views)
    private final BookView bookView;
    private final CategoryView categoryView;
    private final UserView userView;
    private final ReviewView reviewView;
    private final ShelfView shelfView;
    private final AppController appController;

    // Componentes Swing
    private final JLabel statusLabel;
    private final JButton userButton;
    private final JButton bookButton;
    private final JButton categoryButton;
    private final JButton reviewButton;
    private final JButton shelfButton;
    private final JButton saveButton;
    private final JButton loadButton;
    private final JButton logoutButton;

    /**
     * Construtor que recebe todas as sub-views e o controlador.
     * Espelha o construtor de MainViewText .
     */
    public MainViewGui(BookView bookView, CategoryView categoryView, UserView userView,
                       ReviewView reviewView, ShelfView shelfView, AppController appController) {

        // Armazena as dependências
        this.bookView = bookView;
        this.categoryView = categoryView;
        this.userView = userView;
        this.reviewView = reviewView;
        this.shelfView = shelfView;
        this.appController = appController;

        // Configuração da janela principal
        setTitle("Biblioteca GUI");
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // 'X' encerra a app
        setLocationRelativeTo(null); // Centraliza
        setLayout(new BorderLayout(10, 10));

        // --- Painel de Status (Norte) ---
        // Exibe o usuário logado
        statusLabel = new JLabel("Nenhum usuário logado.", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(statusLabel, BorderLayout.NORTH);

        // --- Painel de Botões (Centro) ---
        JPanel buttonPanel = new JPanel(new GridLayout(0, 1, 10, 10)); // 1 coluna
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        // Botões do menu
        userButton = new JButton("Usuários");
        bookButton = new JButton("Livros");
        categoryButton = new JButton("Categorias");
        reviewButton = new JButton("Reviews");
        shelfButton = new JButton("Estantes");
        saveButton = new JButton("Salvar estado");
        loadButton = new JButton("Carregar estado");
        logoutButton = new JButton("Logout");

        // Adiciona os botões ao painel
        buttonPanel.add(userButton);
        buttonPanel.add(bookButton);
        buttonPanel.add(categoryButton);
        buttonPanel.add(reviewButton);
        buttonPanel.add(shelfButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(logoutButton);

        add(buttonPanel, BorderLayout.CENTER);

        // --- Configura Ações (ActionListeners) ---
        // Substitui o switch-case

        // 1) Usuários
        userButton.addActionListener(e -> userView.setVisible(true)); // (case "1")

        // 2) Livros
        bookButton.addActionListener(e -> bookView.setVisible(true)); // (case "2")

        // 3) Categorias
        categoryButton.addActionListener(e -> categoryView.setVisible(true)); // (case "3")

        // 4) Reviews
        reviewButton.addActionListener(e -> reviewView.setVisible(true)); // (case "4")

        // 5) Estantes
        shelfButton.addActionListener(e -> shelfView.setVisible(true)); // (case "5")

        // 6) Salvar
        saveButton.addActionListener(e -> { //
            appController.saveAll();
            JOptionPane.showMessageDialog(this,
                    "Estado salvo", "Salvo", JOptionPane.INFORMATION_MESSAGE); //
        });

        // 7) Carregar
        loadButton.addActionListener(e -> { //
            appController.loadAll();
            JOptionPane.showMessageDialog(this,
                    "Estado carregado", "Carregado", JOptionPane.INFORMATION_MESSAGE); //
            // A notificação do observador (onUserChanged) cuidará de atualizar o status.
        });

        // 0) Logout
        logoutButton.addActionListener(e -> { // (case "0")
            appController.setUser(null);
            // A notificação do observador (onUserChanged) cuidará de atualizar o status.
        });

        // Atualiza o estado inicial
        updateUserStatus(appController.getUser());
    }

    /**
     * Atualiza o label de status e habilita/desabilita botões
     * com base no usuário logado.
     */
    private void updateUserStatus(User loggedUser) {
        boolean isLoggedIn = (loggedUser != null);

        if (isLoggedIn) {
            statusLabel.setText("Usuário atual: " + loggedUser.getUsername()); //
        } else {
            statusLabel.setText("Nenhum usuário logado."); //
        }

        // Acesso negado às estantes se não logado
        // E para reviews, livros, categorias (implícito pela lógica do menu)
        bookButton.setEnabled(isLoggedIn);
        categoryButton.setEnabled(isLoggedIn);
        reviewButton.setEnabled(isLoggedIn);
        shelfButton.setEnabled(isLoggedIn);
        logoutButton.setEnabled(isLoggedIn);

        // Salvar/Carregar pode ser permitido, mas vamos manter
        // a lógica de que precisa estar logado para salvar.
        saveButton.setEnabled(isLoggedIn);

        // Login/Registro (UserView) e Carregar devem estar sempre disponíveis
        userButton.setEnabled(true);
        loadButton.setEnabled(true);
    }

    // --- Implementação da interface MainView ---

    @Override
    public void showMenu() {
        // Em Swing, showMenu é o mesmo que setVisible(true)
        setVisible(true); //
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            // Atualiza o status toda vez que a janela for exibida
            updateUserStatus(appController.getUser()); // (lógica do loop)
        }
        super.setVisible(visible); //
    }

    // --- Implementação da interface UserObserver ---

    /**
     * Este método é chamado pelo AppController quando o usuário
     * faz login ou logout.
     * @param newUser O novo usuário (ou null se for logout).
     */
    @Override
    public void onUserChanged(User newUser) {
        updateUserStatus(newUser);
    }

    @Override
    public User getUser() {
        return null;
    }
}