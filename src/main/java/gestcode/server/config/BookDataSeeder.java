package gestcode.server.config;

import gestcode.server.dto.request.BookCreateRequestDTO;
import gestcode.server.dto.request.CommentRequestDTO;
import gestcode.server.dto.request.RatingRequestDTO;
import gestcode.server.dto.response.BookResponseDTO;
import gestcode.server.repository.BookRepository;
import gestcode.server.repository.UserRepository;
import gestcode.server.service.BookService;
import gestcode.server.service.CommentService;
import gestcode.server.service.UserBookRatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Seeder per a la càrrega de dades de prova de llibres, comentaris i
 * puntuacions.
 *
 * @author Jordi Verdalet Carrera
 */
@Component
@Order(2)
public class BookDataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(BookDataSeeder.class);

    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final BookService bookService;
    private final CommentService commentService;
    private final UserBookRatingService ratingService;

    @Value("${app.seed.testdata:false}")
    private boolean seedTestData;

    public BookDataSeeder(BookRepository bookRepository,
            UserRepository userRepository,
            BookService bookService,
            CommentService commentService,
            UserBookRatingService ratingService) {
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.bookService = bookService;
        this.commentService = commentService;
        this.ratingService = ratingService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (!seedTestData) {
            logger.info("Book test data seeding is disabled. Skipping.");
            return;
        }

        seedBooks();
    }

    private void seedBooks() {
        logger.info("Starting book data seeding...");

        List<BookCreateRequestDTO> booksToSeed = new ArrayList<>();

        booksToSeed.add(createBookDTO("9788415954309", "La plaça del Diamant", "Mercè Rodoreda", 1962, "Classic", 256,
                "Català", "La història de la Colometa en la Barcelona de la postguerra.", 10));
        booksToSeed.add(createBookDTO("9788415002628", "Jo confesso", "Jaume Cabré", 2011, "Novel·la", 1008, "Català",
                "Una reflexió sobre el mal a la història de la humanitat a través de la vida d'Adrià Ardèvol.", 5));
        booksToSeed.add(createBookDTO("9788415642879", "Incerta glòria", "Joan Sales", 1956, "Guerra Civil", 650, "Català",
                "Una de les millors novel·les sobre la Guerra Civil espanyola.", 8));
        booksToSeed.add(createBookDTO("9788429760774", "Terra baixa", "Àngel Guimerà", 1897, "Teatre", 180, "Català",
                "L'eterna lluita entre el bé i el mal en el món rural.", 15));
        booksToSeed.add(createBookDTO("9788429762112", "El quadern gris", "Josep Pla", 1966, "Diari", 900, "Català",
                "El diari personal de l'autor que descriu la societat de l'època.", 4));
        booksToSeed.add(createBookDTO("9788433998736", "Canto jo i la muntanya balla", "Irene Solà", 2019, "Contemporani",
                192, "Català", "Una novel·la on parlen les dones, els homes, els núvols i els bolets.", 12));
        booksToSeed.add(createBookDTO("9788492403164", "Solitud", "Víctor Català", 1905, "Modernisme", 320, "Català",
                "La lluita de la Mila per la seva llibertat espiritual en un entorn hostil.", 6));
        booksToSeed.add(createBookDTO("9788496735545", "L'estrangera", "Maria Barbal", 1985, "Narrativa", 160, "Català",
                "Una història sobre el desarrelament i el retorn als orígens.", 20));
        booksToSeed.add(createBookDTO("9788466415477", "Victus", "Albert Sánchez Piñol", 2012, "Històrica", 608, "Català",
                "La caiguda de Barcelona el 1714 explicada per Martí Zuviría.", 10));
        booksToSeed.add(createBookDTO("9788415954705", "Mirall trencat", "Mercè Rodoreda", 1974, "Saga familiar", 450,
                "Català", "La crònica d'una família barcelonina des de finals del XIX fins a la Guerra Civil.", 7));

        int count = 0;
        for (BookCreateRequestDTO dto : booksToSeed) {
            BookResponseDTO bookResponse;
            if (!bookRepository.existsByIsbn(dto.getIsbn())) {
                bookResponse = bookService.createBook(dto);
                logger.info("Book created: {} (ISBN: {})", bookResponse.getTitle(), bookResponse.getIsbn());
            } else {
                logger.info("Book already exists: {} (ISBN: {})", dto.getTitle(), dto.getIsbn());
                // Busquem el llibre existent per obtenir el seu ID per a les interaccions
                bookResponse = bookRepository.findByIsbn(dto.getIsbn())
                    .map(b -> new BookResponseDTO(b.getId(), b.getIsbn(), b.getTitle(), b.getAuthor(), b.getYear(), b.getGenre(), b.getPages(), b.getLanguage(), b.getDescription(), b.getQuantity(), b.getRating(), b.getCreatedAt()))
                    .orElse(null);
            }

            if (bookResponse != null && count < 5) {
                seedInteractions(bookResponse);
            }
            count++;
        }
    }

    private BookCreateRequestDTO createBookDTO(String isbn, String title, String author, int year, String genre,
            int pages, String language, String description, int quantity) {
        BookCreateRequestDTO dto = new BookCreateRequestDTO();
        dto.setIsbn(isbn);
        dto.setTitle(title);
        dto.setAuthor(author);
        dto.setYear(year);
        dto.setGenre(genre);
        dto.setPages(pages);
        dto.setLanguage(language);
        dto.setDescription(description);
        dto.setQuantity(quantity);
        return dto;
    }

    private void seedInteractions(BookResponseDTO book) {
        if (userRepository.existsByUsername("test1")) {
            createRatingAndComment("test1", book, 4.0, "M'ha agradat molt aquest clàssic. Molt recomanable!");
        }
        if (userRepository.existsByUsername("test2")) {
            createRatingAndComment("test2", book, 5.0, "Una obra mestra imprescindible de la literatura catalana.");
        }
    }

    private void createRatingAndComment(String username, BookResponseDTO book, double ratingValue, String content) {
        // Create/Update Rating via service (recalculates automatically)
        RatingRequestDTO ratingDTO = new RatingRequestDTO(book.getId(), ratingValue);
        ratingService.upsertRating(ratingDTO, username);

        // Create/Update Comment via service
        CommentRequestDTO commentDTO = new CommentRequestDTO(book.getId(), content);
        commentService.upsertComment(commentDTO, username);
    }
}
