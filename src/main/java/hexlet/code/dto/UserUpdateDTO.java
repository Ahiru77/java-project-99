package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import org.openapitools.jackson.nullable.JsonNullable;

@Setter
@Getter
public class UserUpdateDTO {

    @Email private JsonNullable<String> email;

    @NotNull private JsonNullable<String> firstName;

    @NotNull private JsonNullable<String> lastName;

    @Size(min = 3) private JsonNullable<String> password;
}
