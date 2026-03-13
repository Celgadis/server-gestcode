package gestcode.server.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRegisterRequestDTO {

    @NotBlank(message = "El nom d'usuari no pot estar buit")
    @Size(min = 4, max = 50, message = "El nom d'usuari ha de tenir entre 4 i 50 caràcters")
    private String username;

    @NotBlank(message = "La contrasenya no pot estar buida")
    @Size(min = 6, max = 100, message = "La contrasenya ha de tenir almenys 6 caràcters")
    private String password;

    @NotBlank(message = "El nom no pot estar buit")
    private String firstName;

    @NotBlank(message = "El primer cognom no pot estar buit")
    private String lastName1;

    private String lastName2;

    @NotBlank(message = "El correu electrònic no pot estar buit")
    @Email(message = "Format de correu electrònic invàlid")
    private String email;

    public UserRegisterRequestDTO() {}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName1() {
        return lastName1;
    }

    public void setLastName1(String lastName1) {
        this.lastName1 = lastName1;
    }

    public String getLastName2() {
        return lastName2;
    }

    public void setLastName2(String lastName2) {
        this.lastName2 = lastName2;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
