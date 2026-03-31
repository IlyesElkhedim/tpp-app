package back.projet.tpp.domain.model.entity;

import back.projet.tpp.domain.model.enums.AttendanceStatus;
import back.projet.tpp.domain.model.enums.WorkSubmissionGrade;
import back.projet.tpp.domain.model.enums.WorkSubmissionStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "student_at_time_slot")
public class StudentAtTimeSlot {

    @EmbeddedId
    private StudentAtTimeSlotId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("studentId")
    @JoinColumn(name = "id_student", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("timeSlotId")
    @JoinColumn(name = "id_time_slot", nullable = false)
    private TimeSlot timeSlot;

    @Column(name = "attendance", nullable = false)
    @Enumerated(EnumType.STRING)
    private AttendanceStatus attendance;

    @Column(name = "absence_justification")
    private String absenceJustification;

    @OneToOne(mappedBy = "studentAtTimeSlot", cascade = CascadeType.ALL, orphanRemoval = true)
    private WorkSubmission workSubmission;

    @Column(name = "work_submission_status")
    @Enumerated(EnumType.STRING)
    private WorkSubmissionStatus workSubmissionStatus;

    @Column(name = "work_submission_grade")
    @Enumerated(EnumType.STRING)
    private WorkSubmissionGrade workSubmissionGrade;

}
