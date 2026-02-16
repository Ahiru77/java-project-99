package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.util.UserUtils;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.BadRequestException;
import hexlet.code.mapper.UserMapper;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UserRepository repository;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUtils userUtils;


    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    UserDTO create(@Valid @RequestBody UserCreateDTO userData) {
        var user = userMapper.map(userData);
        repository.save(user);
        return userMapper.map(user);
    }

    @GetMapping({"", "/"})
    ResponseEntity<List<UserDTO>> index() {
        var users = repository.findAll();
        var result = users.stream().map(userMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(users.size())).body(result);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<UserDTO> show(@PathVariable Long id) {
        var user = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var userDTO = userMapper.map(user);
        return ResponseEntity.status(200).body(userDTO);
    }

    @PreAuthorize("@userUtils.getCurrentUser().getId() == #id")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<UserDTO> update(@Valid @RequestBody UserUpdateDTO userData, @PathVariable Long id) {
        var user = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
//        if (!userUtils.getCurrentUser().getId().equals(id)) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
//        }
        userMapper.update(userData, user);
        repository.save(user);
        var userDTO = userMapper.map(user);
        return ResponseEntity.status(200).body(userDTO);
    }

    @PreAuthorize("@userUtils.getCurrentUser().getId() == #id")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
//        if (taskRepository.existsByAssigneeId(id)) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).build();
//        }
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
