package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.ExamCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExamCategoryRepository extends JpaRepository<ExamCategory, Long> {
    Optional<ExamCategory> findByCategoryCode(String categoryCode);
    List<ExamCategory> findByActiveTrue();
    List<ExamCategory> findByActiveTrueOrderByDisplayOrderAsc();
}
