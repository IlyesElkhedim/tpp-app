package back.projet.tpp.controller;

import back.projet.tpp.domain.model.enums.AttendanceStatus;
import back.projet.tpp.dto.WorkDescriptionDto;
import back.projet.tpp.dto.WorkSubmissionDto;
import back.projet.tpp.dto.request.MarkAttendanceRequest;
import back.projet.tpp.dto.request.TimeSlotDto;
import back.projet.tpp.dto.request.WorkSubmissionGradeRequest;
import back.projet.tpp.dto.response.StudentAttendanceDto;
import back.projet.tpp.service.TimeSlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.persistence.EntityNotFoundException;
import java.io.IOException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/timeslots")
@Tag(name = "TimeSlots", description = "API for managing time slots (TPP)")
public class TimeSlotController {

    private final TimeSlotService timeSlotService;

    public TimeSlotController(TimeSlotService timeSlotService) {
        this.timeSlotService = timeSlotService;
    }

    // CREATE a timeslot
    @Operation(
            summary = "Create a new time slot",
            description = "Create a new TPP time slot with associated information"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Time slot created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PostMapping
    public ResponseEntity<TimeSlotDto> createTimeSlot(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Time slot information to create",
                    required = true
            )
            @RequestBody TimeSlotDto request) {

        TimeSlotDto created = timeSlotService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // READ a timeslot by id
    @Operation(
            summary = "Get a time slot by its ID",
            description = "Return details of a specific time slot"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time slot found"),
            @ApiResponse(responseCode = "404", description = "Time slot not found")
    }
    )
    @GetMapping("/{timeslotId}")
    public ResponseEntity<TimeSlotDto> getTimeSlotById(
            @Parameter(description = "Time slot ID", required = true)
            @PathVariable Long timeslotId) {
        TimeSlotDto timeSlotDto = timeSlotService.getById(timeslotId);
        return ResponseEntity.ok(timeSlotDto);
    }

    // DELETE a timeslot by id
    @Operation(
            summary = "Delete a time slot",
            description = "Delete a specific time slot by its ID"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Time slot deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Time slot not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTimeSlot(
            @Parameter(description = "ID of the time slot to delete", required = true)
            @PathVariable Long id) {
        timeSlotService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // UPDATE a timeslot by id
    @Operation(
            summary = "Update a time slot",
            description = "Update information of an existing time slot"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time slot updated successfully"),
            @ApiResponse(responseCode = "404", description = "Time slot not found"),
            @ApiResponse(responseCode = "400", description = "Invalid data")
    })
    @PutMapping("/{id}")
    public ResponseEntity<TimeSlotDto> updateTimeSlot(
            @Parameter(description = "ID of the time slot to update", required = true)
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New time slot information",
                    required = true
            )
            @RequestBody TimeSlotDto request) {

        TimeSlotDto updated = timeSlotService.update(id, request);
        return ResponseEntity.ok(updated);
    }


    @Operation(
            summary = "Check a student's attendance",
            description = "Checks if a student has an attendance record for a given time slot"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendance checked successfully"),
            @ApiResponse(responseCode = "404", description = "Time slot or student not found")
    })
    @GetMapping("/{id}/student/{studentId}/attendance")
    public ResponseEntity<AttendanceStatus> getStudentAttendance(
            @Parameter(description = "Time slot ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Student number (8 digits)", required = true)
            @PathVariable String studentId
    ) {
        try {
            AttendanceStatus status = timeSlotService.getStudentAttendance(id, studentId);
            return ResponseEntity.ok(status);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * GET /api/timeslots/{id}/attendance
     * Get attendance for all registered students in a single request
     * Batch-optimized processing: single findByIdTimeSlotId query
     */
    @Operation(
            summary = "Retrieve attendance for all students registered to a time slot",
            description = "Returns attendance of all students registered to a time slot with their full information (first name, last name, email, attendance status)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Attendances retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Time slot not found")
    })
    @GetMapping("/{id}/attendance")
    public ResponseEntity<List<StudentAttendanceDto>> getMultipleAttendance(
            @Parameter(description = "Time slot ID", required = true)
            @PathVariable("id") Long timeSlotId
    ) {
        List<StudentAttendanceDto> attendanceList = timeSlotService.getRegisteredStudentAttendance(timeSlotId);
        return ResponseEntity.ok(attendanceList);
    }

    /**
     * PUT /api/timeslots/{id}/attendance
     * Update attendance for multiple students in a single request
     * Batch-optimized processing: single findAllById + single saveAll
     */
    @Operation(
            summary = "Update attendance for multiple students for a time slot",
            description = "Allows updating (present/absent) multiple students for a single time slot in one request (batch-optimized)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Attendances updated successfully"),
            @ApiResponse(responseCode = "404", description = "One or more students not found for this time slot"),
            @ApiResponse(responseCode = "400", description = "Empty list or invalid data")
    })
    @PutMapping("/{id}/attendance")
    public ResponseEntity<Void> updateAttendance(
            @Parameter(description = "Time slot ID", required = true)
            @PathVariable("id") Long timeSlotId,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "List of attendance updates (studentId + attended status)",
                    required = true
            )
            @RequestBody List<MarkAttendanceRequest> requests
    ) {
        timeSlotService.updateAttendance(timeSlotId, requests);
        return ResponseEntity.noContent().build();
    }

    /**
     * POST /api/timeslots/{idStudent}/{idTimeSlot}
     * Add a work submission for a student and a time slot
     */
    @Operation(
            summary = "Create a work submission for a time slot for a student",
            description = "Create a work submission for a time slot for a student"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Work submission created"),
            @ApiResponse(responseCode = "400", description = "Data not valid"),
            @ApiResponse(responseCode = "404", description = "Student or time slot not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/{idStudent}/{idTimeSlot}")
    public ResponseEntity<String> createWorkSubmission(
            @Parameter(description = "Student Id", required = true)
            @PathVariable String idStudent,
            @Parameter(description = "Time slot Id", required = true)
            @PathVariable Long idTimeSlot,
            @RequestBody List<WorkDescriptionDto> request
    ) {
        timeSlotService.createWorkSubmission(
                idStudent,
                idTimeSlot,
                request
        );
        return ResponseEntity.status(201).body("Work submission created for student with ID: " + idStudent);
    }

    /**
     * GET /api/timeslots/{idStudent}/{idTimeSlot}
     * Get a work submission for a student and a time slot
     */
    @Operation(
            summary = "Get a work submission for a student and a time slot",
            description = "Retrieve the work submission for a given student and time slot"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Work submission retrieved"),
            @ApiResponse(responseCode = "404", description = "Student or time slot not found")
    })
    @GetMapping("/{idStudent}/{idTimeSlot}")
    public ResponseEntity<WorkSubmissionDto> getWorkSubmission(
            @Parameter(description = "Student Id", required = true)
            @PathVariable String idStudent,
            @Parameter(description = "Time slot Id", required = true)
            @PathVariable Long idTimeSlot
    ) {
        WorkSubmissionDto workSubmissionDto = timeSlotService.getWorkSubmission(
                idStudent,
                idTimeSlot
        );
        return ResponseEntity.ok(workSubmissionDto);
    }

    /**
     * PUT /api/timeslots/{idStudent}/{idTimeSlot}/grade
     * Add a work submission for a student and a time slot
     */
    @Operation(
            summary = "Set a grade (ZERO or ONE) for a work submission for a time slot for a student",
            description = "Set a grade (ZERO or ONE) for a work submission for a time slot for a student"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Grade set"),
            @ApiResponse(responseCode = "400", description = "Data not valid"),
            @ApiResponse(responseCode = "404", description = "Student or time slot not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PutMapping("/{idStudent}/{idTimeSlot}/grade")
    public ResponseEntity<String> setGradeWorkSubmission(
            @Parameter(description = "Student Id", required = true)
            @PathVariable String idStudent,
            @Parameter(description = "Time slot Id", required = true)
            @PathVariable Long idTimeSlot,
            @RequestBody WorkSubmissionGradeRequest request
    ) {
        timeSlotService.setGradeWorkSubmission(
                idStudent,
                idTimeSlot,
                request
        );
        return ResponseEntity.status(201).body("Grade set for student with ID: " + idStudent);
    }

    // ============ ICS IMPORT ENDPOINTS ============

    /**
     * Import ICS events from an automatically constructed URL using course ID.
     * The URL is constructed as: baseUrl + courseId
     */
    @Operation(
            summary = "Import time slots from original schedule using course ID",
            description = "Download ICS file from URL constructed with course ID and import events as time slots."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time slots imported successfully"),
            @ApiResponse(responseCode = "400", description = "Download failed"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/{courseId}/import-ics-auto")
    public ResponseEntity<String> importIcsWithCourseId(
            @Parameter(description = "Course ID to construct the ICS URL", required = true)
            @PathVariable Integer courseId) {
        try {
            timeSlotService.importIcsByCourseId(courseId);
            return ResponseEntity.ok("ICS file imported successfully for course " + courseId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to download ICS file: " + e.getMessage());
        }
    }

    /**
     * Import ICS events from a complete URL provided by the user.
     * Useful if automatic URL construction doesn't work.
     */
    @Operation(
            summary = "Import time slots from ICS file using complete URL",
            description = "Download ICS file from the complete URL provided and import events as time slots. " +
                    "Use this endpoint if automated download fails or you need more control."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Time slots imported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid URL or download failed"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/{courseId}/import-ics-url")
    public ResponseEntity<String> importIcsFromUrl(
            @Parameter(description = "Course ID to associate imported events with", required = true)
            @PathVariable Integer courseId,
            @Parameter(description = "Complete URL to the ICS file", required = true)
            @RequestParam String icsUrl) {
        try {
            timeSlotService.importIcsFromUrl(icsUrl, courseId);
            return ResponseEntity.ok("ICS file imported successfully from URL for course " + courseId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to download ICS file from URL: " + e.getMessage());
        }
    }

    /**
     * Import ICS events from a directly uploaded file.
     * User selects and uploads the ICS file directly.
     */
    @Operation(
            summary = "Import time slots from uploaded ICS file",
            description = "Upload an ICS file directly and import events as time slots. " +
                    "Use this endpoint if automated download fails or you prefer manual file upload."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ICS file uploaded and imported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format or processing failed"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/{courseId}/import-ics-upload")
    public ResponseEntity<String> importIcsFromUpload(
            @Parameter(description = "Course ID to associate imported events with", required = true)
            @PathVariable Integer courseId,
            @Parameter(description = "ICS file to upload", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Uploaded file is empty");
            }

            String icsContent = new String(file.getBytes());
            timeSlotService.importIcsFromFileUpload(icsContent, courseId);
            return ResponseEntity.ok("ICS file uploaded and imported successfully for course " + courseId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to process uploaded ICS file: " + e.getMessage());
        }
    }

    /**
     * Import time slots from a directly uploaded Excel file.
     * User selects and uploads the Excel file directly.
     */
    @Operation(
            summary = "Import time slots from uploaded Excel file",
            description = "Upload an Excel file directly and import time slots."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Excel file uploaded and imported successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid file format or processing failed"),
            @ApiResponse(responseCode = "404", description = "Course not found"),
            @ApiResponse(responseCode = "500", description = "Server error")
    })
    @PostMapping("/{courseId}/import-excel-upload")
    public ResponseEntity<String> importExcelFromUpload(
            @Parameter(description = "Course ID to associate imported time slots with", required = true)
            @PathVariable Integer courseId,
            @Parameter(description = "Excel file to upload", required = true)
            @RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Uploaded file is empty");
            }

            timeSlotService.importTimeSlotsFromExcelUpload(file, courseId);
            return ResponseEntity.status(200).body("Excel file uploaded and imported successfully for course " + courseId);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Failed to process uploaded Excel file: " + e.getMessage());
        }
    }

}
