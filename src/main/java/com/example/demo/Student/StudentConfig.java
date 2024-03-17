package com.example.demo.Student;

import com.example.demo.batch.Processor;
import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class StudentConfig {

    private JobBuilderFactory jobBuilderFactory;

    private StepBuilderFactory stepBuilderFactory;

    private StudentRepository studentRepository;

    @Bean
    public FlatFileItemReader<Student> reader(){
        FlatFileItemReader<Student> flatFileItemReader = new FlatFileItemReader<>();
        flatFileItemReader.setResource(new FileSystemResource("src/main/resources/students.csv"));
        flatFileItemReader.setName("csvReader");
        flatFileItemReader.setLinesToSkip(1);
        flatFileItemReader.setLineMapper(lineMapper());
        return flatFileItemReader;
    }

    @Bean
    public LineMapper<Student> lineMapper(){
        DefaultLineMapper<Student> lineMapper = new DefaultLineMapper<>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();

        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);
        lineTokenizer.setNames("id", "name", "dob", "email");

        BeanWrapperFieldSetMapper<Student> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(Student.class);

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Bean
    public Processor processor(){
        return new Processor();
    }

    @Bean
    public RepositoryItemWriter<Student> writer(){
        RepositoryItemWriter<Student> writer = new RepositoryItemWriter<>();
        writer.setRepository(studentRepository);
        writer.setMethodName("save");

        return writer;
    }

    @Bean
    public Step step(){
        return stepBuilderFactory.get("csv-step")
                .<Student, Student>chunk(2)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .build();
    }

    @Bean
    public Job job(){
        return jobBuilderFactory.get("importStudents")
                .flow(step())
                .end().build();
    }

    @Bean
    CommandLineRunner commandLineRunner (StudentRepository repository){
        return args -> {
            Student jason = new Student(
            "Jason",
             LocalDate.of(1986, Month.OCTOBER, 8),
            "jason@jason.com"
            );

            Student lucky = new Student(
                    "Lucksmie",
                    LocalDate.of(1988, Month.JANUARY, 13),
                    "lucky@lucky.com"
            );

            repository.saveAll(
                    Arrays.asList(jason, lucky)
            );
        };
    }
}
