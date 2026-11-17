package com.helixion.sarkariPariksha.Controller;

import com.helixion.sarkariPariksha.Request.ExamRequest;
import com.helixion.sarkariPariksha.Response.ExamCategoryResponse;
import com.helixion.sarkariPariksha.Response.ExamResponse;
import com.helixion.sarkariPariksha.Services.ExamServices;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("Exam")
@Tag(name = "Exam Controller", description = "APIs for Exam and Exam Category Management")
public class ExamController {

    @Autowired
    private ExamServices examServices;

    @PostMapping("/saveExam")
    @Operation(summary = "Save or Update Exams (List)", description = "Creates or updates a list of exams under categories (e.g. SSC CGL, SSC CHSL).")
    public BaseResponse<ExamResponse> saveExam(@RequestBody List<ExamRequest> examRequests) {
        return examServices.saveExams(examRequests);
    }

    @GetMapping("/getExamById/{id}")
    @Operation(summary = "Get Exam By ID", description = "Fetches exam details by its ID.")
    public BaseResponse<ExamResponse> getExamById(@PathVariable Long id) {
        return examServices.getExamById(id);
    }

    @GetMapping("/getAllExams")
    @Operation(summary = "Get All Active Exams", description = "Fetches all active exams along with total mock tests and attempts count.")
    public BaseResponse<ExamResponse> getAllExams() {
        return examServices.getAllExams();
    }

    @GetMapping("/getExamsByCategory/{categoryId}")
    @Operation(summary = "Get Exams By Category ID", description = "Fetches all exams belonging to a specific category (e.g., all exams under SSC).")
    public BaseResponse<ExamResponse> getExamsByCategoryId(@PathVariable Long categoryId) {
        return examServices.getExamsByCategoryId(categoryId);
    }

    @GetMapping("/getAllCategories")
    @Operation(summary = "Get All Exam Categories", description = "Fetches all active exam categories (e.g. SSC, Banking, Railways, Defence) along with total exams count.")
    public BaseResponse<ExamCategoryResponse> getAllExamCategories() {
        return examServices.getAllExamCategories();
    }

    @DeleteMapping("/deleteExamById/{id}")
    @Operation(summary = "Delete (Deactivate) Exam By ID", description = "Deactivates an exam by its ID.")
    public BaseResponse<String> deleteExamById(@PathVariable Long id) {
        return examServices.deleteExamById(id);
    }
}
