package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByExam_Id(Long examId);
    List<Subject> findByExam_IdAndActiveTrue(Long examId);
    List<Subject> findByActiveTrue();
    Optional<Subject> findFirstByExam_Id(Long examId);
    Optional<Subject> findBySubjectCode(String subjectCode);
    Optional<Subject> findBySubjectNameIgnoreCase(String subjectName);
}
