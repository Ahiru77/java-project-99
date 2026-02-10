package hexlet.code.mapper;

import hexlet.code.dto.LabelDTO;
import hexlet.code.model.Label;
import hexlet.code.dto.LabelUpdateDTO;
import hexlet.code.dto.LabelCreateDTO;

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

public abstract class LabelMapper {

    public abstract LabelDTO map(Label model);

    public abstract Label map(LabelCreateDTO model);

    public abstract Label map(LabelDTO model);

    public abstract Label map(LabelUpdateDTO model);

    public abstract void update(LabelUpdateDTO update, @MappingTarget Label destination);
}
