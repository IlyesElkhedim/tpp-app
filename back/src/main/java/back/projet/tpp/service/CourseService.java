package back.projet.tpp.service;

import back.projet.tpp.domain.model.entity.*;
import back.projet.tpp.dto.CourseDto;
import back.projet.tpp.dto.StudentDto;
import back.projet.tpp.mapper.StudentMapper;
import back.projet.tpp.repository.CourseRepository;
import back.projet.tpp.repository.CourseStudentRepository;
import back.projet.tpp.repository.StudentAtTimeSlotRepository;
import back.projet.tpp.repository.StudentRepository;
import back.projet.tpp.repository.TimeSlotRepository;
import back.projet.tpp.repository.SupervisorRepository;
import back.projet.tpp.repository.ContractTypeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CourseService {

    private final CourseStudentRepository courseStudentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final StudentAtTimeSlotRepository studentAtTimeSlotRepository;
    private final SupervisorRepository supervisorRepository;
    private final ContractTypeRepository contractTypeRepository;
    private final StudentMapper studentMapper;
    private final TimeSlotService timeSlotService;

    @Autowired
    public CourseService(
            CourseStudentRepository courseStudentRepository,
            StudentRepository studentRepository,
            CourseRepository courseRepository,
            TimeSlotRepository timeSlotRepository,
            StudentAtTimeSlotRepository studentAtTimeSlotRepository,
            SupervisorRepository supervisorRepository,
            ContractTypeRepository contractTypeRepository,
            StudentMapper studentMapper, TimeSlotService timeSlotService) {
        this.courseStudentRepository = courseStudentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.studentAtTimeSlotRepository = studentAtTimeSlotRepository;
        this.supervisorRepository = supervisorRepository;
        this.contractTypeRepository = contractTypeRepository;
        this.studentMapper = studentMapper;
        this.timeSlotService = timeSlotService;
    }

    /**
     * Retrieve all students of a course by course ID
     */
    public List<StudentDto> getStudentsByCourseId(Long courseId) {
        List<Student> students = courseStudentRepository.findStudentsByCourseId(courseId);
        return students.stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Create a new course
     */
    public CourseDto createCourse(CourseDto courseDto) {
        Course course = CourseDto.toEntity(courseDto);

        if (courseDto.supervisorId() != null) {
            Supervisor supervisor = supervisorRepository.findById(courseDto.supervisorId())
                    .orElseThrow(() -> new EntityNotFoundException("Supervisor not found with ID: " + courseDto.supervisorId()));
            course.setSupervisor(supervisor);
        }
        Course saved = courseRepository.save(course);
        return CourseDto.fromEntity(saved);
    }

    public List<StudentDto> createStudentsForCourse(Long courseId, List<StudentDto> studentsDto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

        List<Student> students = studentsDto.stream()
                .map(StudentDto::toEntity)
                .collect(Collectors.toList());
        List<Student> saved = studentRepository.saveAll(students);

        // Set currentCourse for each saved student to this course (persist as their active course)
        for (Student s : saved) {
            s.setCurrentCourse(course);
        }
        studentRepository.saveAll(saved);

        List<CourseStudent> courseStudents = new java.util.ArrayList<>();
        for (Student student : saved) {
            CourseStudent courseStudent = getCourseStudent(student, course);
            courseStudents.add(courseStudent);
        }

        courseStudentRepository.saveAll(courseStudents);

        for (Student student : saved) {
            timeSlotService.registerStudentToAllCourseTimeSlots(student.getStudentNumber(), course.getId());
        }
        return saved.stream().map(studentMapper::toDto).collect(Collectors.toList());
    }


    private @NonNull CourseStudent getCourseStudent(Student student, Course course) {
        CourseStudent courseStudent = new CourseStudent();
        CourseStudentId id = new CourseStudentId(
                student.getStudentNumber(),
                course.getId()
        );
        courseStudent.setId(id);
        courseStudent.setStudent(student);
        courseStudent.setCourse(course);

        // Set default ContractType to "Apprentissage"
        ContractType defaultContractType = contractTypeRepository.findByLabel("Apprentissage")
                .orElseThrow(() -> new EntityNotFoundException("Default ContractType 'Apprentissage' not found"));
        courseStudent.setContractType(defaultContractType);

        // Set default contract start date to today if not provided
        courseStudent.setContractStartDate(java.time.LocalDate.now());

        return courseStudent;
    }

    public List<CourseDto> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(CourseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Delete a course and properly handle its related students and time slots.
     * - Remove StudentAtTimeSlot entries linked to this course's time slots
     * - Delete time slots
     * - For each CourseStudent association of this course: delete the association;
     *   if the related student has no other course enrollments, delete the student;
     *   otherwise, if the student's currentCourse was this course, switch it to another enrolled course.
     */
    public void deleteCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));

        // 1) Remove attendance entries for all timeslots of this course to avoid FK constraints
        List<TimeSlot> timeSlots = timeSlotRepository.findByCourseId(course.getId());

        for (TimeSlot timeSlot : timeSlots) {
            studentAtTimeSlotRepository.deleteByIdTimeSlotId(timeSlot.getId());
        }

        // 2) Delete the timeslots themselves
        timeSlotRepository.deleteAll(timeSlots);

        // 3) Handle course-student associations
        List<CourseStudent> courseStudents = courseStudentRepository.findByCourseId(course.getId());

        for (CourseStudent cs : courseStudents) {
            Student student = cs.getStudent();
            String studentNumber = student.getStudentNumber();

            // Delete the association for this course
            courseStudentRepository.delete(cs);

            // Check if the student has other enrollments
            List<CourseStudent> remaining = courseStudentRepository.findByStudentStudentNumber(studentNumber);

            if (remaining == null || remaining.isEmpty()) {
                // No other enrollments: delete the student
                studentRepository.delete(student);
            } else {
                // Still enrolled in other courses: if currentCourse was this one, update it to another
                if (student.getCurrentCourse() != null && student.getCurrentCourse().getId().equals(course.getId())) {
                    // pick the first remaining course as the new current course
                    Course newCurrent = remaining.getFirst().getCourse();
                    student.setCurrentCourse(newCurrent);
                    studentRepository.save(student);
                }
            }
        }

        // 4) Finally delete the course
        courseRepository.delete(course);
    }

    public CourseDto getCourseById(Long courseId) {
        return courseRepository
                .findById(courseId)
                .map(CourseDto::fromEntity)
                .orElseThrow(() -> new EntityNotFoundException("Course not found with ID: " + courseId));
    }

    /**
     * Update the contract type of a student within a course.
     */
    public void updateStudentContractType(Integer courseId, String studentNumber, String contractTypeLabel) {
        CourseStudent courseStudent = courseStudentRepository
                .findByCourseIdAndStudentStudentNumber(courseId, studentNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Student " + studentNumber + " is not enrolled in course " + courseId));

        ContractType contractType = contractTypeRepository.findByLabel(contractTypeLabel)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Contract type not found: " + contractTypeLabel));

        courseStudent.setContractType(contractType);
        courseStudentRepository.save(courseStudent);
    }
}
