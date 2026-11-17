package com.helixion.sarkariPariksha.Controller;

import com.helixion.sarkariPariksha.Request.TestSeriesRequest;
import com.helixion.sarkariPariksha.Response.TestSeriesResponse;
import com.helixion.sarkariPariksha.Services.TestSeriesServices;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("TestSeries")
@Tag(name = "TestSeries Controller", description = "APIs for Test Series Packs Management")
public class TestSeriesController {

    @Autowired
    private TestSeriesServices testSeriesServices;

    @PostMapping("/saveTestSeries")
    @Operation(summary = "Save or Update Test Series (List)", description = "Creates or updates a list of test series packs under exams (e.g. SSC CGL Master Test Series).")
    public BaseResponse<TestSeriesResponse> saveTestSeries(@RequestBody List<TestSeriesRequest> requests) {
        return testSeriesServices.saveTestSeriesList(requests);
    }

    @GetMapping("/getTestSeriesById/{id}")
    @Operation(summary = "Get Test Series By ID", description = "Fetches test series pack details by ID.")
    public BaseResponse<TestSeriesResponse> getTestSeriesById(@PathVariable Long id) {
        return testSeriesServices.getTestSeriesById(id);
    }

    @GetMapping("/getAllTestSeries")
    @Operation(summary = "Get All Active Test Series", description = "Fetches all active test series packs along with actual mock test counts.")
    public BaseResponse<TestSeriesResponse> getAllTestSeries() {
        return testSeriesServices.getAllTestSeries();
    }

    @GetMapping("/getTestSeriesByExam/{examId}")
    @Operation(summary = "Get Test Series By Exam ID", description = "Fetches all active test series packs belonging to a specific exam (e.g. all SSC CGL test series).")
    public BaseResponse<TestSeriesResponse> getTestSeriesByExamId(@PathVariable Long examId) {
        return testSeriesServices.getTestSeriesByExamId(examId);
    }

    @DeleteMapping("/deleteTestSeriesById/{id}")
    @Operation(summary = "Delete (Deactivate) Test Series By ID", description = "Deactivates a test series pack by its ID.")
    public BaseResponse<String> deleteTestSeriesById(@PathVariable Long id) {
        return testSeriesServices.deleteTestSeriesById(id);
    }
}

