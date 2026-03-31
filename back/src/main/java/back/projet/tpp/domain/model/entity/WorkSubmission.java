package back.projet.tpp.domain.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinColumns;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "work_submission")
public class WorkSubmission {

    @EmbeddedId
    private StudentAtTimeSlotId id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumns({
            @JoinColumn(name = "id_student", referencedColumnName = "id_student"),
            @JoinColumn(name = "id_time_slot", referencedColumnName = "id_time_slot")
    })
    private StudentAtTimeSlot studentAtTimeSlot;

    @OneToMany(mappedBy = "workSubmission", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<WorkDescription> workDescriptions;

}
