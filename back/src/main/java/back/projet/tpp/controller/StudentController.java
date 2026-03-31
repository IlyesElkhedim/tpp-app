package back.projet.tpp.controller;

import back.projet.tpp.dto.StudentDto;
import back.projet.tpp.service.StudentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/students")
@Tag(name = "Students", description = "API for managing students")
public class StudentController {
    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    /**
     * GET /api/students
     * Get all students
     */
    @Operation(
            summary = "Retrieve all students",
            description = "Returns the complete list of all students registered in the system"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of students retrieved successfully")
    })
    @GetMapping
    public ResponseEntity<List<StudentDto>> getAll() {
        List<StudentDto> studentDtos = studentService.getAllStudents();
        return ResponseEntity.ok(studentDtos);
    }

    /**
     * GET /api/students/{id}
     * Get student by ID
     */
    @Operation(
            summary = "Retrieve a student by their ID",
            description = "Returns the details of a specific student"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Student found"),
            @ApiResponse(responseCode = "404", description = "Student not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<StudentDto> getUserById(
            @Parameter(description = "Student number (8 digits)", required = true)
            @PathVariable String id
    ) {
        StudentDto studentDto = studentService.getById(id);
        return ResponseEntity.ok(studentDto);
    }
}
