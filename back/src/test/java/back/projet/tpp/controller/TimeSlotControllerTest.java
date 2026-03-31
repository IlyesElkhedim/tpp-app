package back.projet.tpp.controller;

import back.projet.tpp.dto.request.MarkAttendanceRequest;
import back.projet.tpp.dto.request.TimeSlotDto;
import back.projet.tpp.dto.response.StudentAttendanceDto;
import back.projet.tpp.domain.model.enums.AttendanceStatus;
import back.projet.tpp.service.TimeSlotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TimeSlotController.class)
@DisplayName("TimeSlotController - Tests d'intégration")
class TimeSlotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TimeSlotService timeSlotService;

    @Test
    @DisplayName("POST /api/timeslots devrait créer un nouveau TimeSlot")
    void createTimeSlot_ShouldCreateNewTimeSlot() throws Exception {
        // Given
        TimeSlotDto request = new TimeSlotDto(
                1L,
                1, // courseId
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(8, 0),
                LocalTime.of(13, 0),
                null
        );

        TimeSlotDto created = new TimeSlotDto(
                1L,
                1,
                LocalDate.of(2025, 1, 15),
                LocalTime.of(9, 0),
                LocalTime.of(12, 0),
                LocalTime.of(8, 0),
                LocalTime.of(13, 0),
                null
        );

        when(timeSlotService.create(any(TimeSlotDto.class))).thenReturn(created);

        // When & Then
        mockMvc.perform(post("/api/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.courseId", is(1)))
                .andExpect(jsonPath("$.date", is("2025-01-15")))
                .andExpect(jsonPath("$.startTime", is("09:00:00")))
                .andExpect(jsonPath("$.endTime", is("12:00:00")));

        verify(timeSlotService, times(1)).create(any(TimeSlotDto.class));
    }

    @Test
    @DisplayName("POST /api/timeslots devrait retourner 400 si les données sont invalides")
    void createTimeSlot_ShouldReturn400_WhenInvalidData() throws Exception {
        // Given - startTime après endTime (invalide)
        TimeSlotDto invalidRequest = new TimeSlotDto(
                1L,
                1, // courseId (Integer)
                LocalDate.of(2025, 1, 15),
                LocalTime.of(12, 0), // start après end
                LocalTime.of(9, 0),  // end avant start
                LocalTime.of(8, 0),
                LocalTime.of(13, 0),
                null
        );

        when(timeSlotService.create(any(TimeSlotDto.class)))
                .thenThrow(new IllegalArgumentException("startTime must be before endTime"));

        // When & Then
        mockMvc.perform(post("/api/timeslots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());

        verify(timeSlotService, times(1)).create(any(TimeSlotDto.class));
    }


    @Test
    @DisplayName("DELETE /api/timeslots/{id} devrait supprimer un TimeSlot")
    void deleteTimeSlot_ShouldDeleteTimeSlot() throws Exception {
        // Given
        Long id = 1L;
        doNothing().when(timeSlotService).deleteById(id);

        // When & Then
        mockMvc.perform(delete("/api/timeslots/{id}", id))
                .andExpect(status().isNoContent());

        verify(timeSlotService, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("DELETE /api/timeslots/{id} devrait retourner 404 si TimeSlot n'existe pas")
    void deleteTimeSlot_ShouldReturn404_WhenNotExists() throws Exception {
        // Given
        Long id = 999L;
        doThrow(new EntityNotFoundException("TimeSlot not found"))
                .when(timeSlotService).deleteById(id);

        // When & Then
        mockMvc.perform(delete("/api/timeslots/{id}", id))
                .andExpect(status().isNotFound());

        verify(timeSlotService, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("PUT /api/timeslots/{id} devrait mettre à jour un TimeSlot")
    void updateTimeSlot_ShouldUpdateTimeSlot() throws Exception {
        // Given
        Long id = 1L;
        TimeSlotDto request = new TimeSlotDto(
                1L,
                1,
                LocalDate.of(2025, 2, 10),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                LocalTime.of(9, 0),
                LocalTime.of(13, 0),
                null
        );

        TimeSlotDto updated = new TimeSlotDto(
                1L,
                1,
                LocalDate.of(2025, 2, 10),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                LocalTime.of(9, 0),
                LocalTime.of(13, 0),
                null
        );

        when(timeSlotService.update(any(Long.class), any(TimeSlotDto.class))).thenReturn(updated);

        // When & Then
        mockMvc.perform(put("/api/timeslots/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.courseId", is(1)))
                .andExpect(jsonPath("$.date", is("2025-02-10")))
                .andExpect(jsonPath("$.startTime", is("10:00:00")));

        verify(timeSlotService, times(1)).update(id, request);
    }

    @Test
    @DisplayName("PUT /api/timeslots/{id} devrait retourner 404 si TimeSlot n'existe pas")
    void updateTimeSlot_ShouldReturn404_WhenNotExists() throws Exception {
        // Given
        Long id = 999L;
        TimeSlotDto request = new TimeSlotDto(
                1L,
                1, // courseId
                LocalDate.of(2025, 2, 10),
                LocalTime.of(10, 0),
                LocalTime.of(12, 0),
                LocalTime.of(9, 0),
                LocalTime.of(13, 0),
                null
        );

        when(timeSlotService.update(any(Long.class), any(TimeSlotDto.class)))
                .thenThrow(new EntityNotFoundException("TimeSlot not found"));

        // When & Then
        mockMvc.perform(put("/api/timeslots/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());

        verify(timeSlotService, times(1)).update(id, request);
    }

    @Test
    @DisplayName("PUT /api/timeslots/{id}/attendance devrait mettre à jour les présences")
    void updateAttendance_ShouldUpdateMultipleStudents() throws Exception {
        // Given
        Long timeSlotId = 1L;
        List<MarkAttendanceRequest> requests = Arrays.asList(
                new MarkAttendanceRequest("10000001", AttendanceStatus.PRESENT),
                new MarkAttendanceRequest("10000002", AttendanceStatus.UNJUSTIFIED),
                new MarkAttendanceRequest("10000003", AttendanceStatus.JUSTIFIED)
        );

        doNothing().when(timeSlotService).updateAttendance(eq(timeSlotId), anyList());

        // When & Then
        mockMvc.perform(put("/api/timeslots/{id}/attendance", timeSlotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isNoContent());

        verify(timeSlotService, times(1)).updateAttendance(eq(timeSlotId), anyList());
    }

    @Test
    @DisplayName("PUT /api/timeslots/{id}/attendance devrait retourner 404 si un étudiant n'existe pas")
    void updateAttendance_ShouldReturn404_WhenStudentNotFound() throws Exception {
        // Given
        Long timeSlotId = 1L;
        List<MarkAttendanceRequest> requests = Arrays.asList(
                new MarkAttendanceRequest("10000001", AttendanceStatus.PRESENT),
                new MarkAttendanceRequest("99999999", AttendanceStatus.UNJUSTIFIED)
        );

        doThrow(new EntityNotFoundException("Some StudentAtTimeSlot entries not found"))
                .when(timeSlotService).updateAttendance(eq(timeSlotId), anyList());

        // When & Then
        mockMvc.perform(put("/api/timeslots/{id}/attendance", timeSlotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isNotFound());

        verify(timeSlotService, times(1)).updateAttendance(eq(timeSlotId), anyList());
    }

    @Test
    @DisplayName("PUT /api/timeslots/{id}/attendance avec liste vide devrait retourner 204")
    void updateAttendance_ShouldReturn204_WhenEmptyList() throws Exception {
        // Given
        Long timeSlotId = 1L;
        List<MarkAttendanceRequest> requests = List.of();

        doNothing().when(timeSlotService).updateAttendance(eq(timeSlotId), anyList());

        // When & Then
        mockMvc.perform(put("/api/timeslots/{id}/attendance", timeSlotId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requests)))
                .andExpect(status().isNoContent());

        verify(timeSlotService, times(1)).updateAttendance(eq(timeSlotId), anyList());
    }

    @Test
    @DisplayName("GET /api/timeslots/{id}/attendance devrait retourner les présences de tous les étudiants inscrits")
    void getRegisteredStudentAttendance_ShouldReturnAllAttendances() throws Exception {
        // Given
        Long timeSlotId = 1L;
        List<StudentAttendanceDto> attendanceList = Arrays.asList(
                new StudentAttendanceDto("10000001", AttendanceStatus.PRESENT),
                new StudentAttendanceDto("10000002", AttendanceStatus.UNJUSTIFIED),
                new StudentAttendanceDto("10000003", AttendanceStatus.JUSTIFIED)
        );

        when(timeSlotService.getRegisteredStudentAttendance(timeSlotId)).thenReturn(attendanceList);

        // When & Then
        mockMvc.perform(get("/api/timeslots/{id}/attendance", timeSlotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].studentId", is("10000001")))
                .andExpect(jsonPath("$[0].attendanceStatus", is("PRESENT")))
                .andExpect(jsonPath("$[1].studentId", is("10000002")))
                .andExpect(jsonPath("$[1].attendanceStatus", is("UNJUSTIFIED")))
                .andExpect(jsonPath("$[2].studentId", is("10000003")))
                .andExpect(jsonPath("$[2].attendanceStatus", is("JUSTIFIED")));

        verify(timeSlotService, times(1)).getRegisteredStudentAttendance(timeSlotId);
    }

    @Test
    @DisplayName("GET /api/timeslots/{id}/attendance devrait retourner une liste vide si aucun étudiant inscrit")
    void getRegisteredStudentAttendance_ShouldReturnEmptyList_WhenNoStudents() throws Exception {
        // Given
        Long timeSlotId = 1L;
        List<StudentAttendanceDto> emptyList = List.of();

        when(timeSlotService.getRegisteredStudentAttendance(timeSlotId)).thenReturn(emptyList);

        // When & Then
        mockMvc.perform(get("/api/timeslots/{id}/attendance", timeSlotId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(timeSlotService, times(1)).getRegisteredStudentAttendance(timeSlotId);
    }

    @Test
    @DisplayName("GET /api/timeslots/{id}/student/{studentId}/attendance devrait retourner la présence d'un étudiant")
    void getStudentAttendance_ShouldReturnAttendance() throws Exception {
        // Given
        Long timeSlotId = 1L;
        String studentId = "10000001";

        when(timeSlotService.getStudentAttendance(timeSlotId, studentId)).thenReturn(AttendanceStatus.PRESENT);

        // When & Then
        mockMvc.perform(get("/api/timeslots/{id}/student/{studentId}/attendance", timeSlotId, studentId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is("PRESENT")));

        verify(timeSlotService, times(1)).getStudentAttendance(timeSlotId, studentId);
    }
}


