package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Entity.ExamMaster;
import com.helixion.sarkariPariksha.Entity.Subject;
import com.helixion.sarkariPariksha.Repository.ExamRepository;
import com.helixion.sarkariPariksha.Repository.SubjectRepository;
import com.helixion.sarkariPariksha.Request.SubjectRequest;
import com.helixion.sarkariPariksha.Response.SubjectResponse;
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
public class SubjectServicesImpl implements SubjectServices {

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ExamRepository examRepository;

    @Override
    public BaseResponse<SubjectResponse> saveSubject(SubjectRequest subjectRequest) {
        if (subjectRequest == null) {
            BaseResponse<SubjectResponse> baseResponse = new BaseResponse<>();
            baseResponse.setStatus(0);
            baseResponse.setCode(400);
            baseResponse.setMessage("Subject request cannot be null");
            return baseResponse;
        }
        return saveSubjects(Collections.singletonList(subjectRequest));
    }

    @Override
    @Transactional
    public BaseResponse<SubjectResponse> saveSubjects(List<SubjectRequest> subjectRequests) {
        BaseResponse<SubjectResponse> baseResponse = new BaseResponse<>();
        try {
            if (subjectRequests == null || subjectRequests.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Subject request list cannot be empty");
                return baseResponse;
            }

            List<SubjectResponse> responseList = new ArrayList<>();

            for (SubjectRequest subjectRequest : subjectRequests) {
                if (subjectRequest.getExamId() == null) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Exam ID is required for subject: " + subjectRequest.getSubjectName());
                    return baseResponse;
                }

                if (subjectRequest.getSubjectName() == null || subjectRequest.getSubjectName().trim().isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Subject name is required");
                    return baseResponse;
                }

                if (subjectRequest.getSubjectCode() == null || subjectRequest.getSubjectCode().trim().isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Subject code is required");
                    return baseResponse;
                }

                // Verify Exam exists
                Optional<ExamMaster> examOptional = examRepository.findById(subjectRequest.getExamId());
                if (examOptional.isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(404);
                    baseResponse.setMessage("Exam not found with ID: " + subjectRequest.getExamId());
                    return baseResponse;
                }

                Subject subject = null;

                // Check if update by ID
                if (subjectRequest.getId() != null) {
                    subject = subjectRepository.findById(subjectRequest.getId()).orElse(null);
                    if (subject == null) {
                        baseResponse.setStatus(0);
                        baseResponse.setCode(404);
                        baseResponse.setMessage("Subject not found with ID: " + subjectRequest.getId());
                        return baseResponse;
                    }
                } else {
                    // Check if subject with code exists for this exam
                    Optional<Subject> existingByCode = subjectRepository.findBySubjectCode(subjectRequest.getSubjectCode().trim().toUpperCase());
                    if (existingByCode.isPresent()) {
                        subject = existingByCode.get(); // Update if exists
                    } else {
                        subject = new Subject();
                    }
                }

                // Map fields
                subject.setExam(examOptional.get());
                subject.setSubjectName(subjectRequest.getSubjectName().trim());
                subject.setSubjectCode(subjectRequest.getSubjectCode().trim().toUpperCase());
                subject.setDescription(subjectRequest.getDescription());
                subject.setDisplayOrder(subjectRequest.getDisplayOrder() != null ? subjectRequest.getDisplayOrder() : 0);
                subject.setActive(subjectRequest.getActive() != null ? subjectRequest.getActive() : true);

                Subject savedSubject = subjectRepository.save(subject);
                responseList.add(mapToSubjectResponse(savedSubject));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage(responseList.size() + " subject(s) saved successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in saveSubjects: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to save subjects: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<SubjectResponse> getSubjectById(Long id) {
        BaseResponse<SubjectResponse> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Subject ID cannot be null");
                return baseResponse;
            }

            Optional<Subject> subjectOptional = subjectRepository.findById(id);
            if (subjectOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Subject not found with ID: " + id);
                return baseResponse;
            }

            SubjectResponse response = mapToSubjectResponse(subjectOptional.get());

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Subject fetched successfully");
            baseResponse.setData(Collections.singletonList(response));

        } catch (Exception exception) {
            log.error("Exception in getSubjectById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch subject: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<SubjectResponse> getAllSubjects() {
        BaseResponse<SubjectResponse> baseResponse = new BaseResponse<>();
        try {
            List<Subject> subjects = subjectRepository.findByActiveTrue();
            List<SubjectResponse> responseList = new ArrayList<>();
            for (Subject s : subjects) {
                responseList.add(mapToSubjectResponse(s));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("All active subjects fetched successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getAllSubjects: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch subjects: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<SubjectResponse> getSubjectsByExamId(Long examId) {
        BaseResponse<SubjectResponse> baseResponse = new BaseResponse<>();
        try {
            if (examId == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Exam ID cannot be null");
                return baseResponse;
            }

            List<Subject> subjects = subjectRepository.findByExam_IdAndActiveTrue(examId);
            List<SubjectResponse> responseList = new ArrayList<>();
            for (Subject s : subjects) {
                responseList.add(mapToSubjectResponse(s));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Subjects for Exam ID " + examId + " fetched successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getSubjectsByExamId: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch subjects by exam: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<String> deleteSubjectById(Long id) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Subject ID cannot be null");
                return baseResponse;
            }

            Optional<Subject> subjectOptional = subjectRepository.findById(id);
            if (subjectOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Subject not found with ID: " + id);
                return baseResponse;
            }

            // Soft delete: set active = false
            Subject subject = subjectOptional.get();
            subject.setActive(false);
            subjectRepository.save(subject);

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Subject deactivated successfully");
            baseResponse.setData(Collections.singletonList("Subject with ID " + id + " has been deactivated successfully"));

        } catch (Exception exception) {
            log.error("Exception in deleteSubjectById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to delete subject: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    private SubjectResponse mapToSubjectResponse(Subject subject) {
        SubjectResponse response = new SubjectResponse();
        response.setId(subject.getId());

        if (subject.getExam() != null) {
            response.setExamId(subject.getExam().getId());
            response.setExamName(subject.getExam().getExamName());
            response.setExamCode(subject.getExam().getExamCode());
        }

        response.setSubjectName(subject.getSubjectName());
        response.setSubjectCode(subject.getSubjectCode());
        response.setDescription(subject.getDescription());
        response.setDisplayOrder(subject.getDisplayOrder());
        response.setActive(subject.getActive());
        response.setCreatedAt(subject.getCreatedAt());
        response.setUpdatedAt(subject.getUpdatedAt());

        return response;
    }
}
