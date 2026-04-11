package gestcode.server.controller;

import gestcode.server.dto.request.BookCreateRequestDTO;
import gestcode.server.dto.request.BookUpdateRequestDTO;
import gestcode.server.dto.response.BookResponseDTO;
import gestcode.server.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de l'API per a l'entitat Book.
 *
 * @author Jordi Verdalet Carrera
 */
@RestController
@RequestMapping("/api/books")
@Tag(name = "Llibres", description = "Endpoints de gestió de llibres de la biblioteca")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    /**
     * Endpoint per crear un llibre nou. Només accessible per a administradors.
     *
     * @param bookDTO Dades del llibre a crear.
     * @return Dades del llibre creat.
     */
    @Operation(summary = "Crear llibre", description = "Afegeix un nou llibre a la biblioteca. (Requereix ADMIN)")
    @ApiResponse(responseCode = "201", description = "Llibre creat correctament")
    @ApiResponse(responseCode = "400", description = "Dades invàlides o ISBN duplicat")
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> createBook(@Valid @RequestBody BookCreateRequestDTO bookDTO) {
        BookResponseDTO response = bookService.createBook(bookDTO);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Endpoint per actualitzar un llibre existent. Només accessible per a administradors.
     *
     * @param id L'identificador del llibre.
     * @param bookDTO Dades del llibre a actualitzar.
     * @return Dades del llibre actualitzat.
     */
    @Operation(summary = "Actualitzar llibre", description = "Edita la informació d'un llibre existent. (Requereix ADMIN)")
    @ApiResponse(responseCode = "200", description = "Llibre actualitzat correctament")
    @ApiResponse(responseCode = "404", description = "Llibre no trobat")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<BookResponseDTO> updateBook(@PathVariable Long id, @Valid @RequestBody BookUpdateRequestDTO bookDTO) {
        BookResponseDTO response = bookService.updateBook(id, bookDTO);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per esborrar físicament un llibre de la base de dades. Només accessible per a administradors.
     *
     * @param id L'identificador del llibre.
     * @return Resposta buida amb OK.
     */
    @Operation(summary = "Esborrar llibre", description = "Elimina un llibre físicament. (Requereix ADMIN)")
    @ApiResponse(responseCode = "204", description = "Llibre esborrat correctament")
    @ApiResponse(responseCode = "404", description = "Llibre no trobat")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Endpoint per recuperar les dades d'un llibre concret.
     *
     * @param id L'identificador del llibre.
     * @return Les dades del llibre.
     */
    @Operation(summary = "Obtenir llibre", description = "Accedeix a les dades públiques d'un llibre. (Accessible per usuaris i administradors)")
    @ApiResponse(responseCode = "200", description = "Dades obtingudes")
    @ApiResponse(responseCode = "404", description = "Llibre no trobat")
    @GetMapping("/{id}")
    public ResponseEntity<BookResponseDTO> getBookById(@PathVariable Long id) {
        BookResponseDTO response = bookService.getBookById(id);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Endpoint per obtenir la llista de llibres segons filtres i paginació.
     *
     * @param title Filtre opcional per títol.
     * @param author Filtre opcional per autor.
     * @param year Filtre opcional per any de publicació.
     * @param genre Filtre opcional per gènere.
     * @param language Filtre opcional per idioma.
     * @param rating Filtre opcional per puntuació mínima.
     * @param pageable Dades de paginació i ordenació.
     * @return Pàgina de llibres.
     */
    @Operation(summary = "Llistar llibres", description = "Llista de llibres amb paginació i filtres. (Accessible per usuaris i administradors)")
    @ApiResponse(responseCode = "200", description = "Llistat obtingut correctament")
    @GetMapping
    public ResponseEntity<Page<BookResponseDTO>> getBooks(
            @Parameter(description = "Filtre per títol (conté)") @RequestParam(required = false) String title,
            @Parameter(description = "Filtre per autor (conté)") @RequestParam(required = false) String author,
            @Parameter(description = "Filtre per any") @RequestParam(required = false) Integer year,
            @Parameter(description = "Filtre per gènere (conté)") @RequestParam(required = false) String genre,
            @Parameter(description = "Filtre per idioma (exacte)") @RequestParam(required = false) String language,
            @Parameter(description = "Filtre per puntuació (mínima)") @RequestParam(required = false) Double rating,
            @Parameter(hidden = true) Pageable pageable) {
        
        Page<BookResponseDTO> response = bookService.getBooks(title, author, year, genre, language, rating, pageable);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
