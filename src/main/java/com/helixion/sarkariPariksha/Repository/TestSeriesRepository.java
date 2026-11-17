package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.TestSeries;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestSeriesRepository extends JpaRepository<TestSeries, Long> {
    List<TestSeries> findByExam_Id(Long examId);
    List<TestSeries> findByExam_IdAndActiveTrue(Long examId);
    List<TestSeries> findByActiveTrue();
    List<TestSeries> findByActiveTrueOrderByDisplayOrderAsc();
    Long countByExam_IdAndActiveTrue(Long examId);
}

