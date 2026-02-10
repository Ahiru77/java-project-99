package hexlet.code.mapper;

import hexlet.code.dto.TaskStatusDTO;
import hexlet.code.model.TaskStatus;
import hexlet.code.dto.TaskStatusUpdateDTO;
import hexlet.code.dto.TaskStatusCreateDTO;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)

public abstract class TaskStatusMapper {

    public abstract TaskStatusDTO map(TaskStatus model);

    public abstract TaskStatus map(TaskStatusCreateDTO model);

    public abstract TaskStatus map(TaskStatusDTO model);

    public abstract TaskStatus map(TaskStatusUpdateDTO model);

    public abstract void update(TaskStatusUpdateDTO update, @MappingTarget TaskStatus destination);
}
