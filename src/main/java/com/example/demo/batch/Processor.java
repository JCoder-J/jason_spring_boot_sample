package com.example.demo.batch;

import com.example.demo.Student.Student;
import org.springframework.batch.item.ItemProcessor;

public class Processor implements ItemProcessor<Student,Student> {

    @Override
    public Student process(Student student) throws Exception {
        return student;
    }
}
