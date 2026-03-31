package back.projet.tpp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(description = "DTO representing a student's enrollment to a course with contract info")
public record CourseEnrollmentDto(
        @Schema(description = "Course ID", example = "1")
        Integer courseId,

        @Schema(description = "Contract type label", example = "Apprentissage")
        String contractType,

        @Schema(description = "Contract start date", example = "2025-09-01")
        LocalDate contractStartDate
) {
}
