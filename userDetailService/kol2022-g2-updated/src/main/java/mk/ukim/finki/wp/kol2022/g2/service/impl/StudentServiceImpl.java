package mk.ukim.finki.wp.kol2022.g2.service.impl;

import mk.ukim.finki.wp.kol2022.g2.model.Course;
import mk.ukim.finki.wp.kol2022.g2.model.Student;
import mk.ukim.finki.wp.kol2022.g2.model.StudentType;
import mk.ukim.finki.wp.kol2022.g2.model.exceptions.InvalidStudentIdException;
import mk.ukim.finki.wp.kol2022.g2.repository.CourseRepository;
import mk.ukim.finki.wp.kol2022.g2.repository.StudentRepository;
import mk.ukim.finki.wp.kol2022.g2.service.StudentService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class StudentServiceImpl implements StudentService , UserDetailsService {
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;

    public StudentServiceImpl(CourseRepository courseRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public List<Student> listAll() {
        return studentRepository.findAll();
    }

    @Override
    public Student findById(Long id) {
        Student student=studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
        return student;
    }

    @Override
    public Student create(String name, String email, String password, StudentType type, List<Long> courseId, LocalDate enrollmentDate) {
        List<Course> courses=courseRepository.findAllById(courseId);
        Student student=new Student(name,email,passwordEncoder.encode(password),type,courses,enrollmentDate);
        return studentRepository.save(student);
    }

    @Override
    public Student update(Long id, String name, String email, String password, StudentType type, List<Long> coursesId, LocalDate enrollmentDate) {
        List<Course> courses=courseRepository.findAllById(coursesId);
        Student student=studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
        student.setName(name);
        student.setEmail(email);
        student.setPassword(password);
        student.setCourses(courses);
        student.setType(type);
        student.setEnrollmentDate(enrollmentDate);
        return studentRepository.save(student);
    }

    @Override
    public Student delete(Long id) {
        Student student=studentRepository.findById(id).orElseThrow(InvalidStudentIdException::new);
        studentRepository.delete(student);
        return student;
    }

    @Override
    public List<Student> filter(Long courseId, Integer yearsOfStudying) {
        return null;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student s=studentRepository.findByEmail(username);

        return new User(
                s.getEmail(),
                s.getPassword(),
                Stream.of(new SimpleGrantedAuthority(String.format("ROLE_%s",s.getType()))).collect(Collectors.toList())
        );
    }
}
