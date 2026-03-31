package back.projet.tpp.repository;

import back.projet.tpp.domain.model.entity.StudentAtTimeSlot;
import back.projet.tpp.domain.model.entity.StudentAtTimeSlotId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAtTimeSlotRepository extends JpaRepository<StudentAtTimeSlot, StudentAtTimeSlotId> {

    /**
     * Check if an attendance exists for a given time slot and student.
     *
     * @param timeSlotId time slot ID
     * @param studentId  student number
     * @return true if an attendance exists, false otherwise
     */
    boolean existsByIdTimeSlotIdAndIdStudentId(Long timeSlotId, String studentId);

    /**
     * Retrieve all students registered to a given time slot.
     *
     * @param timeSlotId time slot ID
     * @return list of StudentAtTimeSlot entries for that time slot
     */
    List<StudentAtTimeSlot> findByIdTimeSlotId(Long timeSlotId);

    /**
     * Delete all StudentAtTimeSlot entries for a given time slot.
     *
     * @param timeSlotId time slot ID
     */
    void deleteByIdTimeSlotId(Long timeSlotId);
}
