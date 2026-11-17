package com.helixion.sarkariPariksha.Repository;

import com.helixion.sarkariPariksha.Entity.MockTestMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MockTestRepository extends JpaRepository<MockTestMaster, Long> {
    List<MockTestMaster> findByExam_Id(Long examId);
    List<MockTestMaster> findByActiveTrue();
    List<MockTestMaster> findByExam_IdAndActiveTrue(Long examId);
    List<MockTestMaster> findByTestTypeAndActiveTrue(String testType);
    Long countByExam_IdAndActiveTrue(Long examId);

    // Test Series Mapping
    List<MockTestMaster> findByTestSeries_Id(Long testSeriesId);
    List<MockTestMaster> findByTestSeries_IdAndActiveTrue(Long testSeriesId);
    Long countByTestSeries_IdAndActiveTrue(Long testSeriesId);
    Long countByTestSeries_IdAndFreeTrueAndActiveTrue(Long testSeriesId);
}
