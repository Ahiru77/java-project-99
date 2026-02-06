package hexlet.code.controller;

import hexlet.code.dto.LabelCreateDTO;
import hexlet.code.dto.LabelDTO;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.exception.BadRequestException;
import hexlet.code.mapper.LabelMapper;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/labels")
public class LabelController {
    @Autowired
    private LabelRepository repository;

    @Autowired
    private LabelMapper labelMapper;

    @PostMapping({"", "/"})
    @ResponseStatus(HttpStatus.CREATED)
    LabelDTO create(@Valid @RequestBody LabelCreateDTO data) {
        var labelData = labelMapper.map(data);
        repository.save(labelData);
        return labelMapper.map(labelData);
    }

    @GetMapping({"", "/"})
    ResponseEntity<List<LabelDTO>> index(){
        var labels = repository.findAll();
        var result = labels.stream().map(labelMapper::map).toList();

        return ResponseEntity.ok()
                .header("X-Total-Count", String.valueOf(labels.size()))
                .body(result);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<LabelDTO> show(@PathVariable Long id) {
        var label = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        if (!repository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        var labelDTO = labelMapper.map(label);
        return ResponseEntity.status(200).body(labelDTO);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    ResponseEntity<LabelDTO> update(@RequestBody LabelUpdateDTO taskData, @PathVariable Long id) {
        var label = repository.findById(id).orElseThrow(() -> new BadRequestException("Not Found"));
        labelMapper.update(taskData, label);
        repository.save(label);
        var labelDTO = labelMapper.map(label);
        return ResponseEntity.status(200).body(labelDTO);
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