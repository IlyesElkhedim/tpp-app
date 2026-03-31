package back.projet.tpp.repository;

import back.projet.tpp.domain.model.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
