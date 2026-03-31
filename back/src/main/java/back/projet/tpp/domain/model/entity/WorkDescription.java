package back.projet.tpp.domain.model.entity;

import back.projet.tpp.domain.model.enums.WorkType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "work_description")
public class WorkDescription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idWorkDescription;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "id_student", referencedColumnName = "id_student"),
            @JoinColumn(name = "id_time_slot", referencedColumnName = "id_time_slot")
    })
    private WorkSubmission workSubmission;

    @Column(name = "work_type")
    @Enumerated(EnumType.STRING)
    private WorkType workType;

    @Column(length = 100)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false)
    private Integer timeSpent; // minutes

}
