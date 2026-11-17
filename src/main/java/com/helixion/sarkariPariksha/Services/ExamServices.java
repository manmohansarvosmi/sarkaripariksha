package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Request.ExamRequest;
import com.helixion.sarkariPariksha.Response.ExamCategoryResponse;
import com.helixion.sarkariPariksha.Response.ExamResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;

import java.util.List;

public interface ExamServices {
    BaseResponse<ExamResponse> saveExam(ExamRequest examRequest);
    BaseResponse<ExamResponse> saveExams(List<ExamRequest> examRequests);
    BaseResponse<ExamResponse> getExamById(Long id);
    BaseResponse<ExamResponse> getAllExams();
    BaseResponse<ExamResponse> getExamsByCategoryId(Long categoryId);
    BaseResponse<String> deleteExamById(Long id);

    // Exam Category
    BaseResponse<ExamCategoryResponse> getAllExamCategories();
}
