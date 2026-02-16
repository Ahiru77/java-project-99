package hexlet.code.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TaskStatusUpdateDTO {
    @NotBlank
    @Size(min = 1)
    private String name;

    @NotBlank
    @Column(unique = true)
    @Size(min = 1)
    private String slug;
}
