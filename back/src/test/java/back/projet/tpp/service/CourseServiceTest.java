package back.projet.tpp.service;

import back.projet.tpp.domain.model.entity.Course;
import back.projet.tpp.domain.model.entity.Student;
import back.projet.tpp.domain.model.enums.CourseLevel;
import back.projet.tpp.domain.model.enums.CourseName;
import back.projet.tpp.dto.CourseDto;
import back.projet.tpp.dto.StudentDto;
import back.projet.tpp.mapper.StudentMapper;
import back.projet.tpp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CourseService - Tests unitaires")
class CourseServiceTest {

    @Mock
    private CourseStudentRepository courseStudentRepository;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private CourseService courseService;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        student1 = new Student();
        student1.setStudentNumber("12345678");
        student1.setFirstName("Jean");
        student1.setLastName("Dupont");
        student1.setEmail("jean.dupont@example.com");

        student2 = new Student();
        student2.setStudentNumber("12345679");
        student2.setFirstName("Marie");
        student2.setLastName("Martin");
        student2.setEmail("marie.martin@example.com");

        Course course = new Course();
        course.setId(1);
        course.setName(CourseName.TIW);
        course.setYears("2025-2026");
    }

    @Test
    @DisplayName("getStudentsByCourseId devrait retourner tous les étudiants d'un cours")
    void getStudentsByCourseId_ShouldReturnAllStudents() {
        // Given
        Long courseId = 1L;
        StudentDto dto1 = new StudentDto("12345678", "Jean", "Dupont", "jean.dupont@example.com", List.of(1), null, List.of());
        StudentDto dto2 = new StudentDto("12345679", "Marie", "Martin", "marie.martin@example.com", List.of(1), null, List.of());

        when(courseStudentRepository.findStudentsByCourseId(courseId)).thenReturn(Arrays.asList(student1, student2));
        when(studentMapper.toDto(student1)).thenReturn(dto1);
        when(studentMapper.toDto(student2)).thenReturn(dto2);

        // When
        List<StudentDto> result = courseService.getStudentsByCourseId(courseId);

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).firstName()).isEqualTo("Jean");
        assertThat(result.get(1).firstName()).isEqualTo("Marie");
        verify(courseStudentRepository, times(1)).findStudentsByCourseId(courseId);
    }

    @Test
    @DisplayName("getStudentsByCourseId devrait retourner une liste vide si le cours n'a pas d'étudiants")
    void getStudentsByCourseId_ShouldReturnEmptyList_WhenNoStudents() {
        // Given
        Long courseId = 999L;
        when(courseStudentRepository.findStudentsByCourseId(courseId)).thenReturn(List.of());

        // When
        List<StudentDto> result = courseService.getStudentsByCourseId(courseId);

        // Then
        assertThat(result).isEmpty();
        verify(courseStudentRepository, times(1)).findStudentsByCourseId(courseId);
    }

    @Test
    @DisplayName("createCourse devrait créer un nouveau cours")
    void createCourse_ShouldCreateNewCourse() {
        // Given
        CourseDto courseDto = new CourseDto(null, CourseName.TIW, CourseLevel.M1, "2025-2026", null);
        Course courseEntity = new Course();
        courseEntity.setName(CourseName.SRS);
        courseEntity.setYears("2025-2026");

        when(courseRepository.save(any(Course.class))).thenReturn(courseEntity);

        // When
        courseService.createCourse(courseDto);

        // Then
        verify(courseRepository, times(1)).save(any(Course.class));
    }
}
