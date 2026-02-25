package hexlet.code.service.impl;

import hexlet.code.dto.UserCreateDTO;
import hexlet.code.exception.NotFoundException;
import hexlet.code.service.UserService;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import hexlet.code.mapper.UserMapper;
import hexlet.code.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    public UserDTO create(UserCreateDTO data) {
        var user = userMapper.map(data);
        repository.save(user);
        return userMapper.map(user);
    }

    @Override
    public List<UserDTO> index() {
        var users = repository.findAll();
        return users.stream().map(userMapper::map).toList();
    }

    @Override
    public UserDTO show(Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return userMapper.map(user);
    }

    @Override
    public UserDTO update(UserUpdateDTO data, Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        userMapper.update(data, user);
        repository.save(user);
        return userMapper.map(user);
    }

    @Override
    public void destroy(Long id) {
        var user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        repository.delete(user);
    }
}
