package back.projet.tpp.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Request to update a student's contract type in a course")
public record UpdateContractTypeRequest(
        @NotBlank(message = "Contract type label is required")
        @Schema(description = "Label of the contract type", example = "Apprentissage")
        String contractTypeLabel
) {}
