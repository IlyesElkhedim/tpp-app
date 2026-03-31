package back.projet.tpp.controller;

import back.projet.tpp.domain.model.enums.CourseLevel;
import back.projet.tpp.domain.model.enums.CourseName;
import back.projet.tpp.dto.CourseDto;
import back.projet.tpp.dto.StudentDto;
import back.projet.tpp.dto.request.TimeSlotDto;
import back.projet.tpp.service.CourseService;
import back.projet.tpp.service.TimeSlotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CourseController.class)
@DisplayName("CourseController - Tests d'intégration")
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CourseService courseService;

    @MockitoBean
    private TimeSlotService timeSlotService;

    @Test
    @DisplayName("GET /api/courses/{courseId}/students devrait retourner tous les étudiants d'un cours")
    void getStudentsByCourseId_ShouldReturnStudents() throws Exception {
        // Given
        Long courseId = 1L;
        StudentDto student1 = new StudentDto("12345678", "Jean", "Dupont", "jean.dupont@example.com", List.of(1), null);
        StudentDto student2 = new StudentDto("12345679", "Marie", "Martin", "marie.martin@example.com", List.of(1), null);
        List<StudentDto> students = Arrays.asList(student1, student2);

        when(courseService.getStudentsByCourseId(courseId)).thenReturn(students);

        // When & Then
        mockMvc.perform(get("/api/courses/{courseId}/students", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Jean")))
                .andExpect(jsonPath("$[1].firstName", is("Marie")));

        verify(courseService, times(1)).getStudentsByCourseId(courseId);
    }

    @Test
    @DisplayName("GET /api/courses/{courseId}/students devrait retourner une liste vide si pas d'étudiants")
    void getStudentsByCourseId_ShouldReturnEmptyList_WhenNoStudents() throws Exception {
        // Given
        Long courseId = 999L;
        when(courseService.getStudentsByCourseId(courseId)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/courses/{courseId}/students", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseService, times(1)).getStudentsByCourseId(courseId);
    }

    @Test
    @DisplayName("POST /api/courses/{courseId}/students devrait créer plusieurs étudiants")
    void createMultiple_ShouldCreateStudents() throws Exception {
        // Given
        Long courseId = 1L;
        StudentDto student1 = new StudentDto("12345678", "Jean", "Dupont", "jean.dupont@example.com", List.of(1), null);
        StudentDto student2 = new StudentDto("12345679", "Marie", "Martin", "marie.martin@example.com", List.of(1), null);
        List<StudentDto> studentsDto = Arrays.asList(student1, student2);
        List<StudentDto> createdStudents = Arrays.asList(student1, student2);

        when(courseService.createStudentsForCourse(any(Long.class), anyList())).thenReturn(createdStudents);

        // When & Then
        mockMvc.perform(post("/api/courses/{courseId}/students", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentsDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].studentNumber", is("12345678")))
                .andExpect(jsonPath("$[1].studentNumber", is("12345679")));

        verify(courseService, times(1)).createStudentsForCourse(courseId, studentsDto);
    }

    @Test
    @DisplayName("POST /api/courses/{courseId}/students devrait retourner 500 si échec")
    void createMultiple_ShouldReturn500_OnFailure() throws Exception {
        // Given
        Long courseId = 1L;
        StudentDto student1 = new StudentDto("12345678", "Jean", "Dupont", "jean.dupont@example.com", List.of(1), null);
        List<StudentDto> studentsDto = List.of(student1);

        when(courseService.createStudentsForCourse(any(Long.class), anyList())).thenReturn(List.of());

        // When & Then
        mockMvc.perform(post("/api/courses/{courseId}/students", courseId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentsDto)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(courseService, times(1)).createStudentsForCourse(courseId, studentsDto);
    }

    @Test
    @DisplayName("POST /api/courses devrait créer un nouveau cours")
    void createCourse_ShouldCreateNewCourse() throws Exception {
        // Given
        CourseDto courseDto = new CourseDto(null, CourseName.SRS, CourseLevel.M2, "2025-2026", null);
        CourseDto returned = new CourseDto(1, CourseName.SRS, CourseLevel.M2, "2025-2026", null);
        when(courseService.createCourse(any(CourseDto.class))).thenReturn(returned);

        // When & Then
        mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(courseDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.name", is("SRS")));

        verify(courseService, times(1)).createCourse(any(CourseDto.class));
    }

    @Test
    @DisplayName("GET /api/courses/{courseId}/timeslots devrait retourner les TimeSlots entre deux dates pour un cours")
    void getTimeSlotsBetweenDates_ShouldReturnTimeSlots() throws Exception {
        // Given
        Integer courseId = 1;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        TimeSlotDto timeSlot1 = new TimeSlotDto(
                1L,
                1,
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(8, 0),
                LocalTime.of(13, 0),
                null
        );

        TimeSlotDto timeSlot2 = new TimeSlotDto(
                1L,
                1,
                LocalDate.of(2025, 1, 20),
                LocalTime.of(14, 0),
                LocalTime.of(17, 0),
                LocalTime.of(13, 0),
                LocalTime.of(18, 0),
                null
        );

        List<TimeSlotDto> timeSlots = Arrays.asList(timeSlot1, timeSlot2);

        when(timeSlotService.findBetweenDates(courseId, startDate, endDate)).thenReturn(timeSlots);

        // When & Then
        mockMvc.perform(get("/api/courses/{courseId}/timeslots", courseId)
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].courseId", is(1)))
                .andExpect(jsonPath("$[1].courseId", is(1)));

        verify(timeSlotService, times(1)).findBetweenDates(courseId, startDate, endDate);
    }

    @Test
    @DisplayName("GET /api/courses/{courseId}/timeslots devrait retourner une liste vide si aucun TimeSlot")
    void getTimeSlotsBetweenDates_ShouldReturnEmptyList_WhenNoTimeSlots() throws Exception {
        // Given
        Integer courseId = 1;
        LocalDate startDate = LocalDate.of(2025, 1, 1);
        LocalDate endDate = LocalDate.of(2025, 1, 31);

        when(timeSlotService.findBetweenDates(courseId, startDate, endDate)).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/courses/{courseId}/timeslots", courseId)
                        .param("startDate", "2025-01-01")
                        .param("endDate", "2025-01-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(timeSlotService, times(1)).findBetweenDates(courseId, startDate, endDate);
    }
}
