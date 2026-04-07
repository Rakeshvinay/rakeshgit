package com.brihathi.Multi_Tenant.repository;
 
import com.brihathi.Multi_Tenant.entity.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
 
@Repository
public interface DifficultyRepository extends JpaRepository<Difficulty, Long> {
}
 