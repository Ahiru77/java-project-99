package hexlet.code.service;
import hexlet.code.dto.UserCreateDTO;
import hexlet.code.dto.UserDTO;
import hexlet.code.dto.UserUpdateDTO;
import java.util.List;

public interface UserService {
    UserDTO create(UserCreateDTO data);
    List<UserDTO> index();
    UserDTO show(Long id);
    UserDTO update(UserUpdateDTO data, Long id);
    void destroy(Long id);
}
