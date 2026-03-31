package back.projet.tpp.service;

import back.projet.tpp.dto.SupervisorDto;
import back.projet.tpp.domain.model.entity.Supervisor;
import back.projet.tpp.repository.SupervisorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class SupervisorService {
    private final SupervisorRepository supervisorRepository;

    @Autowired
    public SupervisorService(SupervisorRepository supervisorRepository) {
        this.supervisorRepository = supervisorRepository;
    }

    public List<SupervisorDto> getAllSupervisors(){
        return supervisorRepository.findAll().stream()
                .map(SupervisorDto::fromEntity)
                .collect(Collectors.toList());
    }

    public SupervisorDto getById(Integer id) {
        return supervisorRepository.findById(id)
                .map(SupervisorDto::fromEntity)
                .orElse(null);
    }

    public SupervisorDto createSupervisor(SupervisorDto supervisorDto) {
        Supervisor supervisor = SupervisorDto.toEntity(supervisorDto);
        Supervisor saved = supervisorRepository.save(supervisor);
        return SupervisorDto.fromEntity(saved);
    }
}
