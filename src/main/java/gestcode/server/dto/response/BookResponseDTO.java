package gestcode.server.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.data.domain.Page;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (Data Transfer Object) de resposta d'un llibre.
 * Conté totes les dades públiques de l'entitat de la base de dades.
 * El camp {@code myRating} conté la puntuació de l'usuari autenticat per a
 * aquest llibre, o {@code null} si l'usuari no ha puntuat el llibre o no
 * s'ha autenticat.
 *
 * @author Jordi Verdalet Carrera
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
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
    private Double myRating;
    private String imageUrl;
    private Page<CommentResponseDTO> comments;
    private LocalDateTime createdAt;

    public BookResponseDTO() {
    }

    /**
     * Constructor sense el camp myRating (per mantenir compatibilitat).
     *
     * @param id          L'identificador del llibre.
     * @param isbn        L'ISBN del llibre.
     * @param title       El títol del llibre.
     * @param author      L'autor del llibre.
     * @param year        L'any de publicació.
     * @param genre       El gènere literari.
     * @param pages       El nombre de pàgines.
     * @param language    L'idioma del llibre.
     * @param description La descripció del llibre.
     * @param quantity    La quantitat de còpies disponibles.
     * @param rating      La mitjana de puntuació global del llibre.
     * @param createdAt   La data de creació del registre.
     * @author Jordi Verdalet Carrera
     */
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
        this.imageUrl = null;
        this.myRating = null;
        this.createdAt = createdAt;
    }

    /**
     * Constructor complet amb el camp myRating.
     *
     * @param id          L'identificador del llibre.
     * @param isbn        L'ISBN del llibre.
     * @param title       El títol del llibre.
     * @param author      L'autor del llibre.
     * @param year        L'any de publicació.
     * @param genre       El gènere literari.
     * @param pages       El nombre de pàgines.
     * @param language    L'idioma del llibre.
     * @param description La descripció del llibre.
     * @param quantity    La quantitat de còpies disponibles.
     * @param rating      La mitjana de puntuació global del llibre.
     * @param myRating    La puntuació de l'usuari autenticat (pot ser null).
     * @param createdAt   La data de creació del registre.
     * @author Jordi Verdalet Carrera
     */
    public BookResponseDTO(Long id, String isbn, String title, String author, int year, String genre, int pages,
            String language, String description, int quantity, double rating, Double myRating, String imageUrl, LocalDateTime createdAt) {
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
        this.myRating = myRating;
        this.imageUrl = imageUrl;
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

    /**
     * Obté la puntuació de l'usuari autenticat per a aquest llibre.
     * Retorna null si l'usuari no ha puntuat el llibre o no està autenticat.
     *
     * @return La puntuació de l'usuari, o null.
     * @author Jordi Verdalet Carrera
     */
    public Double getMyRating() {
        return myRating;
    }

    /**
     * Estableix la puntuació de l'usuari autenticat per a aquest llibre.
     *
     * @param myRating La puntuació de l'usuari, o null.
     * @author Jordi Verdalet Carrera
     */
    public void setMyRating(Double myRating) {
        this.myRating = myRating;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Obté la pàgina de comentaris associats al llibre.
     *
     * @return La pàgina de comentaris.
     * @author Jordi Verdalet Carrera
     */
    public Page<CommentResponseDTO> getComments() {
        return comments;
    }

    /**
     * Estableix la pàgina de comentaris associats al llibre.
     *
     * @param comments La pàgina de comentaris.
     * @author Jordi Verdalet Carrera
     */
    public void setComments(Page<CommentResponseDTO> comments) {
        this.comments = comments;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
