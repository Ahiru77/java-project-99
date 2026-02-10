package hexlet.code.controller;

import hexlet.code.dto.TaskStatusCreateDTO;
import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.exception.BadRequestException;
import hexlet.code.mapper.TaskStatusMapper;
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
@RequestMapping("/api/task_statuses")
public class TaskStatusController {
    @Autowired
    private TaskStatusRepository repository;

    @Autowired
    private TaskStatusMapper taskStatusMapper;

    @Autowired
    private TaskRepository taskRepository;

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    TaskStatusDTO create(@Valid @RequestBody TaskStatusCreateDTO taskStatusData) {
        var taskData = taskStatusMapper.map(taskStatusData);
        repository.save(taskData);
        return taskStatusMapper.map(taskData);
    }

    @GetMapping({"", "/"})
    ResponseEntity<List<TaskStatusDTO>> index() {
        var taskStatuses = repository.findAll();
        var result = taskStatuses.stream().map(taskStatusMapper::map).toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(taskStatuses.size()))
                .body(result);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<TaskStatusDTO> show(@PathVariable Long id) {
        var taskStatus = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var taskStatusDTO = taskStatusMapper.map(taskStatus);
        return ResponseEntity.status(200).body(taskStatusDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<TaskStatusDTO> update(@RequestBody TaskStatusUpdateDTO taskData, @PathVariable Long id) {
        var taskStatus = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        taskStatusMapper.update(taskData, taskStatus);
        repository.save(taskStatus);
        var taskStatusDTO = taskStatusMapper.map(taskStatus);
        return ResponseEntity.status(200).body(taskStatusDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        if (taskRepository.existsByTaskStatusId(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
