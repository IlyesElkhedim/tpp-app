package back.projet.tpp.repository;

import back.projet.tpp.domain.model.entity.Course;
import back.projet.tpp.domain.model.entity.TimeSlot;
import back.projet.tpp.domain.model.enums.CourseLevel;
import back.projet.tpp.domain.model.enums.CourseName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("TimeSlotRepository - Tests d'intégration avec la base de données")
class TimeSlotRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TimeSlotRepository timeSlotRepository;

    private Course createAndPersistCourse() {
        Course course = new Course();
        course.setId(1);
        course.setName(CourseName.TIW);
        course.setYears("2025");
        course.setLevel(CourseLevel.M1);
        entityManager.persist(course);
        entityManager.flush();
        return course;
    }

    @Test
    @DisplayName("findByDateBetween devrait retourner les TimeSlots entre deux dates")
    void findByDateBetween_ShouldReturnTimeSlotsInRange() {
        // Given
        Course course = createAndPersistCourse();

        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setCourse(course);
        timeSlot1.setDate(LocalDate.of(2025, 1, 15));
        timeSlot1.setStartTime(LocalTime.of(9, 0));
        timeSlot1.setEndTime(LocalTime.of(12, 0));
        timeSlot1.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot1.setSubmissionEndTime(LocalTime.of(13, 0));

        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setCourse(course);
        timeSlot2.setDate(LocalDate.of(2025, 1, 20));
        timeSlot2.setStartTime(LocalTime.of(14, 0));
        timeSlot2.setEndTime(LocalTime.of(17, 0));
        timeSlot2.setSubmissionStartTime(LocalTime.of(13, 0));
        timeSlot2.setSubmissionEndTime(LocalTime.of(18, 0));

        TimeSlot timeSlot3 = new TimeSlot();
        timeSlot3.setCourse(course);
        timeSlot3.setDate(LocalDate.of(2025, 2, 10));
        timeSlot3.setStartTime(LocalTime.of(9, 0));
        timeSlot3.setEndTime(LocalTime.of(12, 0));
        timeSlot3.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot3.setSubmissionEndTime(LocalTime.of(13, 0));

        entityManager.persist(timeSlot1);
        entityManager.persist(timeSlot2);
        entityManager.persist(timeSlot3);
        entityManager.flush();

        // When
        List<TimeSlot> result = timeSlotRepository.findByCourseIdAndDateBetween(
                1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );

        // Then
        assertThat(result).hasSize(2);
        assertThat(result).extracting(TimeSlot::getDate)
                .containsExactlyInAnyOrder(
                        LocalDate.of(2025, 1, 15),
                        LocalDate.of(2025, 1, 20)
                );
    }

    @Test
    @DisplayName("findByDateBetween devrait retourner une liste vide si aucun TimeSlot dans la plage")
    void findByDateBetween_ShouldReturnEmptyList_WhenNoTimeSlotsInRange() {
        // Given
        Course course = createAndPersistCourse();
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setCourse(course);
        timeSlot.setDate(LocalDate.of(2025, 3, 15));
        timeSlot.setStartTime(LocalTime.of(9, 0));
        timeSlot.setEndTime(LocalTime.of(12, 0));
        timeSlot.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot.setSubmissionEndTime(LocalTime.of(13, 0));

        entityManager.persist(timeSlot);
        entityManager.flush();

        // When
        List<TimeSlot> result = timeSlotRepository.findByCourseIdAndDateBetween(
                1,
                LocalDate.of(2025, 1, 1),
                LocalDate.of(2025, 1, 31)
        );

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findById devrait retourner un TimeSlot quand il existe")
    void findById_ShouldReturnTimeSlot_WhenExists() {
        // Given
        Course course = createAndPersistCourse();
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setCourse(course);
        timeSlot.setDate(LocalDate.of(2025, 1, 15));
        timeSlot.setStartTime(LocalTime.of(9, 0));
        timeSlot.setEndTime(LocalTime.of(12, 0));
        timeSlot.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot.setSubmissionEndTime(LocalTime.of(13, 0));

        TimeSlot saved = entityManager.persist(timeSlot);
        entityManager.flush();

        // When
        Optional<TimeSlot> found = timeSlotRepository.findById(saved.getId());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getDate()).isEqualTo(LocalDate.of(2025, 1, 15));
        assertThat(found.get().getStartTime()).isEqualTo(LocalTime.of(9, 0));
    }

    @Test
    @DisplayName("save devrait persister un nouveau TimeSlot")
    void save_ShouldPersistNewTimeSlot() {
        // Given
        Course course = createAndPersistCourse();

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setDate(LocalDate.of(2025, 1, 15));
        timeSlot.setStartTime(LocalTime.of(9, 0));
        timeSlot.setEndTime(LocalTime.of(12, 0));
        timeSlot.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot.setSubmissionEndTime(LocalTime.of(13, 0));
        timeSlot.setCourse(course);

        // When
        TimeSlot saved = timeSlotRepository.save(timeSlot);
        entityManager.flush();

        // Then
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getDate()).isEqualTo(LocalDate.of(2025, 1, 15));

        TimeSlot found = entityManager.find(TimeSlot.class, saved.getId());
        assertThat(found).isNotNull();
        assertThat(found.getStartTime()).isEqualTo(LocalTime.of(9, 0));
    }

    @Test
    @DisplayName("deleteById devrait supprimer un TimeSlot")
    void deleteById_ShouldDeleteTimeSlot() {
        // Given
        Course course = createAndPersistCourse();
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setCourse(course);
        timeSlot.setDate(LocalDate.of(2025, 1, 15));
        timeSlot.setStartTime(LocalTime.of(9, 0));
        timeSlot.setEndTime(LocalTime.of(12, 0));
        timeSlot.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot.setSubmissionEndTime(LocalTime.of(13, 0));

        TimeSlot saved = entityManager.persist(timeSlot);
        entityManager.flush();
        Long id = saved.getId();

        // When
        timeSlotRepository.deleteById(id);
        entityManager.flush();

        // Then
        TimeSlot found = entityManager.find(TimeSlot.class, id);
        assertThat(found).isNull();
    }

    @Test
    @DisplayName("existsById devrait retourner true si le TimeSlot existe")
    void existsById_ShouldReturnTrue_WhenExists() {
        // Given
        Course course = createAndPersistCourse();
        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setCourse(course);
        timeSlot.setDate(LocalDate.of(2025, 1, 15));
        timeSlot.setStartTime(LocalTime.of(9, 0));
        timeSlot.setEndTime(LocalTime.of(12, 0));
        timeSlot.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot.setSubmissionEndTime(LocalTime.of(13, 0));

        TimeSlot saved = entityManager.persist(timeSlot);
        entityManager.flush();

        // When
        boolean exists = timeSlotRepository.existsById(saved.getId());

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsById devrait retourner false si le TimeSlot n'existe pas")
    void existsById_ShouldReturnFalse_WhenNotExists() {
        // When
        boolean exists = timeSlotRepository.existsById(999L);

        // Then
        assertThat(exists).isFalse();
    }
}
