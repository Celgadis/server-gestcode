package gestcode.server.service.impl;

import gestcode.server.dto.request.CommentRequestDTO;
import gestcode.server.dto.response.CommentResponseDTO;
import gestcode.server.model.entity.Book;
import gestcode.server.model.entity.User;
import gestcode.server.model.entity.Comment;
import gestcode.server.repository.BookRepository;
import gestcode.server.repository.CommentRepository;
import gestcode.server.repository.UserRepository;
import gestcode.server.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Implementació del servei de comentaris d'usuaris sobre llibres.
 * Gestiona l'operació upsert (crear o actualitzar) i els llistats amb
 * paginació.
 * Tots els llistats utilitzen consultes amb JOIN FETCH per evitar el problema
 * N+1.
 *
 * @author Jordi Verdalet Carrera
 */
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    /**
     * Constructor que injecta els repositoris i l'observer necessaris.
     *
     * @param commentRepository Repositori de puntuacions.
     * @param bookRepository    Repositori de llibres.
     * @param userRepository    Repositori d'usuaris.
     * @author Jordi Verdalet Carrera
     */
    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository,
            BookRepository bookRepository,
            UserRepository userRepository) {
        this.commentRepository = commentRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
    }

    /**
     * Crea o actualitza el comentari de l'usuari autenticat per a un llibre
     * concret.
     * Si ja existia un comentari per a la combinació usuari-llibre, l'actualitza.
     * En cas contrari, en crea un de nou.
     *
     * @param dto      Les dades del comentari (bookId i comment).
     * @param username El nom d'usuari autenticat extret del token JWT.
     * @return El comentari creat o actualitzat en format DTO.
     * @author Jordi Verdalet Carrera
     */
    @Override
    @Transactional
    public CommentResponseDTO upsertComment(CommentRequestDTO dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuari no trobat"));

        Book book = bookRepository.findById(dto.getBookId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat"));

        Optional<Comment> existing = commentRepository.findByUserIdAndBookId(user.getId(), book.getId());

        Comment comment;
        if (existing.isPresent()) {
            // Actualitzar comentari existent
            comment = existing.get();
            comment.setContent(dto.getContent());
        } else {
            // Crear nou comentari
            comment = new Comment(user, book, dto.getContent());
        }

        Comment saved = commentRepository.save(comment);

        return mapToDTO(saved);
    }

    /**
     * Retorna tots els comentaris de l'usuari autenticat, de forma paginada.
     * Utilitza JOIN FETCH per evitar el problema N+1.
     *
     * @param username El nom d'usuari autenticat extret del token JWT.
     * @param pageable Les dades de paginació.
     * @return Pàgina de comentaris de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public Page<CommentResponseDTO> getMyComments(String username, Pageable pageable) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuari no trobat"));

        return commentRepository.findByUserIdWithDetails(user.getId(), pageable)
                .map(this::mapToDTO);
    }

    /**
     * Retorna tots els comentaris d'un llibre concret, de forma paginada.
     * Utilitza JOIN FETCH per evitar el problema N+1.
     *
     * @param bookId   L'identificador del llibre.
     * @param pageable Les dades de paginació.
     * @return Pàgina de comentaris del llibre.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public Page<CommentResponseDTO> getCommentsByBook(Long bookId, Pageable pageable) {
        if (!bookRepository.existsById(bookId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Llibre no trobat");
        }
        return commentRepository.findByBookIdWithDetails(bookId, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Retorna tots els comentaris d'un usuari concret, de forma paginada.
     * Utilitza JOIN FETCH per evitar el problema N+1.
     *
     * @param userId   L'identificador de l'usuari.
     * @param pageable Les dades de paginació.
     * @return Pàgina de comentaris de l'usuari.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public Page<CommentResponseDTO> getCommentsByUser(Long userId, Pageable pageable) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuari no trobat");
        }
        return commentRepository.findByUserIdWithDetails(userId, pageable)
                .map(this::mapToDTO);
    }

    /**
     * Retorna tots els comentaris amb filtres opcionals per userId i/o bookId.
     * Tots els casos utilitzen consultes amb JOIN FETCH per evitar el problema N+1:
     * cap filtre → {@code findAllWithDetails}, un sol filtre → query
     * especialitzada,
     * ambdós filtres → {@code findByUserIdAndBookIdWithDetails}.
     *
     * @param userId   Filtre opcional per l'identificador de l'usuari.
     * @param bookId   Filtre opcional per l'identificador del llibre.
     * @param pageable Les dades de paginació.
     * @return Pàgina de comentaris filtrades.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public Page<CommentResponseDTO> getAllComments(Long userId, Long bookId, Pageable pageable) {
        if (userId != null && bookId == null) {
            return getCommentsByUser(userId, pageable);
        }
        if (bookId != null && userId == null) {
            return getCommentsByBook(bookId, pageable);
        }
        if (userId != null) {
            // Ambdós filtres: query JOIN FETCH específica per evitar N+1
            return commentRepository.findByUserIdAndBookIdWithDetails(userId, bookId, pageable)
                    .map(this::mapToDTO);
        }
        // Cap filtre: retornar totes amb JOIN FETCH
        return commentRepository.findAllWithDetails(pageable)
                .map(this::mapToDTO);
    }

    /**
     * Esborra un comentari de la base de dades.
     *
     * @param id L'identificador del comentari a eliminar.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public void deleteComment(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentari no trobat"));

        commentRepository.delete(comment);
    }

    /**
     * Elimina un comentari físicament de la base de dades utilitzant l'IDE del
     * l'usuari i del llibre.
     *
     * @param userId L'identificador de l'usuari.
     * @param bookId L'identificador del llibre.
     * @return L'identificador del comentari eliminat.
     * @author Jordi Verdalet Carrera
     */
    @Override
    public Long deleteCommentByUserAndBook(Long userId, Long bookId) {
        Comment comment = commentRepository.findByUserIdAndBookId(userId, bookId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Comentari no trobat"));

        Long commentId = comment.getId();
        commentRepository.delete(comment);
        return commentId;
    }

    /**
     * Mètode privat d'ajuda per transformar l'entitat Comment al DTO de
     * resposta.
     *
     * @param comment Entitat Comment.
     * @return El pertinent CommentResponseDTO preparat.
     * @author Jordi Verdalet Carrera
     */
    private CommentResponseDTO mapToDTO(Comment comment) {
        return new CommentResponseDTO(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getUsername(),
                comment.getBook().getId(),
                comment.getBook().getTitle(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt());
    }
}
