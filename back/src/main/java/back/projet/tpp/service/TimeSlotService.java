package back.projet.tpp.service;

import back.projet.tpp.domain.model.entity.Course;
import back.projet.tpp.domain.model.entity.Student;
import back.projet.tpp.domain.model.entity.StudentAtTimeSlot;
import back.projet.tpp.domain.model.entity.StudentAtTimeSlotId;
import back.projet.tpp.domain.model.entity.WorkDescription;
import back.projet.tpp.domain.model.entity.WorkSubmission;
import back.projet.tpp.domain.model.enums.AttendanceStatus;
import back.projet.tpp.dto.WorkDescriptionDto;
import back.projet.tpp.dto.WorkSubmissionDto;
import back.projet.tpp.dto.request.MarkAttendanceRequest;
import back.projet.tpp.dto.request.TimeSlotDto;
import back.projet.tpp.dto.request.WorkSubmissionGradeRequest;
import back.projet.tpp.dto.response.StudentAttendanceDto;
import back.projet.tpp.domain.model.entity.TimeSlot;
import back.projet.tpp.repository.CourseRepository;
import back.projet.tpp.repository.CourseStudentRepository;
import back.projet.tpp.repository.StudentAtTimeSlotRepository;
import back.projet.tpp.repository.TimeSlotRepository;
import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import static back.projet.tpp.domain.model.enums.WorkSubmissionStatus.INVALID;
import static back.projet.tpp.domain.model.enums.WorkSubmissionStatus.VALID;

@Service
@Transactional
public class TimeSlotService {

    private final TimeSlotRepository timeSlotRepository;
    private final StudentAtTimeSlotRepository studentAtTimeSlotRepository;
    private final CourseRepository courseRepository;
    private final CourseStudentRepository courseStudentRepository;
    private final RestTemplate restTemplate;
    private final ExcelTimeSlotImportService excelTimeSlotImportService;
    private final IcsTimeSlotImportService icsTimeSlotImportService;

    public TimeSlotService(TimeSlotRepository timeSlotRepository,
                           StudentAtTimeSlotRepository studentAtTimeSlotRepository,
                           CourseRepository courseRepository,
                           CourseStudentRepository courseStudentRepository,
                           RestTemplate restTemplate,
                           ExcelTimeSlotImportService excelTimeSlotImportService, IcsTimeSlotImportService icsTimeSlotImportService) {
        this.timeSlotRepository = timeSlotRepository;
        this.studentAtTimeSlotRepository = studentAtTimeSlotRepository;
        this.courseRepository = courseRepository;
        this.courseStudentRepository = courseStudentRepository;
        this.restTemplate = restTemplate;
        this.excelTimeSlotImportService = excelTimeSlotImportService;
        this.icsTimeSlotImportService = icsTimeSlotImportService;
    }

    /**
     * Creates a new time slot and automatically registers all students from the associated course.
     * @param request TimeSlotDto containing all time slot details
     * @return TimeSlotDto of the created time slot with its generated ID
     * @throws EntityNotFoundException if the course is not found
     * @throws IllegalArgumentException if the provided times are invalid
     * @throws IllegalStateException if the time slot is not associated with a course (from registerAllStudentsFromCourse)
     */
    public TimeSlotDto create(TimeSlotDto request) throws EntityNotFoundException, IllegalArgumentException, IllegalStateException {
        validateTimes(request.startTime(), request.endTime(),
                request.submissionStartTime(), request.submissionEndTime());

        Course course = courseRepository.findById(Long.valueOf(request.courseId()))
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + request.courseId()));

        TimeSlot timeSlot = TimeSlotDto.toEntity(request, course);
        TimeSlot savedTimeSlot = timeSlotRepository.save(timeSlot);

        registerAllStudentsFromCourse(savedTimeSlot.getId());

        return TimeSlotDto.fromEntity(savedTimeSlot);
    }

    /**
     * Retrieves all time slots for a course within a specified date range.
     * @param courseId The ID of the course
     * @param startDate The start date (inclusive)
     * @param endDate The end date (inclusive)
     * @return List of TimeSlotDto objects for the course within the date range
     * @throws IllegalArgumentException if startDate is after endDate
     */
    @Transactional(readOnly = true)
    public List<TimeSlotDto> findBetweenDates(Integer courseId ,LocalDate startDate, LocalDate endDate) throws IllegalArgumentException {

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must be before endDate");
        }

        return timeSlotRepository.findByCourseIdAndDateBetween(courseId, startDate, endDate).stream().map(TimeSlotDto::fromEntity).toList();
    }

    /**
     * Deletes a time slot and all associated student attendance records.
     * @param id The ID of the time slot to delete
     * @throws EntityNotFoundException if the time slot does not exist
     */
    public void deleteById(Long id) throws EntityNotFoundException {

        if (!timeSlotRepository.existsById(id)) {
            throw new EntityNotFoundException("TimeSlot not found with id " + id);
        }

        // Delete all student attendance entries linked to this time slot to avoid FK constraint violations
        studentAtTimeSlotRepository.deleteByIdTimeSlotId(id);

        timeSlotRepository.deleteById(id);
    }

    /**
     * Updates an existing time slot with new information.
     * @param id The ID of the time slot to update
     * @param request TimeSlotDto containing the updated time slot details
     * @return TimeSlotDto of the updated time slot
     * @throws EntityNotFoundException if the time slot or course is not found
     * @throws IllegalArgumentException if the provided times are invalid
     */
    public TimeSlotDto update(Long id, TimeSlotDto request) throws EntityNotFoundException, IllegalArgumentException {

        TimeSlot existing = timeSlotRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("TimeSlot not found with id " + id));

        validateTimes(
                request.startTime(),
                request.endTime(),
                request.submissionStartTime(),
                request.submissionEndTime()
        );

        Course course = courseRepository.findById(request.courseId().longValue())
                .orElseThrow(() ->
                        new EntityNotFoundException(
                                "Course not found with ID: " + request.courseId()
                        ));

        TimeSlotDto.updateFieldsEntity(existing, request);
        existing.setCourse(course);

        return TimeSlotDto.fromEntity(timeSlotRepository.save(existing));
    }


    /**
     * Validates that start times are before end times for both the time slot and submission windows.
     * @param startTime The start time of the time slot
     * @param endTime The end time of the time slot
     * @param submissionStartTime The start time of the submission window
     * @param submissionEndTime The end time of the submission window
     * @throws IllegalArgumentException if any time validation fails
     */
    private void validateTimes(
            LocalTime startTime,
            LocalTime endTime,
            LocalTime submissionStartTime,
            LocalTime submissionEndTime) throws IllegalArgumentException {

        if (startTime.isAfter(endTime)) {
            throw new IllegalArgumentException("startTime must be before endTime");
        }

        if (submissionStartTime.isAfter(submissionEndTime)) {
            throw new IllegalArgumentException("submissionStartTime must be before submissionEndTime");
        }
    }

    /**
     * Retrieve attendance status of a student for a time slot.
     * @param timeSlotId ID of the time slot
     * @param studentId Student number
     * @return AttendanceStatus (PRESENT, UNJUSTIFIED, JUSTIFIED)
     * @throws EntityNotFoundException if the student is not registered to the time slot
     */
    public AttendanceStatus getStudentAttendance(Long timeSlotId, String studentId) throws EntityNotFoundException {
        StudentAtTimeSlotId id = new StudentAtTimeSlotId(studentId, timeSlotId);
        return studentAtTimeSlotRepository.findById(id)
                .map(StudentAtTimeSlot::getAttendance)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student is not registered to the time slot: (studentId=" + studentId + ", timeSlotId=" + timeSlotId + ")"
                ));
    }

    /**
     * Automatically register ALL students from a course to a time slot.
     * Called automatically when a new time slot is created.
     * @param timeSlotId ID of the time slot
     * @throws EntityNotFoundException if the time slot does not exist or if the course has no students
     * @throws IllegalStateException if the time slot is not associated with a course
     */
    private void registerAllStudentsFromCourse(Long timeSlotId) throws EntityNotFoundException, IllegalStateException {

        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new EntityNotFoundException("Time slot not found with ID: " + timeSlotId));

        Course course = timeSlot.getCourse();
        if (course == null) {
            throw new IllegalStateException("The time slot is not associated with any course");
        }

        List<Student> students = courseStudentRepository.findStudentsByCourseId(Long.valueOf(course.getId()));

        if (students.isEmpty()) {
            return;
        }

        List<String> studentIds = students.stream().map(Student::getStudentNumber).toList();
        List<StudentAtTimeSlotId> ids = studentIds.stream()
                .map(studentId -> new StudentAtTimeSlotId(studentId, timeSlotId))
                .collect(Collectors.toList());

        Iterable<StudentAtTimeSlot> existing = studentAtTimeSlotRepository.findAllById(ids);
        Set<String> alreadyRegisteredIds = new HashSet<>();
        existing.forEach(sats -> alreadyRegisteredIds.add(sats.getId().getStudentId()));

        List<Student> studentsToRegister = students.stream()
                .filter(student -> !alreadyRegisteredIds.contains(student.getStudentNumber()))
                .toList();

        if (studentsToRegister.isEmpty()) {
            return;
        }

        List<StudentAtTimeSlot> entries = studentsToRegister.stream()
                .map(student -> {
                    StudentAtTimeSlot sats = new StudentAtTimeSlot();
                    StudentAtTimeSlotId id = new StudentAtTimeSlotId(student.getStudentNumber(), timeSlotId);
                    sats.setId(id);
                    sats.setStudent(student);
                    sats.setTimeSlot(timeSlot);
                    sats.setAttendance(AttendanceStatus.PRESENT); // By default, present
                    return sats;
                })
                .collect(Collectors.toList());

        studentAtTimeSlotRepository.saveAll(entries);
    }


    /**
     * Update attendance in batch for a list (studentId, attendanceStatus) and given timeSlotId.
     * Single read then saveAll to minimize queries.
     * @param timeSlotId ID of the time slot
     * @param requests List of requests containing studentId and the new attendance status
     * @throws EntityNotFoundException if any requested student is not registered to the time slot
     */
    public void updateAttendance(Long timeSlotId, List<MarkAttendanceRequest> requests) throws EntityNotFoundException {
        if (requests == null || requests.isEmpty()) {
            return;
        }

        List<StudentAtTimeSlotId> ids = requests.stream()
                .map(r -> new StudentAtTimeSlotId(r.studentId(), timeSlotId))
                .collect(Collectors.toList());

        Iterable<StudentAtTimeSlot> foundIterable = studentAtTimeSlotRepository.findAllById(ids);
        List<StudentAtTimeSlot> found = new ArrayList<>();
        foundIterable.forEach(found::add);

        Set<StudentAtTimeSlotId> foundIds = found.stream()
                .map(StudentAtTimeSlot::getId)
                .collect(Collectors.toSet());

        Set<StudentAtTimeSlotId> requestedIds = new HashSet<>(ids);
        requestedIds.removeAll(foundIds);

        if (!requestedIds.isEmpty()) {

            String missingIds = requestedIds.stream()
                    .map(id -> String.format("(studentId=%s, timeSlotId=%d)",
                            id.getStudentId(), id.getTimeSlotId()))
                    .collect(Collectors.joining(", "));
            throw new EntityNotFoundException(
                    "Some students are not registered to this time slot: " + missingIds);
        }

        Map<String, AttendanceStatus> attendanceStatusMap = requests.stream()
                .collect(Collectors.toMap(
                        MarkAttendanceRequest::studentId,
                        MarkAttendanceRequest::attendanceStatus
                ));

        found.forEach(s -> s.setAttendance(attendanceStatusMap.get(s.getId().getStudentId())));

        studentAtTimeSlotRepository.saveAll(found);
    }

    /**
     * Retrieve attendance of all students registered to a time slot.
     * Returns a list of StudentAttendanceDto containing student info and attendance status.
     * Uses a single query for performance optimization.
     * @param timeSlotId ID of the time slot
     * @return List of attendances with student information
     */
    public List<StudentAttendanceDto> getRegisteredStudentAttendance(Long timeSlotId) {

        List<StudentAtTimeSlot> registeredStudents = studentAtTimeSlotRepository.findByIdTimeSlotId(timeSlotId);

        return registeredStudents.stream()
                .map(StudentAttendanceDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Register a student to all existing time slots of a course.
     * To be called when a student is enrolled in a course.
     * @param studentId Student number (String)
     * @param courseId Course ID
     * @throws EntityNotFoundException if the student is not found in the course
     */
    public void registerStudentToAllCourseTimeSlots(String studentId, Integer courseId) throws EntityNotFoundException {

        List<TimeSlot> timeSlots = timeSlotRepository.findByCourseId(courseId);

        if (timeSlots.isEmpty()) {
            return;
        }

        Student student = courseStudentRepository.findStudentsByCourseId(Long.valueOf(courseId)).stream()
                .filter(s -> s.getStudentNumber().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Student not found with ID: " + studentId));

        List<StudentAtTimeSlotId> ids = timeSlots.stream()
                .map(ts -> new StudentAtTimeSlotId(studentId, ts.getId()))
                .collect(Collectors.toList());

        Iterable<StudentAtTimeSlot> existing = studentAtTimeSlotRepository.findAllById(ids);
        Set<Long> alreadyRegisteredTimeSlotIds = new HashSet<>();
        existing.forEach(sats -> alreadyRegisteredTimeSlotIds.add(sats.getId().getTimeSlotId()));

        List<TimeSlot> timeSlotsToRegister = timeSlots.stream()
                .filter(ts -> !alreadyRegisteredTimeSlotIds.contains(ts.getId()))
                .toList();

        if (timeSlotsToRegister.isEmpty()) {
            return;
        }

        List<StudentAtTimeSlot> entries = timeSlotsToRegister.stream()
                .map(timeSlot -> {
                    StudentAtTimeSlot sats = new StudentAtTimeSlot();
                    StudentAtTimeSlotId id = new StudentAtTimeSlotId(studentId, timeSlot.getId());
                    sats.setId(id);
                    sats.setStudent(student);
                    sats.setTimeSlot(timeSlot);
                    sats.setAttendance(AttendanceStatus.PRESENT); // Default: present
                    return sats;
                })
                .collect(Collectors.toList());

        studentAtTimeSlotRepository.saveAll(entries);
    }

    /**
     * Retrieves a time slot by its ID.
     * @param timeslotId The ID of the time slot
     * @return TimeSlotDto containing the time slot details
     * @throws EntityNotFoundException if the time slot is not found
     */
    public TimeSlotDto getById(Long timeslotId) throws EntityNotFoundException {
        TimeSlot timeSlot = timeSlotRepository.findById(timeslotId)
                .orElseThrow(() -> new EntityNotFoundException("TimeSlot not found with id " + timeslotId));
        return TimeSlotDto.fromEntity(timeSlot);
    }

    /**
     * Creates a work submission for a student at a specific time slot.
     * Validates the submission time and marks it as valid or invalid accordingly.
     * @param idStudent The student ID
     * @param idTimeSlot The ID of the time slot
     * @param works List of WorkDescriptionDto objects describing the submitted work
     * @throws ResponseStatusException (NOT_FOUND) if student or time slot is not found
     */
    @Transactional
    public void createWorkSubmission(String idStudent, Long idTimeSlot, List<WorkDescriptionDto> works) throws ResponseStatusException {

        StudentAtTimeSlotId id = new StudentAtTimeSlotId(idStudent, idTimeSlot);
        StudentAtTimeSlot studentAtTimeSlot = studentAtTimeSlotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student or time slot not found"
                ));

        if (studentAtTimeSlot.getWorkSubmission() != null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "WorkSubmission already exist"
            );
        }

        List<WorkDescription> workDescriptionList = new ArrayList<>(List.of());
        WorkSubmission workSubmission = new WorkSubmission();
        workSubmission.setId(id);
        workSubmission.setStudentAtTimeSlot(studentAtTimeSlot);

        for (WorkDescriptionDto work : works) {
            workDescriptionList.add(WorkDescriptionDto.toEntity(work, workSubmission));
        }

        workSubmission.setWorkDescriptions(workDescriptionList);
        studentAtTimeSlot.setWorkSubmission(workSubmission);

        TimeSlotDto timeSlot = this.getById(idTimeSlot);
        studentAtTimeSlot.setWorkSubmissionStatus(VALID);
        if (LocalTime.now().isBefore(timeSlot.submissionStartTime()) || LocalTime.now().isAfter(timeSlot.submissionEndTime())) {
            studentAtTimeSlot.setWorkSubmissionStatus(INVALID);
        }

        studentAtTimeSlotRepository.save(studentAtTimeSlot);
    }

    /**
     * Retrieves the work submission for a student at a specific time slot.
     * @param idStudent The student ID
     * @param idTimeSlot The ID of the time slot
     * @return WorkSubmissionDto containing the submitted work details
     * @throws ResponseStatusException (NOT_FOUND) if student or time slot is not found or if no work submission exists for this student-timeSlot pair
     */
    public WorkSubmissionDto getWorkSubmission(String idStudent, Long idTimeSlot) throws ResponseStatusException {

        StudentAtTimeSlotId id = new StudentAtTimeSlotId(idStudent, idTimeSlot);
        StudentAtTimeSlot studentAtTimeSlot = studentAtTimeSlotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student or time slot not found"
                ));

        if (studentAtTimeSlot.getWorkSubmission() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "WorkSubmission does not exist"
            );
        }

        return WorkSubmissionDto.fromEntity(studentAtTimeSlot.getWorkSubmission());
    }

    /**
     * Sets or updates the grade for a student's work submission.
     * @param idStudent The student ID
     * @param idTimeSlot The ID of the time slot
     * @param grade WorkSubmissionGradeRequest containing the grade value
     * @throws ResponseStatusException (NOT_FOUND) if student or time slot is not found or if no work submission exists for this student-timeSlot pair
     */
    public void setGradeWorkSubmission(String idStudent, Long idTimeSlot, WorkSubmissionGradeRequest grade) throws ResponseStatusException {

        StudentAtTimeSlotId id = new StudentAtTimeSlotId(idStudent, idTimeSlot);
        StudentAtTimeSlot studentAtTimeSlot = studentAtTimeSlotRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Student or time slot not found"
                ));

        if (studentAtTimeSlot.getWorkSubmission() == null) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "WorkSubmission does not exist"
            );
        }

        studentAtTimeSlot.setWorkSubmissionGrade(grade.grade());

        studentAtTimeSlotRepository.save(studentAtTimeSlot);
    }

    /**
     * Imports ICS calendar events from an auto-constructed URL based on the course ID.
     * Uses the university's course planning system to fetch the calendar.
     * @param courseId The ID of the course to fetch calendar for
     * @throws IOException if URL download fails
     * @throws ResponseStatusException (NOT_FOUND) if the course is not found
     */
    public void importIcsByCourseId(Integer courseId) throws IOException, ResponseStatusException {
        Course course = courseRepository.findById(courseId.longValue())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Course not found with ID: " + courseId
                ));

        String[] years = course.getYears().split("-");

        String icsUrl = "https://edt.univ-lyon1.fr/jsp/custom/modules/plannings/anonymous_cal.jsp?resources=" +
                courseId + "&projectId=1&calType=ical&firstDate=" +
                years[0] + "-09-01&lastDate=" +
                years[1] + "-08-31";

        importIcsFromUrl(icsUrl, courseId);
    }

    /**
     * Imports ICS calendar events from a user-provided URL.
     * Fetches the ICS file from the URL and creates time slots from the events.
     * @param icsUrl The complete URL to the ICS file
     * @param courseId The ID of the course to associate imported events with
     * @throws IOException if URL download or processing fails
     * @throws ResponseStatusException (NOT_FOUND) if the course is not found
     */
    public void importIcsFromUrl(String icsUrl, Integer courseId) throws IOException, ResponseStatusException {
        if (!courseRepository.existsById(courseId.longValue())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId);
        }

        String icsContent = restTemplate.getForObject(icsUrl, String.class);

        List<TimeSlotDto> timeSlotDtos = icsTimeSlotImportService.extractTimeSlots(icsContent, courseId);

        for (TimeSlotDto timeSlotDto : timeSlotDtos) {
            persistTimeSlotFromFile(timeSlotDto);
        }
    }

    /**
     * Imports ICS calendar events from uploaded file content.
     * Parses the ICS content and creates time slots from the events.
     * @param icsContent The raw ICS file content as a string
     * @param courseId The ID of the course to associate imported events with
     * @throws IOException if processing fails
     * @throws ResponseStatusException (NOT_FOUND) if the course is not found
     */
    public void importIcsFromFileUpload(String icsContent, Integer courseId) throws IOException, ResponseStatusException {
        if (!courseRepository.existsById(courseId.longValue())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Course not found with ID: " + courseId);
        }

        List<TimeSlotDto> timeSlotDtos = icsTimeSlotImportService.extractTimeSlots(icsContent, courseId);

        for (TimeSlotDto timeSlotDto : timeSlotDtos) {
            persistTimeSlotFromFile(timeSlotDto);
        }
    }

    /**
     * Imports time slots from an uploaded Excel file.
     * Parses the Excel content and creates time slots from the extracted data.
     * @param file The uploaded Excel file
     * @param courseId The ID of the course to associate imported time slots with
     * @throws IOException if file reading or processing fails
     * @throws IllegalArgumentException if date or time format is invalid in the Excel file
     * @throws RuntimeException if required columns (Dates/Horaires) are not found
     */
    public void importTimeSlotsFromExcelUpload(MultipartFile file, Integer courseId) throws IOException, IllegalArgumentException, RuntimeException {
        List<TimeSlotDto> timeSlotDtos = excelTimeSlotImportService.extractTimeSlots(file, courseId);

        for (TimeSlotDto timeSlotDto : timeSlotDtos) {
            persistTimeSlotFromFile(timeSlotDto);
        }
    }

    /**
     * Persists a time slot from an import operation, checking for duplicates.
     * Only creates the time slot if one with the same course, date, and times does not already exist.
     * @param timeSlotDto The TimeSlotDto to persist
     * @throws RuntimeException if persistence fails after all checks
     */
    private void persistTimeSlotFromFile(TimeSlotDto timeSlotDto) throws RuntimeException {
        try {
            // Check if time slot already exists or conflict with another one
            List<TimeSlot> existingTimeSlot = timeSlotRepository.findByCourseIdAndDateAndTimes(
                    timeSlotDto.courseId(),
                    timeSlotDto.date(),
                    timeSlotDto.startTime(),
                    timeSlotDto.endTime()
            ).stream().toList();

            if (existingTimeSlot.isEmpty()) {
                create(timeSlotDto);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to persist time slot");
        }
    }
}
