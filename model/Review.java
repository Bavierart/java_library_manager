package model;

import java.io.Serializable;
import java.util.Objects;

public final class Review extends AbstractCrudObject implements Ranked, Serializable, CrudObjectInterface {

    private static int nextId = 1;
    private final User author;
    private String comment;
    private Double Score;

    private Review(int id, String comment, Double Score, User author) {
        super(id);
        this.author = Objects.requireNonNull(author);
        setComment(comment);
        setScore(Score);
    }

    public String getComment() {
        return comment;
    }

    public Double getRank() {
        return Score;
    }

    public static class Builder {
        private String comment = "";
        private Double Score = 0d;
        private User author;
        private Integer id = null;

        public Builder setComment(String comment) {
            this.comment = (comment == null) ? "" : comment.trim();
            return this;
        }

        public Builder setScore(Double Score) {
            this.Score = (Score == null || Score < 0d || Score > 5d) ? 0d : Score;
            return this;
        }

        public Builder setAuthor(User author) {
            this.author = author;
            return this;
        }

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Review build() {
            if (author == null)
                throw new IllegalStateException("Review precisa ter um autor antes de build()");
            int finalId = (id != null) ? id : nextId++;
            return new Review(finalId, comment, Score, author);
        }
    }


    @Override
    public int getId() { return id; }
    public static void resetIdCounter(int next) { nextId = next; }
    public Double getScore() { return Score; }

    public void setComment(String comment) {
        this.comment = (comment == null) ? "" : comment.trim();
    }

    public User getAuthor() {
        return author;
    }

    public void setScore(Double rank) {
        if (rank < 0d || rank > 5d) this.Score = 0d;
        else this.Score = rank;
    }

    @Override
    public String toString() {
        return String.format("Review{id=%d, autor=%s, Score=%.1f'}",
                id, author.getUsername(), Score);
    }
}
