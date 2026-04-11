/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
  * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
   */
package gestcode.server.model.entity;

import gestcode.server.model.enums.Role;
import gestcode.server.model.enums.UserStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import org.hibernate.annotations.CreationTimestamp;

/**
 * Entitat JPA que representa un usuari del sistema.
 * 
 * @author Jordi Verdalet Carrera
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName1;

    private String lastName2;

    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    private boolean enabled = false;

    @Enumerated(EnumType.STRING)
    private Role role;

    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Constructor per defecte.
     * 
     * @author Jordi Verdalet Carrera
     */
    public User() {
    }

    /**
     * Constructor amb tots els camps obligatoris.
     * 
     * @param username  Nom d'usuari
     * @param password  Contrasenya
     * @param firstName Nom
     * @param lastName1 Primer cognom
     * @param lastName2 Segon cognom
     * @param email     Correu electrònic
     * @param status    Estat de l'usuari
     * @param enabled   Habilitat o no
     * @param role      Rol dins del sistema
     * @author Jordi Verdalet Carrera
     */
    public User(String username, String password, String firstName, String lastName1, String lastName2, String email,
            UserStatus status, boolean enabled, Role role) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName1 = lastName1;
        this.lastName2 = lastName2;
        this.email = email;
        this.status = status;
        this.enabled = enabled;
        this.role = role;
    }

    /**
     * Obté l'identificador únic de l'usuari.
     * 
     * @return L'identificador
     * @author Jordi Verdalet Carrera
     */
    public Long getId() {
        return id;
    }

    /**
     * Estableix l'identificador únic de l'usuari.
     * 
     * @param id L'identificador
     * @author Jordi Verdalet Carrera
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Obté el nom d'usuari de sistema.
     * 
     * @return El nom d'usuari
     * @author Jordi Verdalet Carrera
     */
    public String getUsername() {
        return username;
    }

    /**
     * Estableix el nom d'usuari de sistema.
     * 
     * @param username El nom d'usuari
     * @author Jordi Verdalet Carrera
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Obté la contrasenya encriptada.
     * 
     * @return La contrasenya
     * @author Jordi Verdalet Carrera
     */
    public String getPassword() {
        return password;
    }

    /**
     * Estableix la contrasenya encriptada.
     * 
     * @param password La contrasenya
     * @author Jordi Verdalet Carrera
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Obté el nom de pila.
     * 
     * @return El nom
     * @author Jordi Verdalet Carrera
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Estableix el nom de pila.
     * 
     * @param firstName El nom
     * @author Jordi Verdalet Carrera
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Obté el primer cognom.
     * 
     * @return El primer cognom
     * @author Jordi Verdalet Carrera
     */
    public String getLastName1() {
        return lastName1;
    }

    /**
     * Estableix el primer cognom.
     * 
     * @param lastName1 El primer cognom
     * @author Jordi Verdalet Carrera
     */
    public void setLastName1(String lastName1) {
        this.lastName1 = lastName1;
    }

    /**
     * Obté el segon cognom.
     * 
     * @return El segon cognom
     * @author Jordi Verdalet Carrera
     */
    public String getLastName2() {
        return lastName2;
    }

    /**
     * Estableix el segon cognom.
     * 
     * @param lastName2 El segon cognom
     * @author Jordi Verdalet Carrera
     */
    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }

    /**
     * Obté el correu electrònic.
     * 
     * @return El correu electrònic
     * @author Jordi Verdalet Carrera
     */
    public String getEmail() {
        return email;
    }

    /**
     * Estableix el correu electrònic.
     * 
     * @param email El correu electrònic
     * @author Jordi Verdalet Carrera
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Obté l'estat actual de l'usuari.
     * 
     * @return L'estat
     * @author Jordi Verdalet Carrera
     */
    public UserStatus getStatus() {
        return status;
    }

    /**
     * Estableix l'estat actual de l'usuari.
     * 
     * @param status L'estat
     * @author Jordi Verdalet Carrera
     */
    public void setStatus(UserStatus status) {
        this.status = status;
    }

    /**
     * Obté si l'usuari està habilitat o no.
     * 
     * @return Fals si no està actiu
     * @author Jordi Verdalet Carrera
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Estableix l'activació de l'usuari.
     * 
     * @param enabled si l'usuari està actiu
     * @author Jordi Verdalet Carrera
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Obté el rol de l'usuari dins de l'aplicació.
     * 
     * @return El rol
     * @author Jordi Verdalet Carrera
     */
    public Role getRole() {
        return role;
    }

    /**
     * Estableix el rol de l'usuari dins de l'aplicació.
     * 
     * @param role El rol
     * @author Jordi Verdalet Carrera
     */
    public void setRole(Role role) {
        this.role = role;
    }

    /**
     * Obté la data en que l'usuari ha sigut registrat.
     * 
     * @return La data de registre
     * @author Jordi Verdalet Carrera
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Estableix la data en que l'usuari ha sigut registrat.
     * 
     * @param createdAt La data de registre
     * @author Jordi Verdalet Carrera
     */
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
