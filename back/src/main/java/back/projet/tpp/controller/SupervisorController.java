package back.projet.tpp.controller;

import back.projet.tpp.dto.SupervisorDto;
import back.projet.tpp.service.SupervisorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supervisors")
@Tag(name = "Supervisors", description = "API for managing supervisors")
public class SupervisorController {

    private final SupervisorService supervisorService;

    public SupervisorController(SupervisorService supervisorService) {
        this.supervisorService = supervisorService;
    }

    @Operation(
            summary = "Retrieve all supervisors",
            description = "Returns the list of all supervisors"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supervisors retrieved")
    })
    @GetMapping
    public ResponseEntity<List<SupervisorDto>> getAll() {
        List<SupervisorDto> supervisors = supervisorService.getAllSupervisors();
        return ResponseEntity.ok(supervisors);
    }

    @Operation(
            summary = "Retrieve a supervisor by ID",
            description = "Returns a specific supervisor by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Supervisor found"),
            @ApiResponse(responseCode = "404", description = "Supervisor not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<SupervisorDto> getById(@Parameter(description = "Supervisor ID", required = true) @PathVariable Integer id) {
        try {
            return ResponseEntity.ok(supervisorService.getById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(
            summary = "Create a supervisor",
            description = "Create a new supervisor"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Supervisor created"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<SupervisorDto> createSupervisor(@RequestBody SupervisorDto supervisorDto) {
        SupervisorDto created = supervisorService.createSupervisor(supervisorDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

}
