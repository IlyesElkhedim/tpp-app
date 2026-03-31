package back.projet.tpp.repository;

import back.projet.tpp.domain.model.entity.Student;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@DisplayName("StudentRepository - Tests d'intégration avec la base de données")
class StudentRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private StudentRepository studentRepository;

    @Test
    @DisplayName("findAll devrait retourner tous les étudiants")
    void findAll_ShouldReturnAllStudents() {
        // Given
        Student student1 = new Student();
        student1.setFirstName("Jean");
        student1.setLastName("Dupont");
        student1.setEmail("jean.dupont@example.com");
        student1.setStudentNumber("12345");
        student1.setHashPassword("$2a$10$hashedpassword");

        Student student2 = new Student();
        student2.setFirstName("Marie");
        student2.setLastName("Martin");
        student2.setEmail("marie.martin@example.com");
        student2.setStudentNumber("67890");
        student2.setHashPassword("$2a$10$hashedpassword");

        entityManager.persist(student1);
        entityManager.persist(student2);
        entityManager.flush();

        // When
        List<Student> students = studentRepository.findAll();

        // Then
        assertThat(students).hasSize(2);
        assertThat(students).extracting(Student::getFirstName)
                .containsExactlyInAnyOrder("Jean", "Marie");
    }

    @Test
    @DisplayName("findById devrait retourner un étudiant par son ID")
    void findById_ShouldReturnStudent_WhenExists() {
        // Given
        Student student = new Student();
        student.setFirstName("Jean");
        student.setLastName("Dupont");
        student.setEmail("jean.dupont@example.com");
        student.setStudentNumber("12345");
        student.setHashPassword("$2a$10$hashedpassword");

        Student saved = entityManager.persist(student);
        entityManager.flush();

        // When
        Optional<Student> found = studentRepository.findById(saved.getStudentNumber());

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getFirstName()).isEqualTo("Jean");
        assertThat(found.get().getEmail()).isEqualTo("jean.dupont@example.com");
    }

    @Test
    @DisplayName("findById devrait retourner empty si l'étudiant n'existe pas")
    void findById_ShouldReturnEmpty_WhenNotExists() {
        // When
        Optional<Student> found = studentRepository.findById("999");

        // Then
        assertThat(found).isEmpty();
    }

    @Test
    @DisplayName("save devrait persister un nouvel étudiant")
    void save_ShouldPersistNewStudent() {
        // Given
        Student student = new Student();
        student.setFirstName("Jean");
        student.setLastName("Dupont");
        student.setEmail("jean.dupont@example.com");
        student.setStudentNumber("12345");
        student.setHashPassword("$2a$10$hashedpassword");

        // When
        Student saved = studentRepository.save(student);
        entityManager.flush();

        // Then
        assertThat(saved.getStudentNumber()).isNotNull();
        assertThat(saved.getFirstName()).isEqualTo("Jean");

        Student found = entityManager.find(Student.class, saved.getStudentNumber());
        assertThat(found).isNotNull();
        assertThat(found.getEmail()).isEqualTo("jean.dupont@example.com");
    }

    @Test
    @DisplayName("save devrait mettre à jour un étudiant existant")
    void save_ShouldUpdateExistingStudent() {
        // Given
        Student student = new Student();
        student.setFirstName("Jean");
        student.setLastName("Dupont");
        student.setEmail("jean.dupont@example.com");
        student.setStudentNumber("12345");
        student.setHashPassword("$2a$10$hashedpassword");

        Student saved = entityManager.persist(student);
        entityManager.flush();

        // When
        saved.setEmail("jean.dupont.new@example.com");
        studentRepository.save(saved);
        entityManager.flush();
        entityManager.clear();

        // Then
        Student updated = entityManager.find(Student.class, saved.getStudentNumber());
        assertThat(updated.getEmail()).isEqualTo("jean.dupont.new@example.com");
    }

    @Test
    @DisplayName("deleteById devrait supprimer un étudiant")
    void deleteById_ShouldDeleteStudent() {
        // Given
        Student student = new Student();
        student.setFirstName("Jean");
        student.setLastName("Dupont");
        student.setEmail("jean.dupont@example.com");
        student.setStudentNumber("10012345");
        student.setHashPassword("$2a$10$hashedpassword");

        Student saved = entityManager.persist(student);
        entityManager.flush();
        String studentNumber = saved.getStudentNumber();

        // When
        studentRepository.deleteById(studentNumber);
        entityManager.flush();

        // Then
        Student found = entityManager.find(Student.class, studentNumber);
        assertThat(found).isNull();
    }
}

