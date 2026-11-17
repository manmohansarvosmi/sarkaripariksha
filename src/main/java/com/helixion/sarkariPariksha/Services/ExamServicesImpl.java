package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Entity.ExamCategory;
import com.helixion.sarkariPariksha.Entity.ExamMaster;
import com.helixion.sarkariPariksha.Repository.ExamCategoryRepository;
import com.helixion.sarkariPariksha.Repository.ExamRepository;
import com.helixion.sarkariPariksha.Repository.MockTestAttemptRepository;
import com.helixion.sarkariPariksha.Repository.MockTestRepository;
import com.helixion.sarkariPariksha.Request.ExamRequest;
import com.helixion.sarkariPariksha.Response.ExamCategoryResponse;
import com.helixion.sarkariPariksha.Response.ExamResponse;
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
public class ExamServicesImpl implements ExamServices {

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private ExamCategoryRepository examCategoryRepository;

    @Autowired
    private MockTestRepository mockTestRepository;

    @Autowired
    private MockTestAttemptRepository mockTestAttemptRepository;

    @Override
    public BaseResponse<ExamResponse> saveExam(ExamRequest examRequest) {
        if (examRequest == null) {
            BaseResponse<ExamResponse> baseResponse = new BaseResponse<>();
            baseResponse.setStatus(0);
            baseResponse.setCode(400);
            baseResponse.setMessage("Exam request cannot be null");
            return baseResponse;
        }
        return saveExams(Collections.singletonList(examRequest));
    }

    @Override
    @Transactional
    public BaseResponse<ExamResponse> saveExams(List<ExamRequest> examRequests) {
        BaseResponse<ExamResponse> baseResponse = new BaseResponse<>();
        try {
            if (examRequests == null || examRequests.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Exam request list cannot be empty");
                return baseResponse;
            }

            List<ExamResponse> responseList = new ArrayList<>();

            for (ExamRequest examRequest : examRequests) {
                if (examRequest.getExamName() == null || examRequest.getExamName().trim().isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Exam name is required");
                    return baseResponse;
                }

                if (examRequest.getExamCode() == null || examRequest.getExamCode().trim().isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Exam code is required");
                    return baseResponse;
                }

                ExamMaster examMaster = null;

                // Check if update by ID
                if (examRequest.getId() != null) {
                    examMaster = examRepository.findById(examRequest.getId()).orElse(null);
                    if (examMaster == null) {
                        baseResponse.setStatus(0);
                        baseResponse.setCode(404);
                        baseResponse.setMessage("Exam not found with ID: " + examRequest.getId());
                        return baseResponse;
                    }
                } else {
                    // Check if exam code already exists
                    Optional<ExamMaster> existingByCode = examRepository.findByExamCode(examRequest.getExamCode().trim().toUpperCase());
                    if (existingByCode.isPresent()) {
                        examMaster = existingByCode.get(); // Update if exists
                    } else {
                        examMaster = new ExamMaster();
                    }
                }

                // Assign Category if categoryId is provided
                if (examRequest.getCategoryId() != null) {
                    Optional<ExamCategory> categoryOptional = examCategoryRepository.findById(examRequest.getCategoryId());
                    if (categoryOptional.isEmpty()) {
                        baseResponse.setStatus(0);
                        baseResponse.setCode(404);
                        baseResponse.setMessage("Exam Category not found with ID: " + examRequest.getCategoryId());
                        return baseResponse;
                    }
                    examMaster.setExamCategory(categoryOptional.get());
                }

                // Map fields
                examMaster.setExamName(examRequest.getExamName().trim());
                examMaster.setExamCode(examRequest.getExamCode().trim().toUpperCase());
                examMaster.setDescription(examRequest.getDescription());
                examMaster.setIconUrl(examRequest.getIconUrl());
                examMaster.setEligibility(examRequest.getEligibility());
                examMaster.setDisplayOrder(examRequest.getDisplayOrder() != null ? examRequest.getDisplayOrder() : 0);
                examMaster.setActive(examRequest.getActive() != null ? examRequest.getActive() : true);

                ExamMaster savedExam = examRepository.save(examMaster);
                responseList.add(mapToExamResponse(savedExam));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage(responseList.size() + " exam(s) saved successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in saveExams: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to save exams: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<ExamResponse> getExamById(Long id) {
        BaseResponse<ExamResponse> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Exam ID cannot be null");
                return baseResponse;
            }

            Optional<ExamMaster> examOptional = examRepository.findById(id);
            if (examOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Exam not found with ID: " + id);
                return baseResponse;
            }

            ExamResponse examResponse = mapToExamResponse(examOptional.get());

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Exam fetched successfully");
            baseResponse.setData(Collections.singletonList(examResponse));

        } catch (Exception exception) {
            log.error("Exception in getExamById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch exam: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<ExamResponse> getAllExams() {
        BaseResponse<ExamResponse> baseResponse = new BaseResponse<>();
        try {
            List<ExamMaster> examList = examRepository.findByActiveTrue();
            List<ExamResponse> responseList = new ArrayList<>();
            for (ExamMaster exam : examList) {
                responseList.add(mapToExamResponse(exam));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("All active exams fetched successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getAllExams: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch exams: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<ExamResponse> getExamsByCategoryId(Long categoryId) {
        BaseResponse<ExamResponse> baseResponse = new BaseResponse<>();
        try {
            if (categoryId == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Category ID cannot be null");
                return baseResponse;
            }

            List<ExamMaster> examList = examRepository.findByExamCategory_Id(categoryId);
            List<ExamResponse> responseList = new ArrayList<>();
            for (ExamMaster exam : examList) {
                responseList.add(mapToExamResponse(exam));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Exams for category ID " + categoryId + " fetched successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getExamsByCategoryId: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch exams by category: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<String> deleteExamById(Long id) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Exam ID cannot be null");
                return baseResponse;
            }

            Optional<ExamMaster> examOptional = examRepository.findById(id);
            if (examOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Exam not found with ID: " + id);
                return baseResponse;
            }

            // Soft delete: set active = false
            ExamMaster exam = examOptional.get();
            exam.setActive(false);
            examRepository.save(exam);

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Exam deactivated successfully");
            baseResponse.setData(Collections.singletonList("Exam with ID " + id + " has been deactivated successfully"));

        } catch (Exception exception) {
            log.error("Exception in deleteExamById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to delete exam: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<ExamCategoryResponse> getAllExamCategories() {
        BaseResponse<ExamCategoryResponse> baseResponse = new BaseResponse<>();
        try {
            List<ExamCategory> categories = examCategoryRepository.findByActiveTrueOrderByDisplayOrderAsc();
            List<ExamCategoryResponse> responseList = new ArrayList<>();

            for (ExamCategory category : categories) {
                ExamCategoryResponse response = new ExamCategoryResponse();
                response.setId(category.getId());
                response.setCategoryName(category.getCategoryName());
                response.setCategoryCode(category.getCategoryCode());
                response.setDescription(category.getDescription());
                response.setIconUrl(category.getIconUrl());
                response.setDisplayOrder(category.getDisplayOrder());
                response.setActive(category.getActive());
                response.setCreatedAt(category.getCreatedAt());
                response.setUpdatedAt(category.getUpdatedAt());

                // Total active exams count under this category
                int examCount = examRepository.findByExamCategory_Id(category.getId()).size();
                response.setTotalExams(examCount);

                responseList.add(response);
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("All active exam categories fetched successfully (" + responseList.size() + " categories)");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getAllExamCategories: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch exam categories: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    private ExamResponse mapToExamResponse(ExamMaster exam) {
        ExamResponse response = new ExamResponse();
        response.setId(exam.getId());
        if (exam.getExamCategory() != null) {
            response.setCategoryId(exam.getExamCategory().getId());
            response.setCategoryName(exam.getExamCategory().getCategoryName());
            response.setCategoryCode(exam.getExamCategory().getCategoryCode());
        }
        response.setExamName(exam.getExamName());
        response.setExamCode(exam.getExamCode());
        response.setDescription(exam.getDescription());
        response.setIconUrl(exam.getIconUrl());
        response.setEligibility(exam.getEligibility());
        response.setDisplayOrder(exam.getDisplayOrder());
        response.setActive(exam.getActive());
        response.setCreatedAt(exam.getCreatedAt());
        response.setUpdatedAt(exam.getUpdatedAt());

        // Count mock tests and attempts for this exam
        Long mockTestCount = 0L;
        Long attemptCount = 0L;
        if (exam.getId() != null) {
            mockTestCount = mockTestRepository.countByExam_IdAndActiveTrue(exam.getId());
            attemptCount = mockTestAttemptRepository.countByExamId(exam.getId());
        }
        response.setTotalMockTests(mockTestCount != null ? mockTestCount : 0L);
        response.setTotalAttempts(attemptCount != null ? attemptCount : 0L);

        return response;
    }
}
