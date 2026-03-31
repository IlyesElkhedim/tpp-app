package back.projet.tpp.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "time_slot")
public class TimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "submission_start_time", nullable = false)
    private LocalTime submissionStartTime;

    @Column(name = "submission_end_time", nullable = false)
    private LocalTime submissionEndTime;

    @Column(name = "room")
    private String room;

    @ManyToOne(optional = false) // a TimeSlot must have a Course
    @JoinColumn(name = "id_course", nullable = false)
    private Course course;
}