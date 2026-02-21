package hexlet.code.service.impl;

import hexlet.code.service.TaskService;
import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.NotFoundException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.repository.TaskRepository;
import hexlet.code.specification.TaskSpecification;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskRepository repository;
    private final TaskMapper taskMapper;
    private final TaskSpecification specBuilder;

    @Override
    public TaskDTO create(TaskCreateDTO data) {
        var task = taskMapper.map(data);
        repository.save(task);
        return taskMapper.map(task);
    }

    @Override
    public List<TaskDTO> index(TaskParamsDTO params) {
        var spec = specBuilder.build(params);
        var tasks = repository.findAll(spec);
        return tasks.stream()
                .map(taskMapper::map)
                .toList();
    }

    @Override
    public TaskDTO show(Long id) {
        var task = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Задача с id " + id + " не найдена"));
        return taskMapper.map(task);
    }

    @Override
    public TaskDTO update(TaskUpdateDTO data, Long id) {
        var task = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Задача с id " + id + " не найдена"));
        taskMapper.update(data, task);
        repository.save(task);
        return taskMapper.map(task);
    }

    @Override
    public void destroy(Long id) {
        var task = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Задача с id " + id + " не найдена"));
        repository.delete(task);
    }
}
