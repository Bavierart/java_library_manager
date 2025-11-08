package view.gui;

import controller.*;
import model.Book;
import model.Shelf;
import model.User;
import view.ShelfView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class ShelfViewGui extends JDialog implements ShelfView, UserObserver {

    private final ShelfController shelfController = AppController.getController().shelves();
    private final BookController bookController = AppController.getController().books();

    private User user = shelfController.getUser();

    private final DefaultListModel<Shelf> shelfListModel = new DefaultListModel<>();
    private final JList<Shelf> shelfList = new JList<>(shelfListModel);

    private final DefaultListModel<Book> bookListModel = new DefaultListModel<>();
    private final JList<Book> bookList = new JList<>(bookListModel);

    private final JButton createButton = new JButton("Criar Estante");
    private final JButton removeButton = new JButton("Remover Estante");
    private final JButton addBookButton = new JButton("Adicionar Livro");
    private final JButton removeBookButton = new JButton("Remover Livro");
    private final JButton closeButton = new JButton("Voltar");

    public ShelfViewGui() {
        setTitle("Gerenciamento de Estantes");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout(10, 10));

        JLabel headerLabel = new JLabel("Usuário: " + (user != null ? user.getUsername() : "Nenhum"));
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        add(headerLabel, BorderLayout.NORTH);

        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Estantes"));
        shelfList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        shelfList.setCellRenderer(new ShelfListRenderer());
        leftPanel.add(new JScrollPane(shelfList), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Livros da Estante Selecionada"));
        bookList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        bookList.setCellRenderer(new BookListRenderer());
        rightPanel.add(new JScrollPane(bookList), BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setResizeWeight(0.4);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(createButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(addBookButton);
        buttonPanel.add(removeBookButton);
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);

        shelfList.addListSelectionListener(e -> updateBookList());
        createButton.addActionListener(e -> createShelf());
        removeButton.addActionListener(e -> removeShelf());
        addBookButton.addActionListener(e -> addBookToShelf());
        removeBookButton.addActionListener(e -> removeBookFromShelf());
        closeButton.addActionListener(e -> setVisible(false));

        refreshShelfList();
    }

    private void refreshShelfList() {
        shelfListModel.clear();
        List<Shelf> shelves = shelfController.listAll();
        for (Shelf s : shelves) shelfListModel.addElement(s);
        bookListModel.clear();
    }

    private void updateBookList() {
        bookListModel.clear();
        Shelf selectedShelf = shelfList.getSelectedValue();
        if (selectedShelf == null) return;
        List<Book> books = shelfController.getBooks(selectedShelf.getId());
        for (Book b : books) bookListModel.addElement(b);
    }

    private void createShelf() {
        String name = JOptionPane.showInputDialog(this, "Nome da nova estante:", "Criar Estante", JOptionPane.QUESTION_MESSAGE);
        if (name != null && !name.isBlank()) {
            shelfController.create(name.trim());
            refreshShelfList();
        }
    }

    private void removeShelf() {
        Shelf selectedShelf = shelfList.getSelectedValue();
        if (selectedShelf == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma estante primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Remover estante \"" + selectedShelf.getName() + "\"?", "Confirmar", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            shelfController.deleteById(selectedShelf.getId());
            refreshShelfList();
        }
    }

    private void addBookToShelf() {
        Shelf selectedShelf = shelfList.getSelectedValue();
        if (selectedShelf == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma estante primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<Book> allBooks = bookController.listAll();
        if (allBooks.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nenhum livro disponível.", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Book selectedBook = (Book) JOptionPane.showInputDialog(this,
                "Selecione um livro para adicionar:",
                "Adicionar Livro",
                JOptionPane.PLAIN_MESSAGE,
                null,
                allBooks.toArray(),
                allBooks.getFirst());

        if (selectedBook != null) {
            shelfController.addBook(selectedShelf.getId(), selectedBook);
            updateBookList();
        }
    }

    private void removeBookFromShelf() {
        Shelf selectedShelf = shelfList.getSelectedValue();
        Book selectedBook = bookList.getSelectedValue();

        if (selectedShelf == null || selectedBook == null) {
            JOptionPane.showMessageDialog(this, "Selecione uma estante e um livro primeiro.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Remover o livro \"" + selectedBook.getName() + "\" desta estante?",
                "Confirmar Remoção", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            shelfController.removeBookById(selectedShelf.getId(), selectedBook.getId());
            updateBookList();
        }
    }


    private static class ShelfListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Shelf s) {
                setText("ID: " + s.getId() + " - " + s.getName());
            }
            return c;
        }
    }

    private static class BookListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Book b) {
                setText("ID: " + b.getId() + " - " + b.getName());
            }
            return c;
        }
    }


    @Override
    public void onUserChanged(User newUser) {
        this.user = newUser;
    }

    @Override
    public User getUser() {
        return user;
    }

    @Override
    public void showMenu() {
        setVisible(true);
    }

    @Override
    public void setVisible(boolean visible) {
        if (!visible) {
            super.setVisible(false);
            return;
        }
        if (user == null) {
            JOptionPane.showMessageDialog(this, "Nenhum usuário logado. Acesso negado.", "Erro", JOptionPane.ERROR_MESSAGE);
            super.setVisible(false);
        } else {
            setTitle("Estantes de " + user.getUsername());
            refreshShelfList();
            super.setVisible(true);
        }
    }
}
