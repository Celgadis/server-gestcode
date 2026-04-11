package gestcode.server.dto.response;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) de resposta d'un llibre.
 * Conté totes les dades públiques de l'entitat de la base de dades.
 *
 * @author Jordi Verdalet Carrera
 */
public class BookResponseDTO {

    private Long id;
    private String isbn;
    private String title;
    private String author;
    private int year;
    private String genre;
    private int pages;
    private String language;
    private String description;
    private int quantity;
    private double rating;
    private LocalDateTime createdAt;

    public BookResponseDTO() {
    }

    public BookResponseDTO(Long id, String isbn, String title, String author, int year, String genre, int pages,
            String language, String description, int quantity, double rating, LocalDateTime createdAt) {
        this.id = id;
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.year = year;
        this.genre = genre;
        this.pages = pages;
        this.language = language;
        this.description = description;
        this.quantity = quantity;
        this.rating = rating;
        this.createdAt = createdAt;
    }

    // Getters i Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

}
