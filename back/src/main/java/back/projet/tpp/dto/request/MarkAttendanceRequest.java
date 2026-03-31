package back.projet.tpp.dto.request;

import back.projet.tpp.domain.model.enums.AttendanceStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Request to update a student's attendance status")
public record MarkAttendanceRequest(
        @Schema(description = "Student number (8 digits)", example = "12345678")
        String studentId,

        @Schema(description = "Attendance status (PRESENT, UNJUSTIFIED, JUSTIFIED)", example = "PRESENT")
        AttendanceStatus attendanceStatus
) {
}
