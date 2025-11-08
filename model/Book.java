package model;

import java.util.ArrayList;
import java.util.Collection;

public final class Book extends AbstractCrudObject implements Ranked {

    private static int nextId = 1;
    private String name;
    private Double price;
    private String author;
    private String publisher;
    private Integer pages;
    private final ArrayList<Review> reviews = new ArrayList<>();
    private final ArrayList<Category> categories = new ArrayList<>();

    private Book(int id, String name, Double price, String author, String publisher, Integer pages) {
        super(id);
        setName(name);
        setPrice(price);
        setAuthor(author);
        setPublisher(publisher);
        setPages(pages);
    }

    public ArrayList<Category> getCategories() {
        return categories;
    }

    // -------- Builder Pattern --------
    public static class Builder {
        private String name = "Sem nome";
        private Double price = 0d;
        private String author = "Desconhecido";
        private String publisher = "Desconhecida";
        private Integer pages = 0;
        private Integer id = null;

        public Builder setName(String name) {
            this.name = (name == null || name.isBlank()) ? "Sem nome" : name.trim();
            return this;
        }

        public Builder setPrice(Double price) {
            this.price = (price == null || price < 0) ? 0d : price;
            return this;
        }

        public Builder setAuthor(String author) {
            this.author = (author == null || author.isBlank()) ? "Desconhecido" : author.trim();
            return this;
        }

        public Builder setPublisher(String publisher) {
            this.publisher = (publisher == null || publisher.isBlank()) ? "Desconhecida" : publisher.trim();
            return this;
        }

        public Builder setPages(Integer pages) {
            this.pages = (pages == null || pages < 0) ? 0 : pages;
            return this;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Book build() {
            int finalId = (id != null) ? id : nextId++;
            return new Book(finalId, name, price, author, publisher, pages);
        }
    }

    public static void resetIdCounter(int next) { nextId = next; }

    public String getName() { return name; }
    public Double getPrice() { return price; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }
    public Integer getPages() { return pages; }

    public String getScoreDisplay() {
        if (reviews.isEmpty()) return "(?)";
        double avg = reviews.stream().mapToDouble(Review::getScore).average().orElse(0);
        return String.format("%.1f", avg);
    }

    public void setName(String name) {
        name = (name == null ? "" : name.trim());
        this.name = name.isBlank() ? "Sem nome" : name;
    }

    public void setPrice(Double price) {
        this.price = (price == null || price < 0) ? 0d : price;
    }

    public void setAuthor(String author) {
        this.author = (author == null || author.isBlank()) ? "Desconhecido" : author.trim();
    }

    public void setPublisher(String publisher) {
        this.publisher = (publisher == null || publisher.isBlank()) ? "Desconhecida" : publisher.trim();
    }

    public void setPages(Integer pages) {
        this.pages = (pages == null || pages < 0) ? 0 : pages;
    }

    public ArrayList<Review> getReviews() { return reviews; }

    public void addReview(Review review) {
        if (review != null && !reviews.contains(review))
            reviews.add(review);
    }

    public void removeReview(Review review) {
        reviews.remove(review);
    }

    public void addCategory(Category category) {
        if (category == null) return;
        if (!categories.contains(category)) {
            categories.add(category);
        }
    }

    public void removeCategory(Category category) {
        if (category == null) return;
        categories.remove(category);
    }

    @Override
    public Double getScore() {
        if (reviews.isEmpty()) return 0d;
        return reviews.stream().mapToDouble(Review::getScore).average().orElse(0);
    }

    // -------- toString --------
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(id)
                .append("\nNome: ").append(name)
                .append("\nAutor: ").append(author)
                .append("\nEditora: ").append(publisher)
                .append("\nPáginas: ").append(pages)
                .append("\nPreço: ").append(price)
                .append("\nScore: ").append(getScoreDisplay())
                .append("\nReviews: ").append(reviews.size());

        if (!categories.isEmpty()) {
            sb.append("\nCategorias: ");
            for (Category c : categories) {
                sb.append(c.getName()).append(", ");
            }
            sb.setLength(sb.length() - 2);
        } else {
            sb.append("\nCategorias: (nenhuma)");
        }

        return sb.toString();
    }
}
