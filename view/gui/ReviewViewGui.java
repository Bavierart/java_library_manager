package view.gui;

import controller.AppController;
import controller.BookController;
import controller.ReviewController;
import controller.UserObserver;
import model.Book;
import model.Review;
import model.User;
import view.ReviewView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

/**
 * ReviewViewGui — estilo idêntico ao CategoryViewGui.
 * Lado esquerdo: JList de reviews (clicável).
 * Lado direito: detalhes da review selecionada.
 * Painel inferior com botões organizados.
 */
public final class ReviewViewGui extends JDialog implements ReviewView, UserObserver {

    private final ReviewController reviewController = AppController.getController().reviews();
    private final BookController bookController = AppController.getController().books();

    private User user = reviewController.getUser();
    private Book currentBook;

    // Componentes principais
    private final DefaultListModel<Review> listModel = new DefaultListModel<>();
    private final JList<Review> reviewList = new JList<>(listModel);
    private final JTextArea detailArea = new JTextArea();
    private final JLabel bookStatusLabel;

    // Botões
    private final JButton createButton;
    private final JButton editButton;
    private final JButton removeButton;
    private final JButton refreshButton;
    private final JButton selectBookButton;
    private final JButton closeButton;

    public ReviewViewGui() {
        setTitle("Gerenciamento de Reviews");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout(10, 10));

        // --- Top: status do livro + botão selecionar ---
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        bookStatusLabel = new JLabel("Nenhum livro selecionado.");
        bookStatusLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        selectBookButton = new JButton("Selecionar Livro (ID)");

        topPanel.add(bookStatusLabel, BorderLayout.CENTER);
        topPanel.add(selectBookButton, BorderLayout.EAST);
        add(topPanel, BorderLayout.NORTH);

        // --- Left: JList de reviews dentro de JScrollPane ---
        reviewList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reviewList.setCellRenderer((list, value, index, isSelected, cellHasFocus) -> {
            // Renderer simples: mostra id, rank e autor (se disponível)
            String text;
            if (value == null) {
                text = "(nulo)";
            } else {
                String author = value.getAuthor() != null ? value.getAuthor().getUsername() : "anon";
                text = String.format("ID %d — %.1f/5 — %s: %s",
                        value.getId(),
                        value.getRank(),
                        author,
                        value.getComment() != null && value.getComment().length() > 30
                                ? value.getComment().substring(0, 30) + "..."
                                : (value.getComment() == null ? "" : value.getComment()));
            }
            JLabel lbl = new JLabel(text);
            if (isSelected) {
                lbl.setOpaque(true);
                lbl.setBackground(list.getSelectionBackground());
                lbl.setForeground(list.getSelectionForeground());
            }
            return lbl;
        });

        JScrollPane listScroll = new JScrollPane(reviewList);
        listScroll.setBorder(BorderFactory.createTitledBorder("Reviews"));
        listScroll.setPreferredSize(new Dimension(320, 400));

        // --- Right: detalhes da review selecionada ---
        detailArea.setEditable(false);
        detailArea.setBorder(BorderFactory.createTitledBorder("Detalhes da Review"));
        JScrollPane detailScroll = new JScrollPane(detailArea);

        // --- Split pane ---
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, detailScroll);
        splitPane.setDividerLocation(320);
        add(splitPane, BorderLayout.CENTER);

        // --- Bottom: botões em grade (estilo CategoryViewGui) ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5));
        refreshButton = new JButton("Listar/Atualizar");
        JButton listButton = new JButton("Listar (texto)");
        createButton = new JButton("Criar Review");
        editButton = new JButton("Editar Review");
        removeButton = new JButton("Remover Review");
        JButton showAllButton = new JButton("Mostrar todas as Reviews do Livro");
        closeButton = new JButton("Voltar");

        // Preencher grade (mantendo espaço caso queira adicionar botões depois)
        buttonPanel.add(refreshButton);
        buttonPanel.add(listButton);
        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(showAllButton);
        // duas células vazias para completar grid 2x4
        buttonPanel.add(new JLabel());
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // --- Ações ---
        selectBookButton.addActionListener(e -> selectBook());
        refreshButton.addActionListener(e -> populateReviewList());
        listButton.addActionListener(e -> listReviewsAsText());
        createButton.addActionListener(e -> createReview());
        editButton.addActionListener(e -> editReview());
        removeButton.addActionListener(e -> removeReview());
        showAllButton.addActionListener(e -> listReviewsAsText());
        closeButton.addActionListener(e -> setVisible(false));

        // --- Listener de seleção na JList ---
        reviewList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    showSelectedReviewDetails();
                }
            }
        });

        updateButtonState();
    }

    /** Atualiza habilitação dos botões conforme estado (livro selecionado e usuário logado). */
    private void updateButtonState() {
        boolean bookSelected = (currentBook != null);
        boolean userLogged = (user != null);
        // criar/editar/remover exigem livro selecionado e usuário logado
        createButton.setEnabled(bookSelected && userLogged);
        editButton.setEnabled(bookSelected && userLogged);
        removeButton.setEnabled(bookSelected && userLogged);

        if (!userLogged) {
            createButton.setToolTipText("Você precisa estar logado para criar reviews.");
            editButton.setToolTipText("Você precisa estar logado para editar reviews.");
            removeButton.setToolTipText("Você precisa estar logado para remover reviews.");
        } else if (!bookSelected) {
            String tip = "Nenhum livro selecionado. Use 'Selecionar Livro (ID)' primeiro.";
            createButton.setToolTipText(tip);
            editButton.setToolTipText(tip);
            removeButton.setToolTipText(tip);
        } else {
            createButton.setToolTipText(null);
            editButton.setToolTipText(null);
            removeButton.setToolTipText(null);
        }
    }

    /** Solicita ID do livro e seleciona-o. */
    private void selectBook() {
        String idStr = JOptionPane.showInputDialog(this, "Digite o ID do livro:", "Selecionar Livro", JOptionPane.QUESTION_MESSAGE);
        if (idStr == null) return;
        try {
            int id = Integer.parseInt(idStr);
            Book b = bookController.findById(id);
            if (b == null) {
                JOptionPane.showMessageDialog(this, "Livro não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
            currentBook = b;
            bookStatusLabel.setText("Livro selecionado: " + b.getName() + " (ID " + b.getId() + ")");
            populateReviewList();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
        updateButtonState();
    }

    /** Popula o JList com as reviews do livro atual. */
    private void populateReviewList() {
        listModel.clear();
        detailArea.setText("");
        if (currentBook == null) {
            listModel.addElement(null); // não ideal, mas evita vazio total; renderer trata nulo
            return;
        }

        List<Review> reviews = currentBook.getReviews();
        if (reviews == null || reviews.isEmpty()) {
            // mostra mensagem no detailArea
            detailArea.setText("Nenhuma review neste livro.");
            return;
        }

        for (Review r : reviews) {
            listModel.addElement(r);
        }
        // Seleciona o primeiro item por padrão
        if (!listModel.isEmpty()) {
            reviewList.setSelectedIndex(0);
        }
    }

    /** Exibe os detalhes da review selecionada (no painel direito). */
    private void showSelectedReviewDetails() {
        Review r = getSelectedReview();
        if (r == null) {
            detailArea.setText(currentBook == null ? "Selecione um livro." : "Selecione uma review.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(r.getId()).append("\n");
        sb.append("Autor: ").append(r.getAuthor() != null ? r.getAuthor().getUsername() : "anon").append("\n");
        sb.append("Rank: ").append(r.getRank()).append("/5\n");
        sb.append("\nComentário:\n");
        sb.append(r.getComment() == null ? "" : r.getComment()).append("\n");

        detailArea.setText(sb.toString());
        detailArea.setCaretPosition(0);
    }

    /** Retorna a review selecionada na JList (ou null). */
    private Review getSelectedReview() {
        Review r = reviewList.getSelectedValue();
        return r;
    }

    /** Lista as reviews como texto (JOptionPane) — mantém funcionalidade textual antiga. */
    private void listReviewsAsText() {
        if (!ensureBookSelected()) return;
        List<Review> reviews = currentBook.getReviews();
        if (reviews == null || reviews.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhuma review neste livro.", "Reviews", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (Review r : reviews) {
            sb.append("--------------------\n");
            sb.append(r.toString()).append("\n");
            sb.append("--------------------\n\n");
        }
        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setCaretPosition(0);
        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(600, 400));
        JOptionPane.showMessageDialog(this, sp, "Reviews do Livro", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Garante que exista um livro selecionado (mostra aviso caso contrário). */
    private boolean ensureBookSelected() {
        if (currentBook == null) {
            JOptionPane.showMessageDialog(this, "Nenhum livro selecionado. Use 'Selecionar Livro (ID)' primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    /** Cria uma nova review (usa usuário logado). */
    private void createReview() {
        if (!ensureBookSelected()) return;
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Você precisa estar logado para criar uma review.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField commentField = new JTextField();
        JTextField rankField = new JTextField();

        panel.add(new JLabel("Comentário:"));
        panel.add(commentField);
        panel.add(new JLabel("Score (0-5):"));
        panel.add(rankField);

        int res = JOptionPane.showConfirmDialog(this, panel, "Criar Nova Review", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String comment = commentField.getText();
        Double rank = 0d;
        try {
            rank = Double.parseDouble(rankField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Score inválido, usando 0.", "Aviso", JOptionPane.WARNING_MESSAGE);
            rank = 0d;
        }

        Review r = reviewController.create(currentBook, comment, rank);
        if (r != null) {
            JOptionPane.showMessageDialog(this, "Review criada (ID " + r.getId() + ").", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            populateReviewList();
            findAndSelectReview(r);
        } else {
            JOptionPane.showMessageDialog(this, "Não foi possível criar a review.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Edita a review selecionada — caso não haja seleção pede ID (mantém comportamento antigo). */
    private void editReview() {
        if (!ensureBookSelected()) return;
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Você precisa estar logado para editar uma review.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Review target = getSelectedReview();
        if (target == null) {
            // fallback: pedir ID
            String idStr = JOptionPane.showInputDialog(this, "ID da review para editar:", "Editar Review", JOptionPane.QUESTION_MESSAGE);
            if (idStr == null) return;
            try {
                int id = Integer.parseInt(idStr);
                target = reviewController.findById(currentBook, id);
                if (target == null) {
                    JOptionPane.showMessageDialog(this, "Review não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // verifica autoria
        if (target.getAuthor() == null || user.getUsername() == null || !user.getUsername().equals(target.getAuthor().getUsername())) {
            JOptionPane.showMessageDialog(this, "Você só pode editar suas próprias reviews.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField commentField = new JTextField(target.getComment());
        JTextField rankField = new JTextField(String.valueOf(target.getRank()));

        panel.add(new JLabel("Comentário:"));
        panel.add(commentField);
        panel.add(new JLabel("Score (0-5):"));
        panel.add(rankField);

        int res = JOptionPane.showConfirmDialog(this, panel, "Editar Review (ID " + target.getId() + ")", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String newComment = commentField.getText();
        Double newRank = null;
        try {
            newRank = Double.parseDouble(rankField.getText());
        } catch (NumberFormatException ex) {
            // se inválido, manter null (controller decide)
            newRank = null;
        }

        reviewController.update(currentBook, target, newComment.isBlank() ? null : newComment, newRank);
        JOptionPane.showMessageDialog(this, "Review atualizada!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        populateReviewList();
        findAndSelectReview(target);
    }

    /** Remove a review — seleção preferencial, senão pede ID. */
    private void removeReview() {
        if (!ensureBookSelected()) return;
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Você precisa estar logado para remover uma review.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Review target = getSelectedReview();
        if (target == null) {
            String idStr = JOptionPane.showInputDialog(this, "ID da review para remover:", "Remover Review", JOptionPane.QUESTION_MESSAGE);
            if (idStr == null) return;
            try {
                int id = Integer.parseInt(idStr);
                target = reviewController.findById(currentBook, id);
                if (target == null) {
                    JOptionPane.showMessageDialog(this, "Review não encontrada.", "Erro", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        // verifica autoria
        if (target.getAuthor() == null || user.getUsername() == null || !user.getUsername().equals(target.getAuthor().getUsername())) {
            JOptionPane.showMessageDialog(this, "Você só pode remover suas próprias reviews.", "Acesso Negado", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover esta review?\n" + (target.getComment() == null ? "" : target.getComment()),
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) return;

        reviewController.delete(currentBook, target);
        JOptionPane.showMessageDialog(this, "Review removida!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
        populateReviewList();
    }

    /** Encontra o nó (item) correspondente à review e seleciona-a na JList. */
    private void findAndSelectReview(Review target) {
        if (target == null) return;
        for (int i = 0; i < listModel.size(); i++) {
            Review r = listModel.get(i);
            if (r != null && r.equals(target)) {
                reviewList.setSelectedIndex(i);
                reviewList.ensureIndexIsVisible(i);
                return;
            }
        }
    }

    // --- Implementação UserObserver ---
    @Override
    public void onUserChanged(User newUser) {
        this.user = newUser;
        updateButtonState();
    }

    @Override
    public User getUser() {
        return user;
    }

    // --- Implementação ReviewView ---
    @Override
    public void showMenu() {
        setVisible(true);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            // Sempre atualizar estado ao abrir
            updateButtonState();
            if (currentBook != null) populateReviewList();
        }
        super.setVisible(visible);
    }
}
