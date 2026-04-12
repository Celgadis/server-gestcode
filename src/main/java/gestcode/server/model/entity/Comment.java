/*
* Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
* Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package gestcode.server.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

/**
 * Entitat JPA que representa un comentari
 * 
 * @author Jordi Verdalet Carrera
 */
@Entity
@Table(name = "comments",
    uniqueConstraints = @UniqueConstraint(
        name = "uk_user_book_comment",
        columnNames = {"user_id", "book_id"}
    ))
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Usuari que ha realitzat el comentari.
     * Fetch LAZY per evitar càrregues innecessàries.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Llibre del que s'ha realitzat el comentari.
     * Fetch LAZY per evitar càrregues innecessàries.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private String content;
    
    /**
     * Data i hora de creació del comentari. S'assigna automàticament.
     */
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Data i hora de l'última actualització. S'actualitza automàticament.
     */
    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Constructor per defecte.
     * 
     * @author Jordi Verdalet Carrera
     */
    public Comment() {
    }

    /**
     * Constructor amb tots els camps obligatoris.
     * 
     * @param user      Usuari que ha realitzat el comentari.
     * @param book      Llibre del que s'ha realitzat el comentari.
     * @param content   Contingut del comentari.
     * @author Jordi Verdalet Carrera
     */
    public Comment(User user, Book book, String content) {
        this.user = user;
        this.book = book;
        this.content = content;
    }
    /**
     * Obté l'identificador únic del comentari.
     *
     * @return L'identificador
     * @author Jordi Verdalet Carrera
     */
    public Long getId() {
        return id;
    }

    /**
     * Estableix l'identificador únic del comentari.
     *
     * @param id L'identificador
     * @author Jordi Verdalet Carrera
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obté l'usuari associat al comentari.
     *
     * @return L'entitat User
     * @author Jordi Verdalet Carrera
     */
    public User getUser() {
        return user;
    }

    /**
     * Estableix l'usuari associat al comentari.
     *
     * @param user L'entitat User
     * @author Jordi Verdalet Carrera
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * Obté el llibre associat al comentari.
     *
     * @return L'entitat Book
     * @author Jordi Verdalet Carrera
     */
    public Book getBook() {
        return book;
    }

    /**
     * Estableix el llibre associat al comentari.
     *
     * @param book L'entitat Book
     * @author Jordi Verdalet Carrera
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Obté el valor del comentari.
     *
     * @return El comentari
     * @author Jordi Verdalet Carrera
     */
    public String getContent() {
        return content;
    }

    /**
     * Estableix el valor del comentari.
     *
     * @param content El comentari
     * @author Jordi Verdalet Carrera
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Obté la data de creació del comentari.
     *
     * @return La data de creació
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Estableix la data de creació del comentari.
     *
     * @param createdAt La data de creació
     * @author Jordi Verdalet Carrera
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    /**
     * Obté la data de l'última actualització del comentari.
     *
     * @return La data d'actualització
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Estableix la data de l'última actualització del comentari.
     *
     * @param updatedAt La data d'actualització
     * @author Jordi Verdalet Carrera
     */
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
