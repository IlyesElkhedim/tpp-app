package back.projet.tpp.mapper;

import back.projet.tpp.domain.model.entity.CourseStudent;
import back.projet.tpp.domain.model.entity.Student;
import back.projet.tpp.dto.CourseEnrollmentDto;
import back.projet.tpp.dto.StudentDto;
import back.projet.tpp.repository.CourseStudentRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class StudentMapper {

    private final CourseStudentRepository courseStudentRepository;

    public StudentMapper(CourseStudentRepository courseStudentRepository) {
        this.courseStudentRepository = courseStudentRepository;
    }

    /**
     * Convert a Student to StudentDto with courseIds, currentCourse and enrollments
     */
    public StudentDto toDto(Student student) {
        List<CourseStudent> courseStudents = courseStudentRepository.findByStudentStudentNumber(student.getStudentNumber());
        List<Integer> courseIds = courseStudents.stream()
                .map(cs -> cs.getCourse().getId())
                .collect(Collectors.toList());

        List<CourseEnrollmentDto> enrollments = courseStudents.stream()
                .map(cs -> new CourseEnrollmentDto(
                        cs.getCourse().getId(),
                        cs.getContractType() != null ? cs.getContractType().getLabel() : null,
                        cs.getContractStartDate()
                ))
                .collect(Collectors.toList());

        Integer currentCourse = student.getCurrentCourse() != null
                ? student.getCurrentCourse().getId()
                : (courseIds.isEmpty() ? null : courseIds.getFirst());

        return StudentDto.of(student, courseIds, currentCourse, enrollments);
    }
}
