package hexlet.code.controller;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.dto.TaskParamsDTO;
import hexlet.code.repository.TaskRepository;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.BadRequestException;
import hexlet.code.mapper.TaskMapper;
import hexlet.code.specification.TaskSpecification;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {
    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private TaskSpecification specBuilder;

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    TaskDTO create(@Valid @RequestBody TaskCreateDTO data) {
        var taskData = taskMapper.map(data);
        repository.save(taskData);
        return taskMapper.map(taskData);
    }

    @GetMapping({"", "/"})
    ResponseEntity<List<TaskDTO>> index(TaskParamsDTO params) {
        var spec = specBuilder.build(params);
        var tasks = repository.findAll(spec);
        var result = tasks.stream().map(taskMapper::map).toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(result);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<TaskDTO> show(@PathVariable Long id) {
        var task = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var taskDTO = taskMapper.map(task);
        return ResponseEntity.status(200).body(taskDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<TaskDTO> update(@Valid @RequestBody TaskUpdateDTO taskData, @PathVariable Long id) {
        var task = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        taskMapper.update(taskData, task);
        repository.save(task);
        var taskDTO = taskMapper.map(task);
        return ResponseEntity.status(200).body(taskDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
