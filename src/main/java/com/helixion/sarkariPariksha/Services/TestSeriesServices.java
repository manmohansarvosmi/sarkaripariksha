package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Request.TestSeriesRequest;
import com.helixion.sarkariPariksha.Response.TestSeriesResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;

import java.util.List;

public interface TestSeriesServices {
    BaseResponse<TestSeriesResponse> saveTestSeries(TestSeriesRequest testSeriesRequest);
    BaseResponse<TestSeriesResponse> saveTestSeriesList(List<TestSeriesRequest> testSeriesRequests);
    BaseResponse<TestSeriesResponse> getTestSeriesById(Long id);
    BaseResponse<TestSeriesResponse> getAllTestSeries();
    BaseResponse<TestSeriesResponse> getTestSeriesByExamId(Long examId);
    BaseResponse<String> deleteTestSeriesById(Long id);
}

