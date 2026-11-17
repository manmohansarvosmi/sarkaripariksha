package com.helixion.sarkariPariksha.Controller;

import com.helixion.sarkariPariksha.Request.QuestionRequest;
import com.helixion.sarkariPariksha.Response.BulkUploadResponse;
import com.helixion.sarkariPariksha.Response.QuestionResponse;
import com.helixion.sarkariPariksha.Services.QuestionServices;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("Question")
@Tag(name = "Question Controller", description = "APIs for Question Management & Excel Bulk Upload/Download")
public class QuestionController {

    @Autowired
    private QuestionServices questionServices;

    @PostMapping("/saveQuestion")
    @Operation(summary = "Save or Update Questions (List)", description = "Creates or updates a list of questions with options (A, B, C, D).")
    public BaseResponse<QuestionResponse> saveQuestion(@RequestBody List<QuestionRequest> questionRequests) {
        return questionServices.saveQuestions(questionRequests);
    }

    @GetMapping("/getQuestionById/{id}")
    @Operation(summary = "Get Question By ID", description = "Fetches a question along with its options by question ID.")
    public BaseResponse<QuestionResponse> getQuestionById(@PathVariable Long id) {
        return questionServices.getQuestionById(id);
    }

    @GetMapping("/getQuestionsByMockTest/{mockTestId}")
    @Operation(summary = "Get Questions By Mock Test ID", description = "Fetches all questions and their options for a given mock test.")
    public BaseResponse<QuestionResponse> getQuestionsByMockTestId(@PathVariable Long mockTestId) {
        return questionServices.getQuestionsByMockTestId(mockTestId);
    }

    @DeleteMapping("/deleteQuestionById/{id}")
    @Operation(summary = "Delete (Deactivate) Question By ID", description = "Deactivates a question by its ID.")
    public BaseResponse<String> deleteQuestionById(@PathVariable Long id) {
        return questionServices.deleteQuestionById(id);
    }

    @GetMapping("/downloadTemplate")
    @Operation(summary = "Download Question Excel Template", description = "Downloads an Excel (.xlsx) file with pre-configured dropdowns for Exam, Mock Test, Difficulty, and Answer Types.")
    public ResponseEntity<byte[]> downloadExcelTemplate() {
        byte[] excelBytes = questionServices.generateExcelTemplate();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"Questions_Upload_Template.xlsx\"")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(excelBytes);
    }

    @PostMapping(value = "/uploadExcel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Bulk Upload Questions from Excel", description = "Uploads questions and options in bulk from the filled Excel template. Mock Test can be read from the sheet or passed as default.")
    public BaseResponse<BulkUploadResponse> uploadQuestionsExcel(
            @Parameter(description = "Excel file (.xlsx)", content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE))
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "defaultMockTestId", required = false) Long defaultMockTestId) {
        return questionServices.uploadQuestionsExcel(file, defaultMockTestId);
    }
}

