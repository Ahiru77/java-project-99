package hexlet.code.mapper;

import hexlet.code.dto.TaskDTO;
import hexlet.code.model.Task;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.dto.TaskUpdateDTO;
import hexlet.code.dto.TaskCreateDTO;

import hexlet.code.model.TaskStatus;
import org.mapstruct.*;
import org.springframework.beans.factory.annotation.Autowired;

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

    @Mapping(source = "assignee.id", target = "assigneeId")
    @Mapping(source = "taskStatus.slug", target = "status")
    @Mapping(source = "name", target = "title")
    @Mapping(source = "description", target = "content")
    public abstract TaskDTO map(Task model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "assigneeId", target = "assignee",qualifiedByName = "findUserById")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "findStatusBySlug")
    public abstract Task map(TaskCreateDTO model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "assigneeId", target = "assignee",qualifiedByName = "findUserById")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "findStatusBySlug")
    public abstract Task map(TaskDTO model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "assigneeId", target = "assignee",qualifiedByName = "findUserById")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "findStatusBySlug")
    public abstract Task map(TaskUpdateDTO model);

    @Mapping(source = "title", target = "name")
    @Mapping(source = "content", target = "description")
    @Mapping(source = "assigneeId", target = "assignee",qualifiedByName = "findUserById")
    @Mapping(source = "status", target = "taskStatus", qualifiedByName = "findStatusBySlug")
    public abstract void update(TaskUpdateDTO update, @MappingTarget Task destination);
	
    @Named("findStatusBySlug")
    protected TaskStatus findStatusBySlug(String slug) {
        return taskStatusRepository.findBySlug(slug).orElse(null);
    }

    @Named("findUserById")
    protected User findUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }
}	

