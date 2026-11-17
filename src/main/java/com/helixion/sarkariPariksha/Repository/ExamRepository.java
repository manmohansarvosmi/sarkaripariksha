package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.ExamMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamRepository extends JpaRepository<ExamMaster, Long> {
    Optional<ExamMaster> findByExamCode(String examCode);
    List<ExamMaster> findByExamCategory_Id(Long categoryId);
    List<ExamMaster> findByActiveTrue();
}

