package com.example.demo.Student;

import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@NoArgsConstructor
public class StudentService {

    private StudentRepository studentRepository;

    @Autowired
    public void setStudentRepository(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public List<Student> getStudents(){
        return studentRepository.findAll();
    }

    public void addStudent(Student student) {
        Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
        if (studentOptional.isPresent()){
            throw new IllegalStateException("Email address is taken");
        }
        studentRepository.save(student);
    }

   public void deleteStudent(Long id) {
        if (!studentRepository.existsById(id)){
            throw new IllegalStateException("Student ID " + id + " does not exist.");
        }
        studentRepository.delete(studentRepository.retrieveStudentById(id));
    }

    @Transactional
    public void updateStudent(Long id, String name, String email, String dob){
        if(!studentRepository.findById(id).isPresent()){
            throw new IllegalStateException("Student ID " + id + " does not exist.");
        }
        Student student = studentRepository.findById(id)
                .orElseThrow(() ->new IllegalStateException("Student ID " + id + " does not exist."));
        if (isNotNullOrEmpty(name)){
            student.setName(name);
        }
        if (isNotNullOrEmpty(email)) {
            student.setEmail(email);
        }
        if (isNotNullOrEmpty(dob)) {
            student.setDob(LocalDate.parse(dob));
        }

        studentRepository.save(student);
    }

    private Boolean isNotNullOrEmpty(String checkString){
        return checkString == null || !checkString.isEmpty();
    }
}
