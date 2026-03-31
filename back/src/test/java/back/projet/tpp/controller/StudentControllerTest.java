package back.projet.tpp.controller;

import back.projet.tpp.dto.StudentDto;
import back.projet.tpp.service.StudentService;
import back.projet.tpp.service.TimeSlotService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
@DisplayName("StudentController - Integration tests")
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @MockitoBean
    private TimeSlotService timeSlotService;

    @Test
    @DisplayName("GET /api/students should return all students")
    void getAll_ShouldReturnAllStudents() throws Exception {
        // Given
        StudentDto student1 = new StudentDto("12345678", "Jean", "Dupont", "jean.dupont@example.com", List.of(), null, List.of());
        StudentDto student2 = new StudentDto("12345679", "Marie", "Martin", "marie.martin@example.com", List.of(), null, List.of());
        List<StudentDto> students = Arrays.asList(student1, student2);

        when(studentService.getAllStudents()).thenReturn(students);

        // When & Then
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Jean")))
                .andExpect(jsonPath("$[0].lastName", is("Dupont")))
                .andExpect(jsonPath("$[1].firstName", is("Marie")))
                .andExpect(jsonPath("$[1].lastName", is("Martin")));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    @DisplayName("GET /api/students should return empty list when none")
    void getAll_ShouldReturnEmptyList_WhenNoStudents() throws Exception {
        // Given
        when(studentService.getAllStudents()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(studentService, times(1)).getAllStudents();
    }

    @Test
    @DisplayName("GET /api/students/{id} should return a student by id")
    void getUserById_ShouldReturnStudent_WhenExists() throws Exception {
        // Given
        StudentDto student = new StudentDto("12345678", "Jean", "Dupont", "jean.dupont@example.com", List.of(), null, List.of());
        when(studentService.getById("12345678")).thenReturn(student);

        // When & Then
        mockMvc.perform(get("/api/students/12345678"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.studentNumber", is("12345678")))
                .andExpect(jsonPath("$.firstName", is("Jean")))
                .andExpect(jsonPath("$.lastName", is("Dupont")))
                .andExpect(jsonPath("$.email", is("jean.dupont@example.com")));

        verify(studentService, times(1)).getById("12345678");
    }

}
