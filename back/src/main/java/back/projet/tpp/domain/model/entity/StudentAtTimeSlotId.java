package back.projet.tpp.domain.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StudentAtTimeSlotId implements Serializable {

    @Column(name = "id_student")
    private String studentId;

    @Column(name = "id_time_slot")
    private Long timeSlotId;
}

