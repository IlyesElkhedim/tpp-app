package back.projet.tpp.dto;

import back.projet.tpp.domain.model.entity.Course;
import back.projet.tpp.domain.model.enums.CourseLevel;
import back.projet.tpp.domain.model.enums.CourseName;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO representing a course")
public record CourseDto(
        @Schema(description = "Unique course ID", example = "1")
        Integer id,

        @Schema(description = "Course name/type", example = "TIW", required = true)
        CourseName name,

        @Schema(description = "Course level", example = "M1", required = true)
        CourseLevel level,

        @Schema(description = "Academic year of the course", example = "2025-2026", required = true)
        String years,

        @Schema(description = "ID of the supervisor associated with the course (used at creation and returned by the API)")
        Integer supervisorId
) {
    public static CourseDto fromEntity(Course course) {
        return new CourseDto(
                course.getId(),
                course.getName(),
                course.getLevel(),
                course.getYears(),
                course.getSupervisor() != null ? course.getSupervisor().getId() : null
        );
    }

    public static Course toEntity(CourseDto courseDto) {
        Course course = new Course();
        course.setId(courseDto.id());
        course.setName(courseDto.name());
        course.setLevel(courseDto.level());
        course.setYears(courseDto.years());
        // Supervisor is resolved in the service (via supervisorId) to throw 404 if missing
        return course;
    }
}
