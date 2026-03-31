package back.projet.tpp.repository;

import back.projet.tpp.domain.model.entity.TimeSlot;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {

    List<TimeSlot> findByCourseIdAndDateBetween(Integer courseId, LocalDate startDate, LocalDate endDate);

    List<TimeSlot> findByCourseId(Integer id_course);

    @Query("SELECT ts FROM TimeSlot ts WHERE ts.date = :date AND ((ts.startTime <= :startTime AND ts.endTime >= :startTime) OR (ts.startTime <= :endTime AND ts.endTime >= :endTime) OR (ts.startTime >= :startTime AND ts.endTime <= :endTime))")
    List<TimeSlot> findByCourseIdAndDateAndTimes(Integer courseId, LocalDate date, LocalTime startTime, LocalTime endTime);
}