package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.QuestionOption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionOptionRepository extends JpaRepository<QuestionOption, Long> {
    List<QuestionOption> findByQuestion_Id(Long questionId);
    void deleteByQuestion_Id(Long questionId);
}

