package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Entity.ExamMaster;
import com.helixion.sarkariPariksha.Entity.TestSeries;
import com.helixion.sarkariPariksha.Repository.ExamRepository;
import com.helixion.sarkariPariksha.Repository.MockTestRepository;
import com.helixion.sarkariPariksha.Repository.TestSeriesRepository;
import com.helixion.sarkariPariksha.Request.TestSeriesRequest;
import com.helixion.sarkariPariksha.Response.TestSeriesResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class TestSeriesServicesImpl implements TestSeriesServices {

    @Autowired
    private TestSeriesRepository testSeriesRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private MockTestRepository mockTestRepository;

    @Override
    public BaseResponse<TestSeriesResponse> saveTestSeries(TestSeriesRequest testSeriesRequest) {
        if (testSeriesRequest == null) {
            BaseResponse<TestSeriesResponse> baseResponse = new BaseResponse<>();
            baseResponse.setStatus(0);
            baseResponse.setCode(400);
            baseResponse.setMessage("Test series request cannot be null");
            return baseResponse;
        }
        return saveTestSeriesList(Collections.singletonList(testSeriesRequest));
    }

    @Override
    @Transactional
    public BaseResponse<TestSeriesResponse> saveTestSeriesList(List<TestSeriesRequest> testSeriesRequests) {
        BaseResponse<TestSeriesResponse> baseResponse = new BaseResponse<>();
        try {
            if (testSeriesRequests == null || testSeriesRequests.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Test series request list cannot be empty");
                return baseResponse;
            }

            List<TestSeriesResponse> responseList = new ArrayList<>();

            for (TestSeriesRequest req : testSeriesRequests) {
                if (req.getTitle() == null || req.getTitle().trim().isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Test series title is required");
                    return baseResponse;
                }

                if (req.getExamId() == null) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Exam ID is required for test series: " + req.getTitle());
                    return baseResponse;
                }

                // Verify Exam exists
                Optional<ExamMaster> examOptional = examRepository.findById(req.getExamId());
                if (examOptional.isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(404);
                    baseResponse.setMessage("Exam not found with ID: " + req.getExamId());
                    return baseResponse;
                }

                TestSeries testSeries = null;

                // Check if update by ID
                if (req.getId() != null) {
                    testSeries = testSeriesRepository.findById(req.getId()).orElse(null);
                    if (testSeries == null) {
                        baseResponse.setStatus(0);
                        baseResponse.setCode(404);
                        baseResponse.setMessage("Test series not found with ID: " + req.getId());
                        return baseResponse;
                    }
                } else {
                    testSeries = new TestSeries();
                }

                // Map fields
                testSeries.setExam(examOptional.get());
                testSeries.setTitle(req.getTitle().trim());
                testSeries.setSubTitle(req.getSubTitle());
                testSeries.setDescription(req.getDescription());
                testSeries.setIconUrl(req.getIconUrl());
                testSeries.setBannerUrl(req.getBannerUrl());
                testSeries.setLanguage(req.getLanguage() != null ? req.getLanguage() : "Bilingual");
                testSeries.setRating(req.getRating() != null ? req.getRating() : 4.8);
                testSeries.setTotalEnrolled(req.getTotalEnrolled() != null ? req.getTotalEnrolled() : 0L);
                testSeries.setTotalTests(req.getTotalTests() != null ? req.getTotalTests() : 0);
                testSeries.setTotalFreeTests(req.getTotalFreeTests() != null ? req.getTotalFreeTests() : 0);
                testSeries.setPrice(req.getPrice() != null ? req.getPrice() : BigDecimal.ZERO);
                testSeries.setDiscountPrice(req.getDiscountPrice() != null ? req.getDiscountPrice() : BigDecimal.ZERO);
                testSeries.setFree(req.getIsFree() != null ? req.getIsFree() : false);
                testSeries.setDisplayOrder(req.getDisplayOrder() != null ? req.getDisplayOrder() : 0);
                testSeries.setActive(req.getActive() != null ? req.getActive() : true);

                TestSeries savedTestSeries = testSeriesRepository.save(testSeries);
                responseList.add(mapToTestSeriesResponse(savedTestSeries));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage(responseList.size() + " test series saved successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in saveTestSeriesList: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to save test series: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<TestSeriesResponse> getTestSeriesById(Long id) {
        BaseResponse<TestSeriesResponse> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Test series ID cannot be null");
                return baseResponse;
            }

            Optional<TestSeries> tsOptional = testSeriesRepository.findById(id);
            if (tsOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Test series not found with ID: " + id);
                return baseResponse;
            }

            TestSeriesResponse response = mapToTestSeriesResponse(tsOptional.get());

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Test series fetched successfully");
            baseResponse.setData(Collections.singletonList(response));

        } catch (Exception exception) {
            log.error("Exception in getTestSeriesById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch test series: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<TestSeriesResponse> getAllTestSeries() {
        BaseResponse<TestSeriesResponse> baseResponse = new BaseResponse<>();
        try {
            List<TestSeries> testSeriesList = testSeriesRepository.findByActiveTrueOrderByDisplayOrderAsc();
            List<TestSeriesResponse> responseList = new ArrayList<>();
            for (TestSeries ts : testSeriesList) {
                responseList.add(mapToTestSeriesResponse(ts));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("All active test series fetched successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getAllTestSeries: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch test series: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<TestSeriesResponse> getTestSeriesByExamId(Long examId) {
        BaseResponse<TestSeriesResponse> baseResponse = new BaseResponse<>();
        try {
            if (examId == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Exam ID cannot be null");
                return baseResponse;
            }

            List<TestSeries> testSeriesList = testSeriesRepository.findByExam_IdAndActiveTrue(examId);
            List<TestSeriesResponse> responseList = new ArrayList<>();
            for (TestSeries ts : testSeriesList) {
                responseList.add(mapToTestSeriesResponse(ts));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Test series for Exam ID " + examId + " fetched successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getTestSeriesByExamId: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch test series by exam: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<String> deleteTestSeriesById(Long id) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Test series ID cannot be null");
                return baseResponse;
            }

            Optional<TestSeries> tsOptional = testSeriesRepository.findById(id);
            if (tsOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Test series not found with ID: " + id);
                return baseResponse;
            }

            // Soft delete: set active = false
            TestSeries ts = tsOptional.get();
            ts.setActive(false);
            testSeriesRepository.save(ts);

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Test series deactivated successfully");
            baseResponse.setData(Collections.singletonList("Test series with ID " + id + " has been deactivated successfully"));

        } catch (Exception exception) {
            log.error("Exception in deleteTestSeriesById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to delete test series: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    private TestSeriesResponse mapToTestSeriesResponse(TestSeries ts) {
        TestSeriesResponse response = new TestSeriesResponse();
        response.setId(ts.getId());

        // Exam & Category mapping
        if (ts.getExam() != null) {
            response.setExamId(ts.getExam().getId());
            response.setExamName(ts.getExam().getExamName());
            response.setExamCode(ts.getExam().getExamCode());

            if (ts.getExam().getExamCategory() != null) {
                response.setCategoryId(ts.getExam().getExamCategory().getId());
                response.setCategoryName(ts.getExam().getExamCategory().getCategoryName());
            }
        }

        response.setTitle(ts.getTitle());
        response.setSubTitle(ts.getSubTitle());
        response.setDescription(ts.getDescription());
        response.setIconUrl(ts.getIconUrl());
        response.setBannerUrl(ts.getBannerUrl());
        response.setLanguage(ts.getLanguage());
        response.setRating(ts.getRating());
        response.setTotalEnrolled(ts.getTotalEnrolled());

        // Live count of mock tests under this test series (if created in DB) or entity default
        Long liveTestCount = mockTestRepository.countByTestSeries_IdAndActiveTrue(ts.getId());
        Long liveFreeCount = mockTestRepository.countByTestSeries_IdAndFreeTrueAndActiveTrue(ts.getId());

        response.setTotalTests(liveTestCount > 0 ? liveTestCount.intValue() : (ts.getTotalTests() != null ? ts.getTotalTests() : 0));
        response.setTotalFreeTests(liveFreeCount > 0 ? liveFreeCount.intValue() : (ts.getTotalFreeTests() != null ? ts.getTotalFreeTests() : 0));

        response.setPrice(ts.getPrice());
        response.setDiscountPrice(ts.getDiscountPrice());
        response.setIsFree(ts.getFree());
        response.setDisplayOrder(ts.getDisplayOrder());
        response.setActive(ts.getActive());
        response.setCreatedAt(ts.getCreatedAt());
        response.setUpdatedAt(ts.getUpdatedAt());

        return response;
    }
}

