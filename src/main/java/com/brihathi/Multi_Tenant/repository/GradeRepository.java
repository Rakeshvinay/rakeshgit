package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {
}
