package back.projet.tpp.dto.request;

import back.projet.tpp.domain.model.entity.Course;
import back.projet.tpp.domain.model.entity.TimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;

public record TimeSlotDto(
        Long timeSlotId,
        Integer courseId,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        LocalTime submissionStartTime,
        LocalTime submissionEndTime,
        String room

) {
    public static TimeSlot toEntity(TimeSlotDto timeSlotDto, Course course) {
        TimeSlot timeSlot = new TimeSlot();

        timeSlot.setId(timeSlotDto.timeSlotId());
        updateFieldsEntity(timeSlot, timeSlotDto);
        timeSlot.setCourse(course);

        return timeSlot;
    }

    public static void updateFieldsEntity(TimeSlot timeSlot, TimeSlotDto timeSlotDto) {
        timeSlot.setDate(timeSlotDto.date());
        timeSlot.setStartTime(timeSlotDto.startTime());
        timeSlot.setEndTime(timeSlotDto.endTime());
        timeSlot.setSubmissionStartTime(timeSlotDto.submissionStartTime());
        timeSlot.setSubmissionEndTime(timeSlotDto.submissionEndTime());
        timeSlot.setRoom(timeSlotDto.room());
    }

    public static TimeSlotDto fromEntity(TimeSlot timeSlot) {
        return new TimeSlotDto(
                timeSlot.getId(),
                timeSlot.getCourse() != null ? timeSlot.getCourse().getId() : null,
                timeSlot.getDate(),
                timeSlot.getStartTime(),
                timeSlot.getEndTime(),
                timeSlot.getSubmissionStartTime(),
                timeSlot.getSubmissionEndTime(),
                timeSlot.getRoom()
        );
    }
}