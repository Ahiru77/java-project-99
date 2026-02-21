package hexlet.code.service;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import java.util.List;

public interface TaskService {
    TaskDTO create(TaskCreateDTO data);
    List<TaskDTO> index(TaskParamsDTO params);
    TaskDTO show(Long id);
    TaskDTO update(TaskUpdateDTO data, Long id);
    void destroy(Long id);
}
