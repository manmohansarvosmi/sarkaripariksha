package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Request.SubjectRequest;
import com.helixion.sarkariPariksha.Response.SubjectResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;

import java.util.List;

public interface SubjectServices {
    BaseResponse<SubjectResponse> saveSubject(SubjectRequest subjectRequest);
    BaseResponse<SubjectResponse> saveSubjects(List<SubjectRequest> subjectRequests);
    BaseResponse<SubjectResponse> getSubjectById(Long id);
    BaseResponse<SubjectResponse> getAllSubjects();
    BaseResponse<SubjectResponse> getSubjectsByExamId(Long examId);
    BaseResponse<String> deleteSubjectById(Long id);
}
