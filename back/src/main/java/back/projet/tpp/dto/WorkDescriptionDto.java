package back.projet.tpp.dto;

import back.projet.tpp.domain.model.entity.WorkDescription;
import back.projet.tpp.domain.model.entity.WorkSubmission;
import back.projet.tpp.domain.model.enums.WorkType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO WorkDescription")
public record WorkDescriptionDto(
        WorkType workType,
        String subject,
        String description,
        Integer timeSpent
) {
    public static WorkDescriptionDto fromEntity(WorkDescription workDescription) {
        return new WorkDescriptionDto(
                workDescription.getWorkType(),
                workDescription.getSubject(),
                workDescription.getDescription(),
                workDescription.getTimeSpent()
        );
    }

    public static WorkDescription toEntity(WorkDescriptionDto workDescriptionDto, WorkSubmission workSubmission) {
        WorkDescription workDescription = new WorkDescription();
        workDescription.setWorkSubmission(workSubmission);
        workDescription.setWorkType(workDescriptionDto.workType());
        workDescription.setSubject(workDescriptionDto.subject());
        workDescription.setTimeSpent(workDescriptionDto.timeSpent());
        workDescription.setDescription(workDescriptionDto.description());
        return workDescription;
    }
}
