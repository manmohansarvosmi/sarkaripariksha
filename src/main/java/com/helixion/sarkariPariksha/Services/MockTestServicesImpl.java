package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Entity.ExamMaster;
import com.helixion.sarkariPariksha.Entity.MockTestMaster;
import com.helixion.sarkariPariksha.Entity.TestSeries;
import com.helixion.sarkariPariksha.Repository.ExamRepository;
import com.helixion.sarkariPariksha.Repository.MockTestAttemptRepository;
import com.helixion.sarkariPariksha.Repository.MockTestRepository;
import com.helixion.sarkariPariksha.Repository.TestSeriesRepository;
import com.helixion.sarkariPariksha.Request.MockTestRequest;
import com.helixion.sarkariPariksha.Response.MockTestResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class MockTestServicesImpl implements MockTestServices {

    @Autowired
    private MockTestRepository mockTestRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private TestSeriesRepository testSeriesRepository;

    @Autowired
    private MockTestAttemptRepository mockTestAttemptRepository;

    @Override
    public BaseResponse<MockTestResponse> saveMockTest(MockTestRequest mockTestRequest) {
        if (mockTestRequest == null) {
            BaseResponse<MockTestResponse> baseResponse = new BaseResponse<>();
            baseResponse.setStatus(0);
            baseResponse.setCode(400);
            baseResponse.setMessage("Mock test request cannot be null");
            return baseResponse;
        }
        return saveMockTests(Collections.singletonList(mockTestRequest));
    }

    @Override
    @Transactional
    public BaseResponse<MockTestResponse> saveMockTests(List<MockTestRequest> mockTestRequests) {
        BaseResponse<MockTestResponse> baseResponse = new BaseResponse<>();
        try {
            if (mockTestRequests == null || mockTestRequests.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Mock test request list cannot be empty");
                return baseResponse;
            }

            List<MockTestResponse> responseList = new ArrayList<>();

            for (MockTestRequest mockTestRequest : mockTestRequests) {
                if (mockTestRequest.getTestName() == null || mockTestRequest.getTestName().trim().isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Test name is required");
                    return baseResponse;
                }

                if (mockTestRequest.getTestType() == null || mockTestRequest.getTestType().trim().isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Test type is required (e.g. FULL_LENGTH, SECTIONAL, TOPIC_WISE)");
                    return baseResponse;
                }

                if (mockTestRequest.getExamId() == null) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Exam ID is required for test: " + mockTestRequest.getTestName());
                    return baseResponse;
                }

                // Verify Exam exists
                Optional<ExamMaster> examOptional = examRepository.findById(mockTestRequest.getExamId());
                if (examOptional.isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(404);
                    baseResponse.setMessage("Exam not found with ID: " + mockTestRequest.getExamId());
                    return baseResponse;
                }

                MockTestMaster mockTestMaster = null;

                // Check if update by ID
                if (mockTestRequest.getId() != null) {
                    mockTestMaster = mockTestRepository.findById(mockTestRequest.getId()).orElse(null);
                    if (mockTestMaster == null) {
                        baseResponse.setStatus(0);
                        baseResponse.setCode(404);
                        baseResponse.setMessage("Mock test not found with ID: " + mockTestRequest.getId());
                        return baseResponse;
                    }
                } else {
                    mockTestMaster = new MockTestMaster();
                }

                // Map fields
                mockTestMaster.setExam(examOptional.get());

                // Assign TestSeries if provided
                if (mockTestRequest.getTestSeriesId() != null) {
                    Optional<TestSeries> tsOpt = testSeriesRepository.findById(mockTestRequest.getTestSeriesId());
                    tsOpt.ifPresent(mockTestMaster::setTestSeries);
                }

                mockTestMaster.setTestName(mockTestRequest.getTestName().trim());
                mockTestMaster.setTestType(mockTestRequest.getTestType().trim().toUpperCase());
                mockTestMaster.setTotalQuestions(mockTestRequest.getTotalQuestions());
                mockTestMaster.setTotalMarks(mockTestRequest.getTotalMarks());
                mockTestMaster.setDurationMinutes(mockTestRequest.getDurationMinutes());
                mockTestMaster.setNegativeMark(mockTestRequest.getNegativeMark());
                mockTestMaster.setInstructions(mockTestRequest.getInstructions());
                mockTestMaster.setFree(mockTestRequest.getFree() != null ? mockTestRequest.getFree() : false);
                mockTestMaster.setCreatedBy(mockTestRequest.getCreatedBy());
                mockTestMaster.setActive(mockTestRequest.getActive() != null ? mockTestRequest.getActive() : true);

                MockTestMaster savedMockTest = mockTestRepository.save(mockTestMaster);
                responseList.add(mapToMockTestResponse(savedMockTest));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage(responseList.size() + " mock test(s) saved successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in saveMockTests: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to save mock tests: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<MockTestResponse> getMockTestById(Long id) {
        BaseResponse<MockTestResponse> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Mock test ID cannot be null");
                return baseResponse;
            }

            Optional<MockTestMaster> mockTestOptional = mockTestRepository.findById(id);
            if (mockTestOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Mock test not found with ID: " + id);
                return baseResponse;
            }

            MockTestResponse response = mapToMockTestResponse(mockTestOptional.get());

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Mock test fetched successfully");
            baseResponse.setData(Collections.singletonList(response));

        } catch (Exception exception) {
            log.error("Exception in getMockTestById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch mock test: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<MockTestResponse> getAllMockTests() {
        BaseResponse<MockTestResponse> baseResponse = new BaseResponse<>();
        try {
            List<MockTestMaster> mockTests = mockTestRepository.findByActiveTrue();
            List<MockTestResponse> responseList = new ArrayList<>();
            for (MockTestMaster test : mockTests) {
                responseList.add(mapToMockTestResponse(test));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("All active mock tests fetched successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getAllMockTests: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch mock tests: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<MockTestResponse> getMockTestsByExamId(Long examId) {
        BaseResponse<MockTestResponse> baseResponse = new BaseResponse<>();
        try {
            if (examId == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Exam ID cannot be null");
                return baseResponse;
            }

            List<MockTestMaster> mockTests = mockTestRepository.findByExam_IdAndActiveTrue(examId);
            List<MockTestResponse> responseList = new ArrayList<>();
            for (MockTestMaster test : mockTests) {
                responseList.add(mapToMockTestResponse(test));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Mock tests for Exam ID " + examId + " fetched successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getMockTestsByExamId: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch mock tests by exam: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<MockTestResponse> getMockTestsByTestSeriesId(Long testSeriesId) {
        BaseResponse<MockTestResponse> baseResponse = new BaseResponse<>();
        try {
            if (testSeriesId == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Test Series ID cannot be null");
                return baseResponse;
            }

            List<MockTestMaster> mockTests = mockTestRepository.findByTestSeries_IdAndActiveTrue(testSeriesId);
            List<MockTestResponse> responseList = new ArrayList<>();
            for (MockTestMaster test : mockTests) {
                responseList.add(mapToMockTestResponse(test));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Mock tests for Test Series ID " + testSeriesId + " fetched successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getMockTestsByTestSeriesId: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch mock tests by test series: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<MockTestResponse> getMockTestsByType(String testType) {
        BaseResponse<MockTestResponse> baseResponse = new BaseResponse<>();
        try {
            if (testType == null || testType.trim().isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Test type cannot be null or empty");
                return baseResponse;
            }

            List<MockTestMaster> mockTests = mockTestRepository.findByTestTypeAndActiveTrue(testType.trim().toUpperCase());
            List<MockTestResponse> responseList = new ArrayList<>();
            for (MockTestMaster test : mockTests) {
                responseList.add(mapToMockTestResponse(test));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Mock tests of type '" + testType + "' fetched successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getMockTestsByType: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch mock tests by type: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<String> deleteMockTestById(Long id) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Mock test ID cannot be null");
                return baseResponse;
            }

            Optional<MockTestMaster> mockTestOptional = mockTestRepository.findById(id);
            if (mockTestOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Mock test not found with ID: " + id);
                return baseResponse;
            }

            // Soft delete: set active = false
            MockTestMaster mockTest = mockTestOptional.get();
            mockTest.setActive(false);
            mockTestRepository.save(mockTest);

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Mock test deactivated successfully");
            baseResponse.setData(Collections.singletonList("Mock test with ID " + id + " has been deactivated successfully"));

        } catch (Exception exception) {
            log.error("Exception in deleteMockTestById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to delete mock test: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    private MockTestResponse mapToMockTestResponse(MockTestMaster test) {
        MockTestResponse response = new MockTestResponse();
        response.setId(test.getId());

        // Exam & Category mapping
        if (test.getExam() != null) {
            response.setExamId(test.getExam().getId());
            response.setExamName(test.getExam().getExamName());
            response.setExamCode(test.getExam().getExamCode());

            if (test.getExam().getExamCategory() != null) {
                response.setCategoryId(test.getExam().getExamCategory().getId());
                response.setCategoryName(test.getExam().getExamCategory().getCategoryName());
            }
        }

        // Test Series mapping
        if (test.getTestSeries() != null) {
            response.setTestSeriesId(test.getTestSeries().getId());
            response.setTestSeriesTitle(test.getTestSeries().getTitle());
        }

        response.setTestName(test.getTestName());
        response.setTestType(test.getTestType());
        response.setTotalQuestions(test.getTotalQuestions());
        response.setTotalMarks(test.getTotalMarks());
        response.setDurationMinutes(test.getDurationMinutes());
        response.setNegativeMark(test.getNegativeMark());
        response.setInstructions(test.getInstructions());
        response.setFree(test.getFree() != null ? test.getFree() : false);
        response.setActive(test.getActive());
        response.setCreatedBy(test.getCreatedBy());
        response.setCreatedAt(test.getCreatedAt());
        response.setUpdatedAt(test.getUpdatedAt());

        // Kitne logo ne attempt kiya (default 0 agar kisi ne attempt nahi kiya)
        Long attempts = 0L;
        Long candidates = 0L;
        if (test.getId() != null) {
            attempts = mockTestAttemptRepository.countByMockTest_Id(test.getId());
            candidates = mockTestAttemptRepository.countDistinctCandidatesByMockTestId(test.getId());
        }
        response.setTotalAttempts(attempts != null ? attempts : 0L);
        response.setTotalCandidatesAttempted(candidates != null ? candidates : 0L);

        return response;
    }
}
