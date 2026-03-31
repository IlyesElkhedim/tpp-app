package back.projet.tpp.domain.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "course_supervisor")
public class CourseSupervisor {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_supervisor")
    private Supervisor supervisor;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_course")
    private Course course;
}
