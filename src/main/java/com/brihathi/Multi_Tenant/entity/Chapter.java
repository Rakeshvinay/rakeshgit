package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.Data;
 
@Data
@Entity
@Table(name = "chapters")
public class Chapter {
    @Id
    @Column(name = "chapter_id")
    private String chapterId;
 
    @Column(name = "chapter", nullable = false)
    private String chapter;
 
    @Column(name = "subject", nullable = false)
    private String subject;
 
    @Column(name = "weightage")
    private Double weightage;
 
    @Column(name = "number_of_questions")
    private Integer numberOfQuestions;
}