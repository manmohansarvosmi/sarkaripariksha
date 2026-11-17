package com.helixion.sarkariPariksha.Controller;

import com.helixion.sarkariPariksha.Request.MockTestRequest;
import com.helixion.sarkariPariksha.Response.MockTestResponse;
import com.helixion.sarkariPariksha.Services.MockTestServices;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("MockTest")
@Tag(name = "MockTest Controller", description = "APIs for Mock Test Management")
public class MockTestController {

    @Autowired
    private MockTestServices mockTestServices;

    @PostMapping("/saveMockTest")
    @Operation(summary = "Save or Update Mock Tests (List)", description = "Creates or updates a list of mock tests for exams.")
    public BaseResponse<MockTestResponse> saveMockTest(@RequestBody List<MockTestRequest> mockTestRequests) {
        return mockTestServices.saveMockTests(mockTestRequests);
    }

    @GetMapping("/getMockTestById/{id}")
    @Operation(summary = "Get Mock Test By ID", description = "Fetches mock test details by ID.")
    public BaseResponse<MockTestResponse> getMockTestById(@PathVariable Long id) {
        return mockTestServices.getMockTestById(id);
    }

    @GetMapping("/getAllMockTests")
    @Operation(summary = "Get All Active Mock Tests", description = "Fetches all active mock tests.")
    public BaseResponse<MockTestResponse> getAllMockTests() {
        return mockTestServices.getAllMockTests();
    }

    @GetMapping("/getMockTestsByExam/{examId}")
    @Operation(summary = "Get Mock Tests By Exam ID", description = "Fetches all active mock tests belonging to a specific exam (e.g. all SSC CGL mock tests).")
    public BaseResponse<MockTestResponse> getMockTestsByExamId(@PathVariable Long examId) {
        return mockTestServices.getMockTestsByExamId(examId);
    }

    @GetMapping("/getMockTestsByTestSeries/{testSeriesId}")
    @Operation(summary = "Get Mock Tests By Test Series ID", description = "Fetches all active mock tests belonging to a specific test series pack.")
    public BaseResponse<MockTestResponse> getMockTestsByTestSeriesId(@PathVariable Long testSeriesId) {
        return mockTestServices.getMockTestsByTestSeriesId(testSeriesId);
    }

    @GetMapping("/getMockTestsByType/{testType}")
    @Operation(summary = "Get Mock Tests By Test Type", description = "Fetches all mock tests by type (e.g. FULL_LENGTH, SECTIONAL, TOPIC_WISE).")
    public BaseResponse<MockTestResponse> getMockTestsByType(@PathVariable String testType) {
        return mockTestServices.getMockTestsByType(testType);
    }

    @DeleteMapping("/deleteMockTestById/{id}")
    @Operation(summary = "Delete (Deactivate) Mock Test By ID", description = "Deactivates a mock test by its ID.")
    public BaseResponse<String> deleteMockTestById(@PathVariable Long id) {
        return mockTestServices.deleteMockTestById(id);
    }
}
