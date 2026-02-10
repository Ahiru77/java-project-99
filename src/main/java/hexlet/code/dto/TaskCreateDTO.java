package hexlet.code.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Setter
@Getter
public class TaskCreateDTO {

    private int index;

    @JsonProperty("assignee_id")
    private Long assigneeId;

    private String title;

    private String content;

    private String status;

    @JsonProperty("taskLabelIds")
    private Set<Long> labels;
}

