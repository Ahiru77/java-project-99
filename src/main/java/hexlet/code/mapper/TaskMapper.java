package hexlet.code.mapper;

import hexlet.code.dto.TaskDTO;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.model.Label;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.dto.TaskCreateDTO;

import hexlet.code.model.TaskStatus;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class TaskMapper {

    @Autowired
    private TaskStatusRepository taskStatusRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    @Mapping(source = "labels", target = "labels",qualifiedByName = "findLabelsByTask")
    public abstract TaskDTO map(Task model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "assigneeId", target = "assignee",qualifiedByName = "findUserById")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "findStatusBySlug")
    @Mapping(source = "labels", target = "labels",qualifiedByName = "findLabelsByName")
    public abstract Task map(TaskCreateDTO model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "assigneeId", target = "assignee",qualifiedByName = "findUserById")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "findStatusBySlug")
    @Mapping(source = "labels", target = "labels",qualifiedByName = "findLabelsByName")
    public abstract Task map(TaskDTO model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "assigneeId", target = "assignee",qualifiedByName = "findUserById")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "findStatusBySlug")
    @Mapping(source = "labels", target = "labels",qualifiedByName = "findLabelsByName")
    public abstract Task map(TaskUpdateDTO model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "assigneeId", target = "assignee",qualifiedByName = "findUserById")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "findStatusBySlug")
    @Mapping(source = "labels", target = "labels",qualifiedByName = "findLabelsByName")
    public abstract void update(TaskUpdateDTO update, @MappingTarget Task destination);
	
    @Named("findStatusBySlug")
    protected TaskStatus findStatusBySlug(String slug) {
        return taskStatusRepository.findBySlug(slug).orElse(null);
    }

    @Named("findUserById")
    protected User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Named("findLabelsByTask")
    protected Set<String> findLabelsByTask(Set<Label> labels) {
        if (labels == null) {
            return new HashSet<>();
        }
        Set<String> set = new HashSet<>();
        for (Label label : labels) {
            set.add(label.getName());
        }
        return set;
    }

    @Named("findLabelsByName")
    protected Set<Label> findLabelsByName(Set<String> set) {
        if (set == null) {
            return new HashSet<>();
        }
        Set<Label> labels = new HashSet<>();
        for (String name : set) {
            labelRepository.findByName(name).ifPresent(labels::add);
        }
        return labels;
    }
}	
