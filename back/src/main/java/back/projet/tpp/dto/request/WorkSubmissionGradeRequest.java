package back.projet.tpp.dto.request;

import back.projet.tpp.domain.model.enums.WorkSubmissionGrade;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update a student's work submission's grade")
public record WorkSubmissionGradeRequest (
    @Schema(description = "Grade (ZERO or ONE)", example = "ONE")
    WorkSubmissionGrade grade
    ){
}
