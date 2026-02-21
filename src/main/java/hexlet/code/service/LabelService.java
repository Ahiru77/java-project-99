package hexlet.code.service;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import java.util.List;

public interface LabelService {
    LabelDTO create(LabelCreateDTO data);
    List<LabelDTO> index();
    LabelDTO show(Long id);
    LabelDTO update(LabelUpdateDTO data, Long id);
    void destroy(Long id);
}
