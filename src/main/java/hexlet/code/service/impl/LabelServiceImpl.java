package hexlet.code.service.impl;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.NotFoundException;
import hexlet.code.mapper.LabelMapper;
import hexlet.code.repository.LabelRepository;
import hexlet.code.service.LabelService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository repository;
    private final LabelMapper labelMapper;

    @Override
    public LabelDTO create(LabelCreateDTO data) {
        var label = labelMapper.map(data);
        repository.save(label);
        return labelMapper.map(label);
    }

    @Override
    public List<LabelDTO> index() {
        var labels = repository.findAll();
        return labels.stream()
                .map(labelMapper::map)
                .toList();
    }

    @Override
    public LabelDTO show(Long id) {
        var label = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Метка с id " + id + " не найдена"));
        return labelMapper.map(label);
    }

    @Override
    public LabelDTO update(LabelUpdateDTO data, Long id) {
        var label = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Метка с id " + id + " не найдена"));
        labelMapper.update(data, label);
        repository.save(label);
        return labelMapper.map(label);
    }

    @Override
    public void destroy(Long id) {
        var label = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Метка с id " + id + " не найдена"));
        repository.delete(label);
    }
}
