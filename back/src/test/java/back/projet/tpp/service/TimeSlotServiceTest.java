package back.projet.tpp.service;

import back.projet.tpp.domain.model.entity.Course;
import back.projet.tpp.domain.model.entity.Student;
import back.projet.tpp.domain.model.entity.StudentAtTimeSlot;
import back.projet.tpp.domain.model.entity.StudentAtTimeSlotId;
import back.projet.tpp.domain.model.entity.TimeSlot;
import back.projet.tpp.domain.model.enums.AttendanceStatus;
import back.projet.tpp.domain.model.enums.CourseName;
import back.projet.tpp.dto.request.MarkAttendanceRequest;
import back.projet.tpp.dto.request.TimeSlotDto;
import back.projet.tpp.dto.response.StudentAttendanceDto;
import back.projet.tpp.repository.CourseRepository;
import back.projet.tpp.repository.CourseStudentRepository;
import back.projet.tpp.repository.StudentAtTimeSlotRepository;
import back.projet.tpp.repository.TimeSlotRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("TimeSlotService - Tests unitaires")
class TimeSlotServiceTest {

    @Mock
    private TimeSlotRepository timeSlotRepository;

    @Mock
    private StudentAtTimeSlotRepository studentAtTimeSlotRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private CourseStudentRepository courseStudentRepository;

    @InjectMocks
    private TimeSlotService timeSlotService;

    @Test
    @DisplayName("create devrait créer un nouveau TimeSlot")
    void create_ShouldCreateNewTimeSlot() {
        // Given
        TimeSlotDto request = new TimeSlotDto(
                1L,
                1, // courseId
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(8, 0),
                LocalTime.of(12, 30),
                null // room
        );

        Course course = new Course();
        course.setId(1);
        course.setName(CourseName.TIW);

        TimeSlot savedEntity = new TimeSlot();
        savedEntity.setId(1L);
        savedEntity.setDate(request.date());
        savedEntity.setStartTime(request.startTime());
        savedEntity.setEndTime(request.endTime());
        savedEntity.setCourse(course);

        // Mocks
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(savedEntity);
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(savedEntity));
        when(courseStudentRepository.findStudentsByCourseId(1L)).thenReturn(Collections.emptyList());

        // When
        TimeSlotDto result = timeSlotService.create(request);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.courseId()).isEqualTo(1);
        verify(timeSlotRepository, times(1)).save(any(TimeSlot.class));
    }

    @Test
    @DisplayName("create devrait lever une exception si startTime est après endTime")
    void create_ShouldThrowException_WhenInvalidTimes() {
        // Given
        TimeSlotDto request = new TimeSlotDto(
                1L,
                1, // courseId
                LocalDate.of(2025, 1, 15),
                LocalTime.of(12, 0), // start
                LocalTime.of(9, 0),  // end (before start!)
                LocalTime.of(8, 0),
                LocalTime.of(12, 30),
                null
        );

        // When & Then
        assertThatThrownBy(() -> timeSlotService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("startTime must be before endTime");

        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    @DisplayName("findBetweenDates devrait retourner les créneaux entre deux dates")
    void findBetweenDates_ShouldReturnTimeSlots() {
        // Given
        Integer courseId = 1;
        LocalDate start = LocalDate.of(2025, 1, 1);
        LocalDate end = LocalDate.of(2025, 1, 31);

        Course course = new Course();
        course.setId(1);

        TimeSlot slot1 = new TimeSlot();
        slot1.setId(1L);
        slot1.setDate(LocalDate.of(2025, 1, 15));
        slot1.setCourse(course);

        TimeSlot slot2 = new TimeSlot();
        slot2.setId(2L);
        slot2.setDate(LocalDate.of(2025, 1, 20));
        slot2.setCourse(course);

        List<TimeSlot> expected = Arrays.asList(slot1, slot2);

        when(timeSlotRepository.findByCourseIdAndDateBetween(courseId, start, end)).thenReturn(expected);

        // When
        List<TimeSlotDto> result = timeSlotService.findBetweenDates(courseId, start, end);

        // Then
        assertThat(result).hasSize(2);
        verify(timeSlotRepository, times(1)).findByCourseIdAndDateBetween(courseId, start, end);
    }

    @Test
    @DisplayName("deleteById devrait supprimer un créneau existant")
    void deleteById_ShouldDeleteExistingTimeSlot() {
        // Given
        Long id = 1L;
        when(timeSlotRepository.existsById(id)).thenReturn(true);
        doNothing().when(timeSlotRepository).deleteById(id);

        // When
        timeSlotService.deleteById(id);

        // Then
        verify(timeSlotRepository, times(1)).existsById(id);
        verify(timeSlotRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("deleteById devrait lever une exception si le créneau n'existe pas")
    void deleteById_ShouldThrowException_WhenNotFound() {
        // Given
        Long id = 999L;
        when(timeSlotRepository.existsById(id)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> timeSlotService.deleteById(id))
                .isInstanceOf(EntityNotFoundException.class);

        verify(timeSlotRepository, times(1)).existsById(id);
        verify(timeSlotRepository, never()).deleteById(id);
    }

    @Test
    @DisplayName("update devrait mettre à jour un créneau existant")
    void update_ShouldUpdateExistingTimeSlot() {
        // Given
        Long id = 1L;
        TimeSlotDto request = new TimeSlotDto(
                1L,
                1, // courseId (Integer)
                LocalDate.of(2025, 1, 20),
                LocalTime.of(10, 0),
                LocalTime.of(13, 0),
                LocalTime.of(9, 0),
                LocalTime.of(13, 30),
                null
        );

        Course course = new Course();
        course.setId(1);
        course.setName(CourseName.TIW);

        TimeSlot existing = new TimeSlot();
        existing.setId(id);
        existing.setDate(LocalDate.of(2025, 1, 15));
        existing.setStartTime(LocalTime.of(9, 0));
        existing.setEndTime(LocalTime.of(12, 0));
        existing.setCourse(course);

        TimeSlot updated = new TimeSlot();
        updated.setId(id);
        updated.setDate(request.date());
        updated.setStartTime(request.startTime());
        updated.setEndTime(request.endTime());
        updated.setCourse(course);

        when(timeSlotRepository.findById(id)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(updated);

        // When
        TimeSlotDto result = timeSlotService.update(id, request);

        // Then
        assertThat(result.date()).isEqualTo(request.date());
        assertThat(result.startTime()).isEqualTo(request.startTime());
        assertThat(result.endTime()).isEqualTo(request.endTime());
        verify(timeSlotRepository, times(1)).findById(id);
        verify(timeSlotRepository, times(1)).save(any(TimeSlot.class));
    }

    @Test
    @DisplayName("getStudentAttendance devrait retourner true si l'étudiant est présent")
    void getStudentAttendance_ShouldReturnTrue_WhenAttended() {
        // Given
        Long timeSlotId = 1L;
        String studentId = "10000001";

        StudentAtTimeSlot sats = new StudentAtTimeSlot();
        StudentAtTimeSlotId id = new StudentAtTimeSlotId(studentId, timeSlotId);
        sats.setId(id);
        sats.setAttendance(AttendanceStatus.PRESENT);

        when(studentAtTimeSlotRepository.findById(id)).thenReturn(Optional.of(sats));

        // When
        AttendanceStatus result = timeSlotService.getStudentAttendance(timeSlotId, studentId);

        // Then
        assertThat(result).isEqualTo(AttendanceStatus.PRESENT);
        verify(studentAtTimeSlotRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("getStudentAttendance devrait retourner UNJUSTIFIED si l'étudiant est absent sans justification")
    void getStudentAttendance_ShouldReturnFalse_WhenNotAttended() {
        // Given
        Long timeSlotId = 1L;
        String studentId = "10000001";

        StudentAtTimeSlot sats = new StudentAtTimeSlot();
        StudentAtTimeSlotId id = new StudentAtTimeSlotId(studentId, timeSlotId);
        sats.setId(id);
        sats.setAttendance(AttendanceStatus.UNJUSTIFIED);

        when(studentAtTimeSlotRepository.findById(id)).thenReturn(Optional.of(sats));

        // When
        AttendanceStatus result = timeSlotService.getStudentAttendance(timeSlotId, studentId);

        // Then
        assertThat(result).isEqualTo(AttendanceStatus.UNJUSTIFIED);
        verify(studentAtTimeSlotRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("updateAttendance devrait mettre à jour plusieurs étudiants")
    void updateAttendance_ShouldUpdateMultipleStudents() {
        // Given
        Long timeSlotId = 10L;
        List<MarkAttendanceRequest> requests = Arrays.asList(
                new MarkAttendanceRequest("10000001", AttendanceStatus.PRESENT),
                new MarkAttendanceRequest("10000002", AttendanceStatus.UNJUSTIFIED),
                new MarkAttendanceRequest("10000003", AttendanceStatus.JUSTIFIED)
        );

        StudentAtTimeSlot student1 = new StudentAtTimeSlot();
        student1.setId(new StudentAtTimeSlotId("10000001", 10L));
        student1.setAttendance(AttendanceStatus.UNJUSTIFIED);

        StudentAtTimeSlot student2 = new StudentAtTimeSlot();
        student2.setId(new StudentAtTimeSlotId("10000002", 10L));
        student2.setAttendance(AttendanceStatus.PRESENT);

        StudentAtTimeSlot student3 = new StudentAtTimeSlot();
        student3.setId(new StudentAtTimeSlotId("10000003", 10L));
        student3.setAttendance(AttendanceStatus.UNJUSTIFIED);

        List<StudentAtTimeSlot> students = Arrays.asList(student1, student2, student3);

        when(studentAtTimeSlotRepository.findAllById(anyList())).thenReturn(students);
        when(studentAtTimeSlotRepository.saveAll(anyList())).thenReturn(students);

        // When
        timeSlotService.updateAttendance(timeSlotId, requests);

        // Then
        assertThat(student1.getAttendance()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(student2.getAttendance()).isEqualTo(AttendanceStatus.UNJUSTIFIED);
        assertThat(student3.getAttendance()).isEqualTo(AttendanceStatus.JUSTIFIED);
        verify(studentAtTimeSlotRepository, times(1)).findAllById(anyList());
        verify(studentAtTimeSlotRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("updateAttendance devrait lever une exception si certains étudiants n'existent pas")
    void updateAttendance_ShouldThrowException_WhenSomeNotFound() {
        // Given
        Long timeSlotId = 10L;
        List<MarkAttendanceRequest> requests = Arrays.asList(
                new MarkAttendanceRequest("10000001", AttendanceStatus.PRESENT),
                new MarkAttendanceRequest("99999999", AttendanceStatus.UNJUSTIFIED)
        );

        StudentAtTimeSlot student1 = new StudentAtTimeSlot();
        student1.setId(new StudentAtTimeSlotId("10000001", 10L));
        student1.setAttendance(AttendanceStatus.UNJUSTIFIED);

        List<StudentAtTimeSlot> students = List.of(student1);

        when(studentAtTimeSlotRepository.findAllById(anyList())).thenReturn(students);

        // When & Then
        assertThatThrownBy(() -> timeSlotService.updateAttendance(timeSlotId, requests))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Some students are not registered to this time slot");

        verify(studentAtTimeSlotRepository, times(1)).findAllById(anyList());
        verify(studentAtTimeSlotRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("updateAttendance ne devrait rien faire avec une liste vide")
    void updateAttendance_ShouldDoNothing_WhenEmptyList() {
        // Given
        Long timeSlotId = 10L;
        List<MarkAttendanceRequest> requests = List.of();

        // When
        timeSlotService.updateAttendance(timeSlotId, requests);

        // Then
        verify(studentAtTimeSlotRepository, never()).findAllById(anyList());
        verify(studentAtTimeSlotRepository, never()).saveAll(anyList());
    }

    @Test
    @DisplayName("getRegisteredStudentAttendance devrait retourner une liste des présences pour tous les étudiants inscrits")
    void getRegisteredStudentAttendance_ShouldReturnAttendanceList() {
        // Given
        Long timeSlotId = 1L;

        StudentAtTimeSlot sats1 = new StudentAtTimeSlot();
        sats1.setId(new StudentAtTimeSlotId("10000001", 1L));
        sats1.setAttendance(AttendanceStatus.PRESENT);

        StudentAtTimeSlot sats2 = new StudentAtTimeSlot();
        sats2.setId(new StudentAtTimeSlotId("10000002", 1L));
        sats2.setAttendance(AttendanceStatus.UNJUSTIFIED);

        StudentAtTimeSlot sats3 = new StudentAtTimeSlot();
        sats3.setId(new StudentAtTimeSlotId("10000003", 1L));
        sats3.setAttendance(AttendanceStatus.JUSTIFIED);

        List<StudentAtTimeSlot> students = Arrays.asList(sats1, sats2, sats3);

        when(studentAtTimeSlotRepository.findByIdTimeSlotId(timeSlotId)).thenReturn(students);

        // When
        List<StudentAttendanceDto> result = timeSlotService.getRegisteredStudentAttendance(timeSlotId);

        // Then
        assertThat(result).hasSize(3);
        assertThat(result.get(0).studentId()).isEqualTo("10000001");
        assertThat(result.get(0).attendanceStatus()).isEqualTo(AttendanceStatus.PRESENT);
        assertThat(result.get(1).studentId()).isEqualTo("10000002");
        assertThat(result.get(1).attendanceStatus()).isEqualTo(AttendanceStatus.UNJUSTIFIED);
        assertThat(result.get(2).studentId()).isEqualTo("10000003");
        assertThat(result.get(2).attendanceStatus()).isEqualTo(AttendanceStatus.JUSTIFIED);
        verify(studentAtTimeSlotRepository, times(1)).findByIdTimeSlotId(timeSlotId);
    }

    @Test
    @DisplayName("getRegisteredStudentAttendance devrait retourner une liste vide si aucun étudiant n'est inscrit")
    void getRegisteredStudentAttendance_ShouldReturnEmptyList_WhenNoStudentsRegistered() {
        // Given
        Long timeSlotId = 1L;

        when(studentAtTimeSlotRepository.findByIdTimeSlotId(timeSlotId)).thenReturn(List.of());

        // When
        List<StudentAttendanceDto> result = timeSlotService.getRegisteredStudentAttendance(timeSlotId);

        // Then
        assertThat(result).isEmpty();
        verify(studentAtTimeSlotRepository, times(1)).findByIdTimeSlotId(timeSlotId);
    }

    @Test
    @DisplayName("create devrait lever une exception si submissionStartTime est après submissionEndTime")
    void create_ShouldThrowException_WhenInvalidSubmissionTimes() {
        // Given
        TimeSlotDto request = new TimeSlotDto(
                1L,
                1,
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(12, 0),  // submission start
                LocalTime.of(8, 0),    // submission end (before start!)
                null
        );

        // When & Then
        assertThatThrownBy(() -> timeSlotService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("submissionStartTime must be before submissionEndTime");

        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    @DisplayName("create devrait lever une exception si le cours n'existe pas")
    void create_ShouldThrowException_WhenCourseNotFound() {
        // Given
        TimeSlotDto request = new TimeSlotDto(
                1L,
                999, // courseId inexistant
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(8, 0),
                LocalTime.of(12, 30),
                null
        );

        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> timeSlotService.create(request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Course not found");

        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    @DisplayName("create devrait inscrire automatiquement tous les étudiants du cours")
    void create_ShouldRegisterAllStudentsFromCourse() {
        // Given
        TimeSlotDto request = new TimeSlotDto(
                1L,
                1,
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(8, 0),
                LocalTime.of(12, 30),
                null
        );

        Course course = new Course();
        course.setId(1);
        course.setName(CourseName.TIW);

        TimeSlot savedTimeSlot = new TimeSlot();
        savedTimeSlot.setId(1L);
        savedTimeSlot.setDate(request.date());
        savedTimeSlot.setStartTime(request.startTime());
        savedTimeSlot.setEndTime(request.endTime());
        savedTimeSlot.setCourse(course);

        Student student1 = new Student();
        student1.setStudentNumber("10000001");

        Student student2 = new Student();
        student2.setStudentNumber("10000002");

        List<Student> students = Arrays.asList(student1, student2);

        when(courseRepository.findById(1L)).thenReturn(Optional.of(course));
        when(timeSlotRepository.save(any(TimeSlot.class))).thenReturn(savedTimeSlot);
        when(timeSlotRepository.findById(1L)).thenReturn(Optional.of(savedTimeSlot));
        when(courseStudentRepository.findStudentsByCourseId(1L)).thenReturn(students);
        when(studentAtTimeSlotRepository.findAllById(anyList())).thenReturn(Collections.emptyList());

        // When
        TimeSlotDto result = timeSlotService.create(request);

        // Then
        assertThat(result).isNotNull();
        verify(timeSlotRepository, times(1)).save(any(TimeSlot.class));
        verify(courseStudentRepository, times(1)).findStudentsByCourseId(1L);
        verify(studentAtTimeSlotRepository, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("findBetweenDates devrait lever une exception si startDate est après endDate")
    void findBetweenDates_ShouldThrowException_WhenInvalidDateRange() {
        // Given
        Integer courseId = 1;
        LocalDate start = LocalDate.of(2025, 1, 31);
        LocalDate end = LocalDate.of(2025, 1, 1);

        // When & Then
        assertThatThrownBy(() -> timeSlotService.findBetweenDates(courseId, start, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("startDate must be before endDate");

        verify(timeSlotRepository, never()).findByCourseIdAndDateBetween(any(), any(), any());
    }

    @Test
    @DisplayName("update devrait lever une exception si le créneau n'existe pas")
    void update_ShouldThrowException_WhenTimeSlotNotFound() {
        // Given
        Long id = 999L;
        TimeSlotDto request = new TimeSlotDto(
                1L,
                1, // courseId (Integer)
                LocalDate.of(2025, 1, 20),
                LocalTime.of(10, 0),
                LocalTime.of(13, 0),
                LocalTime.of(9, 0),
                LocalTime.of(13, 30),
                null
        );

        when(timeSlotRepository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> timeSlotService.update(id, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("TimeSlot not found");

        verify(timeSlotRepository, times(1)).findById(id);
        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }

    @Test
    @DisplayName("update devrait lever une exception si le cours n'existe pas")
    void update_ShouldThrowException_WhenCourseNotFound() {
        // Given
        Long id = 1L;
        TimeSlotDto request = new TimeSlotDto(
                1L,
                999, // courseId inexistant
                LocalDate.of(2025, 1, 20),
                LocalTime.of(10, 0),
                LocalTime.of(13, 0),
                LocalTime.of(9, 0),
                LocalTime.of(13, 30),
                null
        );

        Course existingCourse = new Course();
        existingCourse.setId(1);

        TimeSlot existing = new TimeSlot();
        existing.setId(id);
        existing.setCourse(existingCourse);
        existing.setDate(LocalDate.of(2025, 1, 15));
        existing.setStartTime(LocalTime.of(9, 0));
        existing.setEndTime(LocalTime.of(12, 0));

        when(timeSlotRepository.findById(id)).thenReturn(Optional.of(existing));
        when(courseRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> timeSlotService.update(id, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Course not found");

        verify(timeSlotRepository, never()).save(any(TimeSlot.class));
    }
}

