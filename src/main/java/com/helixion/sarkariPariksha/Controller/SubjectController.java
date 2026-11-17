package com.helixion.sarkariPariksha.Controller;

import com.helixion.sarkariPariksha.Request.SubjectRequest;
import com.helixion.sarkariPariksha.Response.SubjectResponse;
import com.helixion.sarkariPariksha.Services.SubjectServices;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("Subject")
@Tag(name = "Subject Controller", description = "APIs for Subject Management")
public class SubjectController {

    @Autowired
    private SubjectServices subjectServices;

    @PostMapping("/saveSubject")
    @Operation(summary = "Save or Update Subjects (List)", description = "Creates or updates a list of subjects under an exam (e.g. Quant, Reasoning, English).")
    public BaseResponse<SubjectResponse> saveSubject(@RequestBody List<SubjectRequest> subjectRequests) {
        return subjectServices.saveSubjects(subjectRequests);
    }

    @GetMapping("/getSubjectById/{id}")
    @Operation(summary = "Get Subject By ID", description = "Fetches subject details by its ID.")
    public BaseResponse<SubjectResponse> getSubjectById(@PathVariable Long id) {
        return subjectServices.getSubjectById(id);
    }

    @GetMapping("/getAllSubjects")
    @Operation(summary = "Get All Active Subjects", description = "Fetches all active subjects.")
    public BaseResponse<SubjectResponse> getAllSubjects() {
        return subjectServices.getAllSubjects();
    }

    @GetMapping("/getSubjectsByExam/{examId}")
    @Operation(summary = "Get Subjects By Exam ID", description = "Fetches all active subjects for a given exam (e.g. all subjects for SSC CGL).")
    public BaseResponse<SubjectResponse> getSubjectsByExamId(@PathVariable Long examId) {
        return subjectServices.getSubjectsByExamId(examId);
    }

    @DeleteMapping("/deleteSubjectById/{id}")
    @Operation(summary = "Delete (Deactivate) Subject By ID", description = "Deactivates a subject by its ID.")
    public BaseResponse<String> deleteSubjectById(@PathVariable Long id) {
        return subjectServices.deleteSubjectById(id);
    }
}
