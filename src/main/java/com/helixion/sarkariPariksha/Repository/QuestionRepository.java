package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.QuestionMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<QuestionMaster, Long> {
    List<QuestionMaster> findByMockTest_Id(Long mockTestId);
    List<QuestionMaster> findByMockTest_IdAndActiveTrue(Long mockTestId);
    List<QuestionMaster> findBySubject_IdAndActiveTrue(Long subjectId);
    Long countByMockTest_Id(Long mockTestId);
}

