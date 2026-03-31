package back.projet.tpp.repository;

import back.projet.tpp.domain.model.entity.Course;
import back.projet.tpp.domain.model.entity.Student;
import back.projet.tpp.domain.model.entity.StudentAtTimeSlot;
import back.projet.tpp.domain.model.entity.StudentAtTimeSlotId;
import back.projet.tpp.domain.model.entity.TimeSlot;
import back.projet.tpp.domain.model.enums.CourseLevel;
import back.projet.tpp.domain.model.enums.CourseName;
import back.projet.tpp.domain.model.enums.AttendanceStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("StudentAtTimeSlotRepository - Tests d'intégration")
class StudentAtTimeSlotRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentAtTimeSlotRepository studentAtTimeSlotRepository;

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
    @DisplayName("findByIdTimeSlotId devrait retourner tous les étudiants inscrits à un créneau")
    void findByIdTimeSlotId_ShouldReturnAllStudentsForTimeSlot() {
        // Given
        Course course = createAndPersistCourse();

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setDate(LocalDate.of(2025, 1, 15));
        timeSlot.setStartTime(LocalTime.of(9, 0));
        timeSlot.setEndTime(LocalTime.of(12, 0));
        timeSlot.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot.setSubmissionEndTime(LocalTime.of(13, 0));
        timeSlot.setCourse(course);
        entityManager.persist(timeSlot);

        Student student1 = new Student();
        student1.setStudentNumber("100001");
        student1.setFirstName("Alice");
        student1.setLastName("Dupont");
        student1.setEmail("alice@test.fr");
        student1.setHashPassword("$2a$10$hashedpassword");
        entityManager.persist(student1);

        Student student2 = new Student();
        student2.setStudentNumber("100002");
        student2.setFirstName("Bob");
        student2.setLastName("Martin");
        student2.setEmail("bob@test.fr");
        student2.setHashPassword("$2a$10$hashedpassword");
        entityManager.persist(student2);

        Student student3 = new Student();
        student3.setStudentNumber("100003");
        student3.setFirstName("Charlie");
        student3.setLastName("Bernard");
        student3.setEmail("charlie@test.fr");
        student3.setHashPassword("$2a$10$hashedpassword");
        entityManager.persist(student3);

        StudentAtTimeSlot sats1 = new StudentAtTimeSlot();
        sats1.setId(new StudentAtTimeSlotId(student1.getStudentNumber(), timeSlot.getId()));
        sats1.setStudent(student1);
        sats1.setTimeSlot(timeSlot);
        sats1.setAttendance(AttendanceStatus.PRESENT);
        entityManager.persist(sats1);

        StudentAtTimeSlot sats2 = new StudentAtTimeSlot();
        sats2.setId(new StudentAtTimeSlotId(student2.getStudentNumber(), timeSlot.getId()));
        sats2.setStudent(student2);
        sats2.setTimeSlot(timeSlot);
        sats2.setAttendance(AttendanceStatus.UNJUSTIFIED);
        entityManager.persist(sats2);

        StudentAtTimeSlot sats3 = new StudentAtTimeSlot();
        sats3.setId(new StudentAtTimeSlotId(student3.getStudentNumber(), timeSlot.getId()));
        sats3.setStudent(student3);
        sats3.setTimeSlot(timeSlot);
        sats3.setAttendance(AttendanceStatus.JUSTIFIED);
        entityManager.persist(sats3);

        entityManager.flush();

        // When
        List<StudentAtTimeSlot> result = studentAtTimeSlotRepository.findByIdTimeSlotId(timeSlot.getId());

        // Then
        assertThat(result).hasSize(3);
        assertThat(result).extracting(s -> s.getId().getStudentId())
                .containsExactlyInAnyOrder(student1.getStudentNumber(), student2.getStudentNumber(), student3.getStudentNumber());
        assertThat(result).extracting(StudentAtTimeSlot::getAttendance)
                .containsExactlyInAnyOrder(AttendanceStatus.PRESENT, AttendanceStatus.UNJUSTIFIED, AttendanceStatus.JUSTIFIED);
    }

    @Test
    @DisplayName("findByIdTimeSlotId devrait retourner une liste vide si aucun étudiant n'est inscrit")
    void findByIdTimeSlotId_ShouldReturnEmptyList_WhenNoStudents() {
        // Given
        Course course = createAndPersistCourse();

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setDate(LocalDate.of(2025, 1, 15));
        timeSlot.setStartTime(LocalTime.of(9, 0));
        timeSlot.setEndTime(LocalTime.of(12, 0));
        timeSlot.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot.setSubmissionEndTime(LocalTime.of(13, 0));
        timeSlot.setCourse(course);
        entityManager.persist(timeSlot);
        entityManager.flush();

        // When
        List<StudentAtTimeSlot> result = studentAtTimeSlotRepository.findByIdTimeSlotId(timeSlot.getId());

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("findByIdTimeSlotId ne devrait pas retourner les étudiants d'autres créneaux")
    void findByIdTimeSlotId_ShouldNotReturnStudentsFromOtherTimeSlots() {
        // Given
        Course course = createAndPersistCourse();

        TimeSlot timeSlot1 = new TimeSlot();
        timeSlot1.setDate(LocalDate.of(2025, 1, 15));
        timeSlot1.setStartTime(LocalTime.of(9, 0));
        timeSlot1.setEndTime(LocalTime.of(12, 0));
        timeSlot1.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot1.setSubmissionEndTime(LocalTime.of(13, 0));
        timeSlot1.setCourse(course);
        entityManager.persist(timeSlot1);

        TimeSlot timeSlot2 = new TimeSlot();
        timeSlot2.setDate(LocalDate.of(2025, 1, 20));
        timeSlot2.setStartTime(LocalTime.of(14, 0));
        timeSlot2.setEndTime(LocalTime.of(17, 0));
        timeSlot2.setSubmissionStartTime(LocalTime.of(13, 0));
        timeSlot2.setSubmissionEndTime(LocalTime.of(18, 0));
        timeSlot2.setCourse(course);
        entityManager.persist(timeSlot2);

        Student student1 = new Student();
        student1.setStudentNumber("10000001");
        student1.setFirstName("Alice");
        student1.setLastName("Dupont");
        student1.setEmail("alice@test.fr");
        student1.setHashPassword("$2a$10$hashedpassword");
        entityManager.persist(student1);

        Student student2 = new Student();
        student2.setStudentNumber("10000002");
        student2.setFirstName("Bob");
        student2.setLastName("Martin");
        student2.setEmail("bob@test.fr");
        student2.setHashPassword("$2a$10$hashedpassword");
        entityManager.persist(student2);

        // Étudiant inscrit au timeSlot1
        StudentAtTimeSlot sats1 = new StudentAtTimeSlot();
        sats1.setId(new StudentAtTimeSlotId(student1.getStudentNumber(), timeSlot1.getId()));
        sats1.setStudent(student1);
        sats1.setTimeSlot(timeSlot1);
        sats1.setAttendance(AttendanceStatus.PRESENT);
        entityManager.persist(sats1);

        // Étudiant inscrit au timeSlot2
        StudentAtTimeSlot sats2 = new StudentAtTimeSlot();
        sats2.setId(new StudentAtTimeSlotId(student2.getStudentNumber(), timeSlot2.getId()));
        sats2.setStudent(student2);
        sats2.setTimeSlot(timeSlot2);
        sats2.setAttendance(AttendanceStatus.UNJUSTIFIED);
        entityManager.persist(sats2);

        entityManager.flush();

        // When
        List<StudentAtTimeSlot> result = studentAtTimeSlotRepository.findByIdTimeSlotId(timeSlot1.getId());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId().getStudentId()).isEqualTo(student1.getStudentNumber());
        assertThat(result.getFirst().getAttendance()).isEqualTo(AttendanceStatus.PRESENT);
    }

    @Test
    @DisplayName("existsByIdTimeSlotIdAndIdStudentId devrait retourner true si l'inscription existe")
    void existsByIdTimeSlotIdAndIdStudentId_ShouldReturnTrue_WhenExists() {
        // Given
        Course course = createAndPersistCourse();

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setDate(LocalDate.of(2025, 1, 15));
        timeSlot.setStartTime(LocalTime.of(9, 0));
        timeSlot.setEndTime(LocalTime.of(12, 0));
        timeSlot.setSubmissionStartTime(LocalTime.of(8, 0));
        timeSlot.setSubmissionEndTime(LocalTime.of(13, 0));
        timeSlot.setCourse(course);
        entityManager.persist(timeSlot);

        Student student = new Student();
        student.setStudentNumber("10000001");
        student.setFirstName("Alice");
        student.setLastName("Dupont");
        student.setEmail("alice@test.fr");
        student.setHashPassword("$2a$10$hashedpassword");
        entityManager.persist(student);

        StudentAtTimeSlot sats = new StudentAtTimeSlot();
        sats.setId(new StudentAtTimeSlotId(student.getStudentNumber(), timeSlot.getId()));
        sats.setStudent(student);
        sats.setTimeSlot(timeSlot);
        sats.setAttendance(AttendanceStatus.PRESENT);
        entityManager.persist(sats);
        entityManager.flush();

        // When
        Boolean exists = studentAtTimeSlotRepository.existsByIdTimeSlotIdAndIdStudentId(
                timeSlot.getId(),
                student.getStudentNumber()
        );

        // Then
        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("existsByIdTimeSlotIdAndIdStudentId devrait retourner false si l'inscription n'existe pas")
    void existsByIdTimeSlotIdAndIdStudentId_ShouldReturnFalse_WhenNotExists() {
        // When
        Boolean exists = studentAtTimeSlotRepository.existsByIdTimeSlotIdAndIdStudentId(999L, "999");

        // Then
        assertThat(exists).isFalse();
    }
}

