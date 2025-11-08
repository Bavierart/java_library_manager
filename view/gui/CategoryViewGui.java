package view.gui;

import controller.AppController;
import controller.BookController;
import controller.CategoryController;
import model.Book;
import model.Category;
import view.CategoryView;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.util.List;


public final class CategoryViewGui extends JDialog implements CategoryView {

    private final CategoryController categoryController = AppController.getController().categories();
    private final BookController bookController = AppController.getController().books();

    private final JTree categoryTree;
    private final DefaultMutableTreeNode rootNode;
    private final DefaultTreeModel treeModel;
    private final JTextArea bookListArea;

    public CategoryViewGui() {
        setTitle("Gerenciamento de Categorias");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setModal(true);
        setLayout(new BorderLayout(10, 10));

        rootNode = new DefaultMutableTreeNode("Categorias");
        treeModel = new DefaultTreeModel(rootNode);
        categoryTree = new JTree(treeModel);
        categoryTree.setRootVisible(true);
        JScrollPane treeScrollPane = new JScrollPane(categoryTree);

        bookListArea = new JTextArea();
        bookListArea.setEditable(false);
        bookListArea.setBorder(BorderFactory.createTitledBorder("Livros na Categoria"));
        JScrollPane bookScrollPane = new JScrollPane(bookListArea);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, treeScrollPane, bookScrollPane);
        splitPane.setDividerLocation(300);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 4, 5, 5)); // 2 linhas de botões

        JButton refreshButton = new JButton("Listar/Atualizar"); // 1) e 10)
        JButton createButton = new JButton("Criar Categoria"); // 2)
        JButton editButton = new JButton("Editar Selecionada"); // 3)
        JButton removeButton = new JButton("Remover Selecionada"); // 4) e 9)
        JButton addBookButton = new JButton("Add Livro à Categoria"); // 5)
        JButton removeBookButton = new JButton("Rem Livro da Categoria"); // 6)
        JButton addSubCatButton = new JButton("Add Subcategoria"); // 8)
        JButton closeButton = new JButton("Voltar"); // 0)

        buttonPanel.add(refreshButton);
        buttonPanel.add(createButton);
        buttonPanel.add(editButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(addSubCatButton);
        buttonPanel.add(addBookButton);
        buttonPanel.add(removeBookButton);
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> populateTree());
        createButton.addActionListener(e -> createCategory());
        editButton.addActionListener(e -> editCategory());
        removeButton.addActionListener(e -> removeCategory());
        addBookButton.addActionListener(e -> addBookToCategory());
        removeBookButton.addActionListener(e -> removeBookFromCategory());
        addSubCatButton.addActionListener(e -> addSubCategory());
        closeButton.addActionListener(e -> setVisible(false));

        categoryTree.addTreeSelectionListener(new TreeSelectionListener() {
            @Override
            public void valueChanged(TreeSelectionEvent e) {
                listBooksOfSelectedCategory();
            }
        });
    }

    /**
     * Popula a JTree com categorias e subcategorias.
     * Substitui listCategories() [cite: 43] e listSubCategories()[cite: 81].
     */
    private void populateTree() {
        rootNode.removeAllChildren();

        List<Category> topLevelCategories = categoryController.listAll();
        if (topLevelCategories.isEmpty()) {
        } else {
            for (Category cat : topLevelCategories) {
                DefaultMutableTreeNode catNode = new DefaultMutableTreeNode(cat);
                rootNode.add(catNode);
                populateChildren(catNode, cat);
            }
        }
        treeModel.reload(rootNode);
        for (int i = 0; i < categoryTree.getRowCount(); i++) {
            categoryTree.expandRow(i);
        }
    }

    /**
     * Método auxiliar recursivo para popular subcategorias.
     */
    private void populateChildren(DefaultMutableTreeNode parentNode, Category parentCategory) {
        List<Category> subCategories = categoryController.listSubCategories(parentCategory);
        if (subCategories == null || subCategories.isEmpty()) {
            return;
        }
        for (Category sub : subCategories) {
            DefaultMutableTreeNode subNode = new DefaultMutableTreeNode(sub);
            parentNode.add(subNode);
            populateChildren(subNode, sub); // Recursão
        }
    }

    /**
     * Obtém a categoria selecionada na árvore.
     * @return A Categoria, ou null se nenhuma (ou a raiz) estiver selecionada.
     */
    private Category getSelectedCategory() {
        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) categoryTree.getLastSelectedPathComponent();
        if (selectedNode == null || selectedNode.getUserObject() instanceof String) {
            // Nada selecionado, ou é o nó raiz "Categorias"
            return null;
        }
        return (Category) selectedNode.getUserObject();
    }

    /**
     * Atualiza a JTextArea com os livros da categoria selecionada.
     * Equivalente a listBooksOfCategory() [cite: 66-71].
     */
    private void listBooksOfSelectedCategory() {
        bookListArea.setText("");
        Category cat = getSelectedCategory();
        if (cat == null) {
            bookListArea.setText("Selecione uma categoria na árvore.");
            return;
        }

        List<Book> books = categoryController.listBooksInCategory(cat);
        if (books.isEmpty()) {
            bookListArea.setText("Nenhum livro nesta categoria.");
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("Livros em '").append(cat.getName()).append("':\n");
            for (Book b : books) {
                sb.append(" - ").append(b.getName()).append(" (ID ").append(b.getId()).append(")\n");
            }
            bookListArea.setText(sb.toString());
        }
    }

    /**
     * Pede um nome e cria uma categoria de nível superior.
     * Equivalente a createCategory() [cite: 46-47].
     */
    private void createCategory() {
        String name = JOptionPane.showInputDialog(this, "Nome da nova categoria:",
                "Criar Categoria", JOptionPane.QUESTION_MESSAGE);

        if (name != null && !name.isBlank()) {
            Category c = categoryController.create(name);
            JOptionPane.showMessageDialog(this, "Categoria criada: " + c, "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            populateTree(); // Atualiza a árvore
        }
    }

    /**
     * Edita a categoria selecionada.
     * Equivalente a editCategory() [cite: 47-53].
     */
    private void editCategory() {
        Category cat = getSelectedCategory();
        if (cat == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma categoria para editar.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String newName = JOptionPane.showInputDialog(this, "Novo nome para '" + cat.getName() + "':",
                cat.getName());

        if (newName != null && !newName.isBlank()) {
            categoryController.update(cat.getId(), newName);
            JOptionPane.showMessageDialog(this, "Categoria atualizada.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            populateTree(); // Atualiza a árvore
        } else {
            JOptionPane.showMessageDialog(this, "Sem alterações.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Remove a categoria (ou subcategoria) selecionada.
     * Equivalente a removeCategory() [cite: 53-55] e removeSubCategory() [cite: 79-80].
     */
    private void removeCategory() {
        Category cat = getSelectedCategory();
        if (cat == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma categoria para remover.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Tem certeza que deseja remover '" + cat.getName() + "'?\n(Subcategorias também serão afetadas)",
                "Remover Categoria", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = categoryController.deleteById(cat.getId());
            JOptionPane.showMessageDialog(this, ok ? "Removida." : "Não encontrada.");
            populateTree();
            listBooksOfSelectedCategory(); // Limpa a lista de livros
        }
    }

    /**
     * Adiciona um livro (por ID) à categoria selecionada.
     * Equivalente a addBookToCategory() [cite: 56-60].
     */
    private void addBookToCategory() {
        Category cat = getSelectedCategory();
        if (cat == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma categoria.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookIdStr = JOptionPane.showInputDialog(this, "ID do livro para adicionar:",
                "Adicionar Livro", JOptionPane.QUESTION_MESSAGE);
        try {
            int bookId = Integer.parseInt(bookIdStr);
            Book book = bookController.findById(bookId);
            if (book == null) {
                JOptionPane.showMessageDialog(this, "Livro não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean added = categoryController.addBookToCategory(cat, book);
            JOptionPane.showMessageDialog(this, added ? "Livro adicionado." : "Falha ou já adicionado.");
            listBooksOfSelectedCategory(); // Atualiza a lista de livros

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Remove um livro (por ID) da categoria selecionada.
     * Equivalente a removeBookFromCategory() [cite: 60-65].
     */
    private void removeBookFromCategory() {
        Category cat = getSelectedCategory();
        if (cat == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma categoria.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookIdStr = JOptionPane.showInputDialog(this, "ID do livro para remover:",
                "Remover Livro", JOptionPane.QUESTION_MESSAGE);

        try {
            int bookId = Integer.parseInt(bookIdStr);
            Book book = bookController.findById(bookId);
            if (book == null) {
                JOptionPane.showMessageDialog(this, "Livro não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean removed = categoryController.removeBookFromCategory(cat, book);
            JOptionPane.showMessageDialog(this, removed ? "Livro removido." : "Falha ao remover.");
            listBooksOfSelectedCategory();

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Adiciona uma subcategoria à categoria selecionada.
     * Equivalente a addSubCategory() [cite: 77-78].
     */
    private void addSubCategory() {
        Category parent = getSelectedCategory();
        if (parent == null) {
            JOptionPane.showMessageDialog(this, "Por favor, selecione uma categoria pai.", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String name = JOptionPane.showInputDialog(this, "Nome da nova subcategoria:",
                "Adicionar Subcategoria", JOptionPane.QUESTION_MESSAGE);

        if (name != null && !name.isBlank()) {
            boolean added = categoryController.addSubCategory(parent, name);
            JOptionPane.showMessageDialog(this, added ? "Subcategoria adicionada." : "Falha ao adicionar.");
            populateTree();
            findAndSelectNode(parent);
        }
    }

    /** Método auxiliar para encontrar e selecionar um nó após a atualização da árvore. */
    private void findAndSelectNode(Category catToSelect) {
        DefaultMutableTreeNode node = findNode(rootNode, catToSelect);
        if (node != null) {
            TreePath path = new TreePath(node.getPath());
            categoryTree.setSelectionPath(path);
            categoryTree.scrollPathToVisible(path);
        }
    }

    private DefaultMutableTreeNode findNode(DefaultMutableTreeNode root, Category category) {
        if (root.getUserObject().equals(category)) {
            return root;
        }
        for (int i = 0; i < root.getChildCount(); i++) {
            DefaultMutableTreeNode result = findNode((DefaultMutableTreeNode) root.getChildAt(i), category);
            if (result != null) {
                return result;
            }
        }
        return null;
    }

    @Override
    public void showMenu() {
        setVisible(true);
    }

    /**
     * Implementação da interface CategoryView.
     * Atualiza a árvore e torna a janela visível.
     */
    @Override
    public void setVisible(boolean visible) {
        if (visible) {
            populateTree();
            listBooksOfSelectedCategory();
        }
        super.setVisible(visible);
    }
}