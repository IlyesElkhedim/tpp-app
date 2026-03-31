package back.projet.tpp.dto;

import back.projet.tpp.domain.model.entity.Student;
import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

@Schema(description = "DTO representing a student")
public record StudentDto(
        @NotBlank(message = "Student number is required")
        @Schema(description = "Student number (8 digits)", example = "12345678", required = true)
        String studentNumber,

        @NotBlank(message = "First name is required")
        @Schema(description = "Student's first name", example = "Jean")
        String firstName,

        @NotBlank(message = "Last name is required")
        @Schema(description = "Student's last name", example = "Dupont")
        String lastName,

        @NotBlank(message = "Email is required")
        @Email(message = "Email must be valid")
        @Schema(description = "Student email address", example = "jean.dupont@example.com")
        String email,

        @NotEmpty(message = "Student must be enrolled in at least one course")
        @Schema(description = "List of course IDs the student is enrolled in", example = "[1, 2]", required = true)
        List<Integer> courseIds,

        @Schema(description = "ID of the student's current course (nullable)", example = "1")
        Integer currentCourse,

        @Schema(description = "List of course enrollments with contract information")
        List<CourseEnrollmentDto> enrollments
) {
    // Additional constructor used by tests: (studentNumber, firstName, lastName, email, courseIds)
    public StudentDto(String studentNumber, String firstName, String lastName, String email, List<Integer> courseIds) {
        this(studentNumber, firstName, lastName, email, courseIds == null ? List.of() : courseIds, null, List.of());
    }

    // Additional constructor used by tests: (studentNumber, firstName, lastName, email, courseIds, currentCourse)
    public StudentDto(String studentNumber, String firstName, String lastName, String email, List<Integer> courseIds, Integer currentCourse) {
        this(studentNumber, firstName, lastName, email, courseIds == null ? List.of() : courseIds, currentCourse, List.of());
    }

    public static StudentDto fromEntity(Student student) {
        return new StudentDto(
                student.getStudentNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                List.of(), // courseIds not loaded here
                null,
                List.of()
        );
    }

    public static StudentDto of(Student student, List<Integer> courseIds, Integer currentCourse, List<CourseEnrollmentDto> enrollments) {
        if (currentCourse == null && student.getCurrentCourse() != null) {
            currentCourse = student.getCurrentCourse().getId();
        }
        return new StudentDto(
                student.getStudentNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                courseIds == null ? List.of() : courseIds,
                currentCourse,
                enrollments == null ? List.of() : enrollments
        );
    }

    public static Student toEntity(StudentDto studentDto) {
        Student student = new Student();
        student.setStudentNumber(studentDto.studentNumber());
        student.setFirstName(studentDto.firstName());
        student.setLastName(studentDto.lastName());
        student.setEmail(studentDto.email());
        // Set a default password (should be changed by the user)
        student.setHashPassword("$2a$10$defaultPasswordHashToChangeByUser");
        return student;
    }
}
