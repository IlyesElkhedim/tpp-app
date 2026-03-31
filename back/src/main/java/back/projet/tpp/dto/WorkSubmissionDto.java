package back.projet.tpp.dto;

import back.projet.tpp.domain.model.entity.WorkSubmission;
import back.projet.tpp.domain.model.enums.WorkSubmissionGrade;
import back.projet.tpp.domain.model.enums.WorkSubmissionStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;


@Schema(description = "DTO WorkDescription")
public record WorkSubmissionDto(
        String idStudent,
        Long idTimeSlot,
        WorkSubmissionStatus workSubmissionStatus,
        WorkSubmissionGrade workSubmissionGrade,
        List<WorkDescriptionDto> works
) {
    public static WorkSubmissionDto fromEntity(WorkSubmission workSubmission) {
        return new WorkSubmissionDto(
                workSubmission.getId().getStudentId(),
                workSubmission.getId().getTimeSlotId(),
                workSubmission.getStudentAtTimeSlot().getWorkSubmissionStatus(),
                workSubmission.getStudentAtTimeSlot().getWorkSubmissionGrade(),
                workSubmission.getWorkDescriptions().stream().map(WorkDescriptionDto::fromEntity).toList()
        );
    }
}
