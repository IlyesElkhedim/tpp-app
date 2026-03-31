package back.projet.tpp.controller;

import back.projet.tpp.dto.CourseDto;
import back.projet.tpp.dto.StudentDto;
import back.projet.tpp.dto.request.TimeSlotDto;
import back.projet.tpp.dto.request.UpdateContractTypeRequest;
import back.projet.tpp.service.CourseService;
import back.projet.tpp.service.TimeSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/courses")
@Tag(name = "Courses", description = "API for managing courses")
public class CourseController {

    private final CourseService courseService;

    private final TimeSlotService timeSlotService;

    public CourseController(CourseService courseService, TimeSlotService timeSlotService) {
        this.courseService = courseService;
        this.timeSlotService = timeSlotService;
    }

    /**
     * GET /api/courses
     * Get all courses
     */
    @Operation(
            summary = "Retrieve all courses",
            description = "Returns the list of all courses"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of courses retrieved successfully"),
            @ApiResponse(responseCode = "204", description = "No courses found")
    })
    @GetMapping
    public ResponseEntity<List<CourseDto>> getAllCourses() {
        List<CourseDto> courses = courseService.getAllCourses();

        return ResponseEntity.ok(courses);
    }

    /**
     * GET /api/courses/{courseId}
     * Get a course by id
     */
    @Operation(
            summary = "Get a course by id",
            description = "Return the details of a specific course"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Course found"),
            @ApiResponse(responseCode = "204", description = "No course found")
    })
    @GetMapping("/{courseId}")
    public ResponseEntity<CourseDto> getCourseById(
            @PathVariable Long courseId
    ) {
        try {
            CourseDto course = courseService.getCourseById(courseId);
            return ResponseEntity.ok(course);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }

    }

    /**
     * DELETE /api/courses/{courseId}
     * Delete a course by ID
     */
    @Operation(
            summary = "Delete a course",
            description = "Allows to delete a specific course by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Course deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @DeleteMapping("/{courseId}")
    public ResponseEntity<String> deleteCourse(
            @Parameter(description = "Course ID", required = true)
            @PathVariable Long courseId
    ) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.noContent().build();
    }

    /**
     * GET /api/courses/{courseId}/students
     * Get all students by course ID
     */
    @Operation(
            summary = "Retrieve students of a course",
            description = "Returns the list of all students enrolled in a specific course"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of students retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Course not found")
    })
    @GetMapping("/{courseId}/students")
    public ResponseEntity<List<StudentDto>> getStudentsByCourseId(
            @Parameter(description = "Course ID", required = true)
            @PathVariable Integer courseId) {
        List<StudentDto> students = courseService.getStudentsByCourseId(Long.valueOf(courseId));
        return ResponseEntity.ok(students);
    }

    /**
     * POST /api/courses/{courseId}/students
     * Create multiple students for a course
     */
    @Operation(
            summary = "Create multiple students for a course",
            description = "Create multiple students and automatically enroll them in the specified course"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Students created successfully"),
            @ApiResponse(responseCode = "500", description = "Error while creating students")
    })
    @PostMapping("/{courseId}/students")
    public ResponseEntity<List<StudentDto>> createMultiple(
            @Parameter(description = "Course ID", required = true)
            @PathVariable Long courseId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of students to create",
                    required = true
            )
            @RequestBody List<StudentDto> studentsDto
    ) {
        List<StudentDto> createdStudents = courseService.createStudentsForCourse(courseId, studentsDto);
        if (!createdStudents.isEmpty()) {
            return ResponseEntity.ok(createdStudents);
        }
        return ResponseEntity.status(500).body(List.of());
    }

    /**
     * POST /api/courses
     * Create a new course
     */
    @Operation(
            summary = "Create a new course",
            description = "Allows creating a new course"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Course created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data"),
            @ApiResponse(responseCode = "404", description = "Supervisor not found")
    })
    @PostMapping
    public ResponseEntity<CourseDto> createCourse(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Course information to create",
                    required = true
            )
            @RequestBody CourseDto courseDto
    ) {
        try {
            CourseDto created = courseService.createCourse(courseDto);
            return ResponseEntity.status(201).body(created);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // GET all timeslots between two dates
    @Operation(
            summary = "Retrieve time slots between two dates",
            description = "Returns all time slots between a start date and an end date"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of time slots retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid date format")
    })
    @GetMapping("/{courseId}/timeslots")
    public ResponseEntity<List<TimeSlotDto>> getTimeSlotsBetweenDates(
            @Parameter(description = "Start date (format: YYYY-MM-DD)", required = true, example = "2025-01-01")
            @RequestParam("startDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,

            @Parameter(description = "End date (format: YYYY-MM-DD)", required = true, example = "2025-12-31")
            @RequestParam("endDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,

            @PathVariable Integer courseId
    ) {

        return ResponseEntity.ok(
                timeSlotService.findBetweenDates(courseId, startDate, endDate)
        );
    }

    /**
     * PATCH /api/courses/{courseId}/students/{studentNumber}/contract-type
     * Update the contract type of a student in a course
     */
    @Operation(
            summary = "Update contract type of a student in a course",
            description = "Changes the contract type (e.g. Apprentissage, Stage en entreprise) for a specific student within a course"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Contract type updated successfully"),
            @ApiResponse(responseCode = "404", description = "Student, course or contract type not found")
    })
    @PatchMapping("/{courseId}/students/{studentNumber}/contract-type")
    public ResponseEntity<Void> updateContractType(
            @Parameter(description = "Course ID", required = true)
            @PathVariable Integer courseId,
            @Parameter(description = "Student number", required = true)
            @PathVariable String studentNumber,
            @RequestBody UpdateContractTypeRequest request
    ) {
        try {
            courseService.updateStudentContractType(courseId, studentNumber, request.contractTypeLabel());
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
