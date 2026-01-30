package hexlet.code.controller;

import hexlet.code.dto.TaskCreateDTO;
import hexlet.code.dto.TaskDTO;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.exception.BadRequestException;
import hexlet.code.mapper.TaskMapper;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {
    @Autowired
    private TaskRepository repository;

    @Autowired
    private TaskMapper taskMapper;

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    TaskDTO create(@Valid @RequestBody TaskCreateDTO taskStatusData) {
        var taskData = taskMapper.map(taskStatusData);
        repository.save(taskData);
        return taskMapper.map(taskData);
    }

    @GetMapping({"", "/"})
    ResponseEntity<List<TaskDTO>> index(){
        var tasks = repository.findAll();
        var result = tasks.stream().map(taskMapper::map).toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(tasks.size()))
                .body(result);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<TaskDTO> show(@PathVariable Long id) {
        var taskStatus = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var taskStatusDTO = taskMapper.map(taskStatus);
        return ResponseEntity.status(200).body(taskStatusDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<TaskDTO> update(@RequestBody TaskUpdateDTO taskData, @PathVariable Long id) {
        var taskStatus = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        taskMapper.update(taskData, taskStatus);
        repository.save(taskStatus);
        var taskStatusDTO = taskMapper.map(taskStatus);
        return ResponseEntity.status(200).body(taskStatusDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        var task = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
