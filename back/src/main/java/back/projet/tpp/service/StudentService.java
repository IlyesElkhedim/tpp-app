package back.projet.tpp.service;

import back.projet.tpp.dto.StudentDto;
import back.projet.tpp.mapper.StudentMapper;
import back.projet.tpp.repository.StudentAtTimeSlotRepository;
import back.projet.tpp.repository.StudentRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class StudentService {
    private final StudentRepository studentRepository;
    private final StudentAtTimeSlotRepository studentAtTimeSlotRepository;
    private final StudentMapper studentMapper;

    @Autowired
    public StudentService(StudentRepository studentRepository,
                          StudentAtTimeSlotRepository studentAtTimeSlotRepository,
                          StudentMapper studentMapper) {
        this.studentRepository = studentRepository;
        this.studentAtTimeSlotRepository = studentAtTimeSlotRepository;
        this.studentMapper = studentMapper;
    }

    public List<StudentDto> getAllStudents() {
        return studentRepository.findAll().stream()
                .map(studentMapper::toDto)
                .collect(Collectors.toList());
    }

    public StudentDto getById(String studentNumber) {
        return studentRepository.findById(studentNumber)
                .map(studentMapper::toDto)
                .orElse(null);
    }

}
