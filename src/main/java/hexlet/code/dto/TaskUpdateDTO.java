package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
public class TaskUpdateDTO {

    @JsonProperty("taskLabelIds")
    private Set<Long> labels;

    private int index;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    @NotBlank
    private String title;

    private String content;

    @NotNull
    private String status;
}
