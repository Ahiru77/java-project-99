package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreateDTO {

    @Email private String email;

    @NotBlank private String firstName;

    @NotBlank private String lastName;

    @Size(min = 3, max = 100) private String password;
}
