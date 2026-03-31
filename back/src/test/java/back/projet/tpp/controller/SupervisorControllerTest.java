package back.projet.tpp.controller;

import back.projet.tpp.dto.SupervisorDto;
import back.projet.tpp.service.SupervisorService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@WebMvcTest(SupervisorController.class)
@DisplayName("SupervisorController - Integration Tests")
class SupervisorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private SupervisorService supervisorService;

    @Test
    @DisplayName("GET /api/supervisors should return all supervisors")
    void getAllSupervisors_ShouldReturnAllSupervisors() throws Exception {
        // Given
        SupervisorDto supervisor1 = new SupervisorDto(1, "Pierre", "Durand", "pierre.durand@example.com");
        SupervisorDto supervisor2 = new SupervisorDto(2, "Sophie", "Bernard", "sophie.bernard@example.com");
        List<SupervisorDto> supervisors = Arrays.asList(supervisor1, supervisor2);

        when(supervisorService.getAllSupervisors()).thenReturn(supervisors);

        // When & Then
        mockMvc.perform(get("/api/supervisors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].firstName", is("Pierre")))
                .andExpect(jsonPath("$[0].lastName", is("Durand")))
                .andExpect(jsonPath("$[1].firstName", is("Sophie")))
                .andExpect(jsonPath("$[1].lastName", is("Bernard")));

        verify(supervisorService, times(1)).getAllSupervisors();
    }

    @Test
    @DisplayName("GET /api/supervisors should return an empty list when no supervisors")
    void getAllSupervisors_ShouldReturnEmptyList_WhenNoSupervisors() throws Exception {
        // Given
        when(supervisorService.getAllSupervisors()).thenReturn(List.of());

        // When & Then
        mockMvc.perform(get("/api/supervisors"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        verify(supervisorService, times(1)).getAllSupervisors();
    }

    @Test
    @DisplayName("GET /api/supervisors/{id} should return a supervisor by its ID")
    void getSupervisorById_ShouldReturnSupervisor_WhenExists() throws Exception {
        // Given
        SupervisorDto supervisor = new SupervisorDto(1, "Pierre", "Durand", "pierre.durand@example.com");
        when(supervisorService.getById(1)).thenReturn(supervisor);

        // When & Then
        mockMvc.perform(get("/api/supervisors/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.firstName", is("Pierre")))
                .andExpect(jsonPath("$.lastName", is("Durand")))
                .andExpect(jsonPath("$.email", is("pierre.durand@example.com")));

        verify(supervisorService, times(1)).getById(1);
    }

    @Test
    @DisplayName("GET /api/supervisors/{id} should return 200 with null if supervisor does not exist")
    void getSupervisorById_ShouldReturnNull_WhenNotExists() throws Exception {
        // Given
        when(supervisorService.getById(999)).thenReturn(null);

        // When & Then
        mockMvc.perform(get("/api/supervisors/999"))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

        verify(supervisorService, times(1)).getById(999);
    }
}
