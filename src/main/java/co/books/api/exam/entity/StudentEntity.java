package co.books.api.exam.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="students")
@Getter
@Setter
public class StudentEntity {

    @Id
    private String stdId;

    private String stdName;

    private int stdAge;

    private String stdPhone;

    private String  stdEmail;

    private String description;



}
