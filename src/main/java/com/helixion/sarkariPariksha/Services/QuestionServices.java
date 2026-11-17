package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Request.QuestionRequest;
import com.helixion.sarkariPariksha.Response.BulkUploadResponse;
import com.helixion.sarkariPariksha.Response.QuestionResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface QuestionServices {
    BaseResponse<QuestionResponse> saveQuestion(QuestionRequest questionRequest);
    BaseResponse<QuestionResponse> saveQuestions(List<QuestionRequest> questionRequests);
    BaseResponse<QuestionResponse> getQuestionById(Long id);
    BaseResponse<QuestionResponse> getQuestionsByMockTestId(Long mockTestId);
    BaseResponse<String> deleteQuestionById(Long id);
    byte[] generateExcelTemplate();
    BaseResponse<BulkUploadResponse> uploadQuestionsExcel(MultipartFile file, Long defaultMockTestId);
}
