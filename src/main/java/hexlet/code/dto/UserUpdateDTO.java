package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;

@Setter
@Getter
public class UserUpdateDTO {

    @Email private String email;

    @NotNull private String firstName;

    @NotNull private String lastName;

    @Size(min = 3) private String password;
}
