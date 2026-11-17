package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Request.MockTestRequest;
import com.helixion.sarkariPariksha.Response.MockTestResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;

import java.util.List;

public interface MockTestServices {
    BaseResponse<MockTestResponse> saveMockTest(MockTestRequest mockTestRequest);
    BaseResponse<MockTestResponse> saveMockTests(List<MockTestRequest> mockTestRequests);
    BaseResponse<MockTestResponse> getMockTestById(Long id);
    BaseResponse<MockTestResponse> getAllMockTests();
    BaseResponse<MockTestResponse> getMockTestsByExamId(Long examId);
    BaseResponse<MockTestResponse> getMockTestsByTestSeriesId(Long testSeriesId);
    BaseResponse<MockTestResponse> getMockTestsByType(String testType);
    BaseResponse<String> deleteMockTestById(Long id);
}
