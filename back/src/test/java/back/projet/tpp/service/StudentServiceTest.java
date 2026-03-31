package back.projet.tpp.service;

import back.projet.tpp.domain.model.entity.Student;
import back.projet.tpp.dto.StudentDto;
import back.projet.tpp.mapper.StudentMapper;
import back.projet.tpp.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("StudentService - Unit tests")
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private StudentMapper studentMapper;

    @InjectMocks
    private StudentService studentService;

    private Student student1;
    private Student student2;

    @BeforeEach
    void setUp() {
        student1 = new Student();
        student1.setStudentNumber("10012345");
        student1.setFirstName("Jean");
        student1.setLastName("Dupont");
        student1.setEmail("jean.dupont@example.com");

        student2 = new Student();
        student2.setStudentNumber("10067890");
        student2.setFirstName("Marie");
        student2.setLastName("Martin");
        student2.setEmail("marie.martin@example.com");
    }

    @Test
    @DisplayName("getAllStudents should return all students")
    void getAllStudents_ShouldReturnAllStudents() {
        // Given
        StudentDto dto1 = new StudentDto("10012345", "Jean", "Dupont", "jean.dupont@example.com", List.of());
        StudentDto dto2 = new StudentDto("10067890", "Marie", "Martin", "marie.martin@example.com", List.of());
        when(studentRepository.findAll()).thenReturn(Arrays.asList(student1, student2));
        when(studentMapper.toDto(student1)).thenReturn(dto1);
        when(studentMapper.toDto(student2)).thenReturn(dto2);

        // When
        List<StudentDto> result = studentService.getAllStudents();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).firstName()).isEqualTo("Jean");
        assertThat(result.get(1).firstName()).isEqualTo("Marie");
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllStudents should return empty list when none")
    void getAllStudents_ShouldReturnEmptyList_WhenNoStudents() {
        // Given
        when(studentRepository.findAll()).thenReturn(List.of());
        // No mapper calls needed for empty list

        // When
        List<StudentDto> result = studentService.getAllStudents();

        // Then
        assertThat(result).isEmpty();
        verify(studentRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getById should return a student when exists")
    void getById_ShouldReturnStudent_WhenExists() {
        // Given
        StudentDto dto1 = new StudentDto("10012345", "Jean", "Dupont", "jean.dupont@example.com", List.of());
        when(studentRepository.findById("10012345")).thenReturn(Optional.of(student1));
        when(studentMapper.toDto(student1)).thenReturn(dto1);

        // When
        StudentDto result = studentService.getById("10012345");

        // Then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Jean");
        assertThat(result.lastName()).isEqualTo("Dupont");
        verify(studentRepository, times(1)).findById("10012345");
    }

    @Test
    @DisplayName("getById should return null when student does not exist")
    void getById_ShouldReturnNull_WhenNotExists() {
        // Given
        when(studentRepository.findById("99999999")).thenReturn(Optional.empty());

        // When
        StudentDto result = studentService.getById("99999999");

        // Then
        assertThat(result).isNull();
        verify(studentRepository, times(1)).findById("99999999");
    }
}
