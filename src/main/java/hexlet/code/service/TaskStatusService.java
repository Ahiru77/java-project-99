package hexlet.code.service;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import java.util.List;

public interface TaskStatusService {
    TaskStatusDTO create(TaskStatusCreateDTO data);
    List<TaskStatusDTO> index();
    TaskStatusDTO show(Long id);
    TaskStatusDTO update(TaskStatusUpdateDTO data, Long id);
    void destroy(Long id);
}
