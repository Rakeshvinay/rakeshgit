package com.brihathi.Multi_Tenant.entity;
 
import jakarta.persistence.*;
import lombok.Data;
 
@Entity
@Table(name = "difficulties")
@Data
public class Difficulty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
 
    @Column(nullable = false, unique = true)
    private String difficulty;
}
 