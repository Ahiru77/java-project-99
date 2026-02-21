package hexlet.code.service.impl;

import hexlet.code.service.TaskStatusService;
import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.NotFoundException;
import hexlet.code.mapper.TaskStatusMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {

    private final TaskStatusRepository repository;
    private final TaskStatusMapper taskStatusMapper;
    private final TaskRepository taskRepository;

    @Override
    public TaskStatusDTO create(TaskStatusCreateDTO data) {
        var taskStatus = taskStatusMapper.map(data);
        repository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @Override
    public List<TaskStatusDTO> index() {
        var taskStatuses = repository.findAll();
        return taskStatuses.stream()
                .map(taskStatusMapper::map)
                .toList();
    }

    @Override
    public TaskStatusDTO show(Long id) {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Статус с id " + id + " не найден"));
        return taskStatusMapper.map(taskStatus);
    }

    @Override
    public TaskStatusDTO update(TaskStatusUpdateDTO data, Long id) {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Статус с id " + id + " не найден"));
        taskStatusMapper.update(data, taskStatus);
        repository.save(taskStatus);
        return taskStatusMapper.map(taskStatus);
    }

    @Override
    public void destroy(Long id) {
        var taskStatus = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Статус с id " + id + " не найден"));

        if (taskRepository.existsByTaskStatusId(id)) {
            throw new RuntimeException("Невозможно удалить, статус используется");
        }

        repository.delete(taskStatus);
    }
}
