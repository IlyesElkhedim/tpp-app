package back.projet.tpp.repository;

import back.projet.tpp.domain.model.entity.CourseStudent;
import back.projet.tpp.domain.model.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CourseStudentRepository extends JpaRepository<CourseStudent, Long> {
    // Retrieve all students of a course by course ID (JPQL)
    @Query("SELECT cs.student FROM CourseStudent cs WHERE cs.course.id = :courseId")
    List<Student> findStudentsByCourseId(@Param("courseId") Long courseId);

    // Alternative: retrieve all CourseStudent for a course
    List<CourseStudent> findByCourseId(Integer courseId);

    // Retrieve all CourseStudent entries for a student via student.studentNumber
    List<CourseStudent> findByStudentStudentNumber(String studentNumber);

    // Find a specific CourseStudent by courseId and studentNumber
    Optional<CourseStudent> findByCourseIdAndStudentStudentNumber(Integer courseId, String studentNumber);
}
