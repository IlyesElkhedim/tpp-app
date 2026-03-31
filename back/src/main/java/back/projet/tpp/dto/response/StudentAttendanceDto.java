package back.projet.tpp.dto.response;

import back.projet.tpp.domain.model.entity.StudentAtTimeSlot;
import back.projet.tpp.domain.model.enums.AttendanceStatus;

public record StudentAttendanceDto(
        String studentId,
        AttendanceStatus attendanceStatus
) {
    public static StudentAttendanceDto fromEntity(StudentAtTimeSlot sats) {
        return new StudentAttendanceDto(
                sats.getId().getStudentId(),
                sats.getAttendance()
        );
    }
}
