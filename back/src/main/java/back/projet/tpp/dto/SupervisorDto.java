package back.projet.tpp.dto;

import back.projet.tpp.domain.model.entity.Supervisor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO representing a supervisor")
public record SupervisorDto(
        @Schema(description = "Unique supervisor ID", example = "1")
        Integer id,

        @Schema(description = "Supervisor's first name", example = "Marie", required = true)
        String firstName,

        @Schema(description = "Supervisor's last name", example = "Martin", required = true)
        String lastName,

        @Schema(description = "Supervisor's email address", example = "marie.martin@example.com", required = true)
        String email
) {
    public static SupervisorDto fromEntity(Supervisor supervisor) {
        return new SupervisorDto(
                supervisor.getId(),
                supervisor.getFirstName(),
                supervisor.getLastName(),
                supervisor.getEmail()
        );
    }

    public static Supervisor toEntity(SupervisorDto dto) {
        Supervisor supervisor = new Supervisor();
        supervisor.setId(dto.id());
        supervisor.setFirstName(dto.firstName());
        supervisor.setLastName(dto.lastName());
        supervisor.setEmail(dto.email());
        return supervisor;
    }
}
