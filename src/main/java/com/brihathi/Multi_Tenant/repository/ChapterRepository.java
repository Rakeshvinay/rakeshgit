package com.brihathi.Multi_Tenant.repository;

import com.brihathi.Multi_Tenant.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, String> {
    Optional<Chapter> findBySubjectAndChapter(String subject, String chapter);
    Optional<Chapter> findBySubjectAndChapterId(String subject, String chapterId);
    List<Chapter> findBySubject(String subject);
 
} 