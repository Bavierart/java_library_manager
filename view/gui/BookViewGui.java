package view.gui;

import controller.AppController;
import controller.BookController;
import model.Book;
import view.BookView;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;

/**
 * Implementação gráfica (Swing) da BookView,
 * com a mesma lógica da CLI e visual moderno baseado em árvore e painel de detalhes.
 */
public final class BookViewGui extends JDialog implements BookView {

    private final BookController bookController = AppController.getController().books();

    private final DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("Livros");
    private final DefaultTreeModel treeModel = new DefaultTreeModel(rootNode);
    private final JTree bookTree = new JTree(treeModel);
    private final JTextArea detailArea = new JTextArea();

    public BookViewGui() {
        setTitle("Gerenciamento de Livros");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout(10, 10));

        // --- Painel Esquerdo: Árvore de Livros ---
        bookTree.setRootVisible(true);
        JScrollPane treeScroll = new JScrollPane(bookTree);

        // --- Painel Direito: Detalhes do Livro ---
        detailArea.setEditable(false);
        detailArea.setBorder(BorderFactory.createTitledBorder("Detalhes do Livro"));
        JScrollPane detailScroll = new JScrollPane(detailArea);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScroll, detailScroll);
        split.setDividerLocation(300);
        add(split, BorderLayout.CENTER);

        // --- Painel de Botões ---
        JPanel buttonPanel = new JPanel(new GridLayout(2, 3, 5, 5));
        JButton refreshBtn = new JButton("Listar/Atualizar");
        JButton createBtn = new JButton("Criar Livro");
        JButton editBtn = new JButton("Editar Selecionado");
        JButton removeBtn = new JButton("Remover Selecionado");
        JButton rankBtn = new JButton("Ver Score");
        JButton closeBtn = new JButton("Voltar");

        buttonPanel.add(refreshBtn);
        buttonPanel.add(createBtn);
        buttonPanel.add(editBtn);
        buttonPanel.add(removeBtn);
        buttonPanel.add(rankBtn);
        buttonPanel.add(closeBtn);
        add(buttonPanel, BorderLayout.SOUTH);

        // --- Ações dos botões ---
        refreshBtn.addActionListener(e -> populateTree());
        createBtn.addActionListener(e -> createBook());
        editBtn.addActionListener(e -> editBook());
        removeBtn.addActionListener(e -> removeBook());
        rankBtn.addActionListener(e -> showScore());
        closeBtn.addActionListener(e -> setVisible(false));

        // --- Ação de seleção na árvore ---
        bookTree.addTreeSelectionListener(this::showSelectedBookDetails);
        bookTree.setCellRenderer((tree, value, selected, expanded, leaf, row, hasFocus) -> {
            JLabel label = new JLabel();
            Object userObject = ((DefaultMutableTreeNode) value).getUserObject();

            if (userObject instanceof Book) {
                Book b = (Book) userObject;
                label.setText(b.getName()); // Mostra apenas o nome
            } else {
                label.setText(userObject.toString());
            }

            if (selected) {
                label.setOpaque(true);
                label.setBackground(UIManager.getColor("Tree.selectionBackground"));
                label.setForeground(UIManager.getColor("Tree.selectionForeground"));
            }

            return label;
        });

    }

    /** Atualiza a árvore com a lista de livros */
    private void populateTree() {
        rootNode.removeAllChildren();
        List<Book> books = bookController.listAll();
        if (books.isEmpty()) {
            rootNode.add(new DefaultMutableTreeNode("(Nenhum livro cadastrado)"));
        } else {
            for (Book b : books) {
                rootNode.add(new DefaultMutableTreeNode(b));
            }
        }
        treeModel.reload();
        for (int i = 0; i < bookTree.getRowCount(); i++) {
            bookTree.expandRow(i);
        }
    }

    /** Retorna o livro selecionado */
    private Book getSelectedBook() {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) bookTree.getLastSelectedPathComponent();
        if (node == null || !(node.getUserObject() instanceof Book)) return null;
        return (Book) node.getUserObject();
    }

    /** Mostra detalhes do livro selecionado */
    private void showSelectedBookDetails(TreeSelectionEvent e) {
        Book b = getSelectedBook();
        if (b == null) {
            detailArea.setText("Selecione um livro na lista.");
            return;
        }

        StringBuilder sb = new StringBuilder();
        sb.append("ID: ").append(b.getId()).append("\n");
        sb.append("Nome: ").append(b.getName()).append("\n");
        sb.append("Autor: ").append(b.getAuthor()).append("\n");
        sb.append("Editora: ").append(b.getPublisher()).append("\n");
        sb.append("Páginas: ").append(b.getPages()).append("\n");
        sb.append("Preço: R$ ").append(String.format("%.2f", b.getPrice())).append("\n");
        sb.append("Score: ").append(b.getScore()).append("\n");

        if (b.getReviews() != null)
            sb.append("\nAvaliações: ").append(b.getReviews().size()).append("\n");

        if (b.getCategories() != null && !b.getCategories().isEmpty()) {
            sb.append("\nCategorias:\n");
            b.getCategories().forEach(c -> sb.append(" - ").append(c.getName()).append("\n"));
        }

        detailArea.setText(sb.toString());
    }

    /** Criação de um novo livro */
    private void createBook() {
        JTextField nameField = new JTextField();
        JTextField authorField = new JTextField();
        JTextField publisherField = new JTextField();
        JTextField pagesField = new JTextField();
        JTextField priceField = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nome:"));
        panel.add(nameField);
        panel.add(new JLabel("Autor:"));
        panel.add(authorField);
        panel.add(new JLabel("Editora:"));
        panel.add(publisherField);
        panel.add(new JLabel("Páginas:"));
        panel.add(pagesField);
        panel.add(new JLabel("Preço (R$):"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Criar Livro",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText();
                String author = authorField.getText();
                String publisher = publisherField.getText();
                int pages = Integer.parseInt(pagesField.getText());
                double price = Double.parseDouble(priceField.getText());

                Book b = bookController.create(name, price, author, publisher, pages);
                JOptionPane.showMessageDialog(this, "Livro criado com sucesso: " + b.getName());
                populateTree();
                findAndSelectNode(b);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Edição do livro selecionado */
    private void editBook() {
        Book b = getSelectedBook();
        if (b == null) {
            JOptionPane.showMessageDialog(this, "Selecione um livro para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JTextField nameField = new JTextField(b.getName());
        JTextField authorField = new JTextField(b.getAuthor());
        JTextField publisherField = new JTextField(b.getPublisher());
        JTextField pagesField = new JTextField(String.valueOf(b.getPages()));
        JTextField priceField = new JTextField(String.valueOf(b.getPrice()));

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.add(new JLabel("Nome:"));
        panel.add(nameField);
        panel.add(new JLabel("Autor:"));
        panel.add(authorField);
        panel.add(new JLabel("Editora:"));
        panel.add(publisherField);
        panel.add(new JLabel("Páginas:"));
        panel.add(pagesField);
        panel.add(new JLabel("Preço (R$):"));
        panel.add(priceField);

        int result = JOptionPane.showConfirmDialog(this, panel, "Editar Livro",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String author = authorField.getText().trim();
                String publisher = publisherField.getText().trim();
                Integer pages = Integer.parseInt(pagesField.getText());
                Double price = Double.parseDouble(priceField.getText());

                bookController.update(b.getId(), name, price, author, publisher, pages);
                JOptionPane.showMessageDialog(this, "Livro atualizado com sucesso!");
                populateTree();
                findAndSelectNode(bookController.findById(b.getId()));
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Remoção de um livro */
    private void removeBook() {
        Book b = getSelectedBook();

        if (b == null) {
            JOptionPane.showMessageDialog(this, "Selecione um livro para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remover '" + b.getName() + "'?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = bookController.deleteById(b.getId());
            JOptionPane.showMessageDialog(this, ok ? "Removido com sucesso." : "Erro ao remover.");
            populateTree();
        }
    }

    /** Exibe o Score dos livros */
    private void showScore() {
        List<Book> books = bookController.listAll();
        if (books.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum livro cadastrado.");
            return;
        }

        StringBuilder sb = new StringBuilder("Score dos Livros:\n");
        books.stream()
                .sorted((a, b) -> b.getScore().compareTo(a.getScore()))
                .forEach(book -> sb.append(book.getScore()).append(" - ").append(book.getName()).append("\n"));

        JOptionPane.showMessageDialog(this, sb.toString(), "Score", JOptionPane.INFORMATION_MESSAGE);
    }

    /** Re-seleciona o livro na árvore após atualização */
    private void findAndSelectNode(Book bookToSelect) {
        DefaultMutableTreeNode node = findNode(rootNode, bookToSelect);
        if (node != null) {
            TreePath path = new TreePath(node.getPath());
            bookTree.setSelectionPath(path);
            bookTree.scrollPathToVisible(path);
        }
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, Book book) {
        if (root.getUserObject() instanceof Book && ((Book) root.getUserObject()).getId() == book.getId()) {
            return root;
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode result = findNode((DefaultMutableTreeNode) root.getChildAt(i), book);
            if (result != null) return result;
        }
        return null;
    }

    @Override
    public void showMenu() {
        setVisible(true);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            populateTree();
            detailArea.setText("Selecione um livro na lista.");
        }
        super.setVisible(visible);
    }
}
