package hexlet.code.controller;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.util.UserUtils;
import hexlet.code.repository.UserRepository;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.exception.BadRequestException;
import hexlet.code.mapper.UserMapper;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class UsersController {
    @Autowired
    private UserRepository repository;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUtils userUtils;


    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    UserDTO create(@Valid @RequestBody UserCreateDTO userData) {
        var user = userMapper.map(userData);
        repository.save(user);
        return userMapper.map(user);
    }

    @GetMapping("/users")
    ResponseEntity<List<UserDTO>> index() {
        var users = repository.findAll();
        var result = users.stream().map(userMapper::map).toList();
        return ResponseEntity.ok().header("X-Total-Count", String.valueOf(users.size())).body(result);
    }

    @GetMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<UserDTO> show(@PathVariable Long id) {
        var user = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var userDTO = userMapper.map(user);
        return ResponseEntity.status(200).body(userDTO);
    }

    @PutMapping("/users/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<UserDTO> update(@RequestBody UserUpdateDTO userData, @PathVariable Long id) {
        var user = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        if (!userUtils.getCurrentUser().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        userMapper.update(userData, user);
        repository.save(user);
        var userDTO = userMapper.map(user);
        return ResponseEntity.status(200).body(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> destroy(@PathVariable Long id) {
        var user = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        repository.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
