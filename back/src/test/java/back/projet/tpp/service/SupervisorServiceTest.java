package back.projet.tpp.service;

import back.projet.tpp.domain.model.entity.Supervisor;
import back.projet.tpp.dto.SupervisorDto;
import back.projet.tpp.repository.SupervisorRepository;
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
@DisplayName("SupervisorService - Unit Tests")
class SupervisorServiceTest {

    @Mock
    private SupervisorRepository supervisorRepository;

    @InjectMocks
    private SupervisorService supervisorService;

    private Supervisor supervisor1;
    private Supervisor supervisor2;

    @BeforeEach
    void setUp() {
        supervisor1 = new Supervisor();
        supervisor1.setId(1);
        supervisor1.setFirstName("Pierre");
        supervisor1.setLastName("Durand");
        supervisor1.setEmail("pierre.durand@example.com");

        supervisor2 = new Supervisor();
        supervisor2.setId(2);
        supervisor2.setFirstName("Sophie");
        supervisor2.setLastName("Bernard");
        supervisor2.setEmail("sophie.bernard@example.com");
    }

    @Test
    @DisplayName("getAllSupervisors should return all supervisors")
    void getAllSupervisors_ShouldReturnAllSupervisors() {
        // Given
        when(supervisorRepository.findAll()).thenReturn(Arrays.asList(supervisor1, supervisor2));

        // When
        List<SupervisorDto> result = supervisorService.getAllSupervisors();

        // Then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).firstName()).isEqualTo("Pierre");
        assertThat(result.get(1).firstName()).isEqualTo("Sophie");
        verify(supervisorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getAllSupervisors should return empty list when no supervisors")
    void getAllSupervisors_ShouldReturnEmptyList_WhenNoSupervisors() {
        // Given
        when(supervisorRepository.findAll()).thenReturn(List.of());

        // When
        List<SupervisorDto> result = supervisorService.getAllSupervisors();

        // Then
        assertThat(result).isEmpty();
        verify(supervisorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("getById should return supervisor when exists")
    void getById_ShouldReturnSupervisor_WhenExists() {
        // Given
        when(supervisorRepository.findById(1)).thenReturn(Optional.of(supervisor1));

        // When
        SupervisorDto result = supervisorService.getById(1);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.firstName()).isEqualTo("Pierre");
        assertThat(result.lastName()).isEqualTo("Durand");
        assertThat(result.email()).isEqualTo("pierre.durand@example.com");
        verify(supervisorRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("getById should return null when supervisor does not exist")
    void getById_ShouldReturnNull_WhenNotExists() {
        // Given
        when(supervisorRepository.findById(999)).thenReturn(Optional.empty());

        // When
        SupervisorDto result = supervisorService.getById(999);

        // Then
        assertThat(result).isNull();
        verify(supervisorRepository, times(1)).findById(999);
    }
}
