package gestcode.server.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * DTO (Data Transfer Object) per a la creació d'un llibre.
 * S'utilitza per rebre i validar les dades de la petició REST.
 *
 * @author Jordi Verdalet Carrera
 */
public class BookCreateRequestDTO {

    @NotBlank(message = "L'ISBN no pot estar buit")
    @Size(max = 20, message = "L'ISBN no pot tenir més de 20 caràcters")
    private String isbn;

    @NotBlank(message = "El títol no pot estar buit")
    @Size(max = 255, message = "El títol no pot tenir més de 255 caràcters")
    private String title;

    @NotBlank(message = "L'autor no pot estar buit")
    @Size(max = 255, message = "L'autor no pot tenir més de 255 caràcters")
    private String author;

    @NotNull(message = "L'any de publicació és obligatori")
    private Integer year;

    @NotBlank(message = "El gènere no pot estar buit")
    @Size(max = 100, message = "El gènere no pot tenir més de 100 caràcters")
    private String genre;

    @NotNull(message = "El nombre de pàgines és obligatori")
    @Min(value = 1, message = "Hi ha d'haver com a mínim 1 pàgina")
    private Integer pages;

    @NotBlank(message = "L'idioma no pot estar buit")
    @Size(max = 50, message = "L'idioma no pot tenir més de 50 caràcters")
    private String language;

    @NotBlank(message = "La descripció no pot estar buida")
    private String description;

    @NotNull(message = "La quantitat és obligatòria")
    @Min(value = 0, message = "La quantitat no pot ser negativa")
    private Integer quantity;

    // Getters i Setters

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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getPages() {
        return pages;
    }

    public void setPages(Integer pages) {
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
