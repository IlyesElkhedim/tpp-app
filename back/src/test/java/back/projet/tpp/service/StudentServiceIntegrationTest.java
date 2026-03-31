package back.projet.tpp.service;

import back.projet.tpp.domain.model.entity.Course;
import back.projet.tpp.domain.model.enums.CourseLevel;
import back.projet.tpp.domain.model.enums.CourseName;
import back.projet.tpp.repository.CourseRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(properties = {
        "spring.sql.init.mode=never",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
@DisplayName("StudentService - Integration tests")
class StudentServiceIntegrationTest {

    @Autowired
    private StudentService studentService;

    @Autowired
    private CourseRepository courseRepository;

    @BeforeEach
    void setUp() {
        // Create a course for the context (kept for compatibility)
        Course course = new Course();
        course.setId(1);
        course.setName(CourseName.TIW);
        course.setYears("2025");
        course.setLevel(CourseLevel.M1);
        courseRepository.save(course);
    }

    @Test
    @DisplayName("getAllStudents should return empty list when none present")
    void getAllStudents_ShouldReturnEmpty_WhenNoStudents() {
        List<?> all = studentService.getAllStudents();
        assertThat(all).isEmpty();
    }
}
