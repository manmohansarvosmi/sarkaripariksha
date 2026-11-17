package com.helixion.sarkariPariksha.Services;

import com.helixion.sarkariPariksha.Entity.ExamMaster;
import com.helixion.sarkariPariksha.Entity.MockTestMaster;
import com.helixion.sarkariPariksha.Entity.QuestionMaster;
import com.helixion.sarkariPariksha.Entity.QuestionOption;
import com.helixion.sarkariPariksha.Entity.Subject;
import com.helixion.sarkariPariksha.Repository.ExamRepository;
import com.helixion.sarkariPariksha.Repository.MockTestRepository;
import com.helixion.sarkariPariksha.Repository.QuestionOptionRepository;
import com.helixion.sarkariPariksha.Repository.QuestionRepository;
import com.helixion.sarkariPariksha.Repository.SubjectRepository;
import com.helixion.sarkariPariksha.Request.QuestionOptionRequest;
import com.helixion.sarkariPariksha.Request.QuestionRequest;
import com.helixion.sarkariPariksha.Response.BulkUploadResponse;
import com.helixion.sarkariPariksha.Response.QuestionOptionResponse;
import com.helixion.sarkariPariksha.Response.QuestionResponse;
import com.helixion.sarkariPariksha.Utils.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuestionServicesImpl implements QuestionServices {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuestionOptionRepository questionOptionRepository;

    @Autowired
    private MockTestRepository mockTestRepository;

    @Autowired
    private ExamRepository examRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Override
    public BaseResponse<QuestionResponse> saveQuestion(QuestionRequest questionRequest) {
        if (questionRequest == null) {
            BaseResponse<QuestionResponse> baseResponse = new BaseResponse<>();
            baseResponse.setStatus(0);
            baseResponse.setCode(400);
            baseResponse.setMessage("Question request cannot be null");
            return baseResponse;
        }
        return saveQuestions(Collections.singletonList(questionRequest));
    }

    @Override
    @Transactional
    public BaseResponse<QuestionResponse> saveQuestions(List<QuestionRequest> questionRequests) {
        BaseResponse<QuestionResponse> baseResponse = new BaseResponse<>();
        try {
            if (questionRequests == null || questionRequests.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Question request list cannot be empty");
                return baseResponse;
            }

            List<QuestionResponse> responseList = new ArrayList<>();

            for (QuestionRequest questionRequest : questionRequests) {
                if (questionRequest.getMockTestId() == null) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Mock Test ID is required");
                    return baseResponse;
                }

                if (questionRequest.getQuestionText() == null || questionRequest.getQuestionText().trim().isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(400);
                    baseResponse.setMessage("Question text is required");
                    return baseResponse;
                }

                Optional<MockTestMaster> mockTestOptional = mockTestRepository.findById(questionRequest.getMockTestId());
                if (mockTestOptional.isEmpty()) {
                    baseResponse.setStatus(0);
                    baseResponse.setCode(404);
                    baseResponse.setMessage("Mock test not found with ID: " + questionRequest.getMockTestId());
                    return baseResponse;
                }

                QuestionMaster questionMaster;
                if (questionRequest.getId() != null) {
                    questionMaster = questionRepository.findById(questionRequest.getId()).orElse(null);
                    if (questionMaster == null) {
                        baseResponse.setStatus(0);
                        baseResponse.setCode(404);
                        baseResponse.setMessage("Question not found with ID: " + questionRequest.getId());
                        return baseResponse;
                    }
                } else {
                    questionMaster = new QuestionMaster();
                }

                questionMaster.setMockTest(mockTestOptional.get());

                // Assign Subject if provided
                if (questionRequest.getSubjectId() != null) {
                    Optional<Subject> subjectOptional = subjectRepository.findById(questionRequest.getSubjectId());
                    subjectOptional.ifPresent(questionMaster::setSubject);
                }

                questionMaster.setQuestionText(questionRequest.getQuestionText().trim());
                questionMaster.setQuestionType(questionRequest.getQuestionType() != null ? questionRequest.getQuestionType() : "TEXT");
                questionMaster.setDifficultyLevel(questionRequest.getDifficultyLevel() != null ? questionRequest.getDifficultyLevel() : "MEDIUM");
                questionMaster.setAnswerType(questionRequest.getAnswerType() != null ? questionRequest.getAnswerType() : "SINGLE_CHOICE");
                questionMaster.setMarks(questionRequest.getMarks() != null ? questionRequest.getMarks() : 1.0);
                questionMaster.setNegativeMarks(questionRequest.getNegativeMarks() != null ? questionRequest.getNegativeMarks() : 0.25);
                questionMaster.setExplanation(questionRequest.getExplanation());
                questionMaster.setQuestionImageUrl(questionRequest.getQuestionImageUrl());
                questionMaster.setActive(questionRequest.getActive() != null ? questionRequest.getActive() : true);

                QuestionMaster savedQuestion = questionRepository.save(questionMaster);

                // Handle options
                if (questionRequest.getOptions() != null && !questionRequest.getOptions().isEmpty()) {
                    // If update, clean existing options first
                    if (questionRequest.getId() != null) {
                        questionOptionRepository.deleteByQuestion_Id(savedQuestion.getId());
                    }

                    List<QuestionOption> optionsToSave = new ArrayList<>();
                    int order = 1;
                    for (QuestionOptionRequest optReq : questionRequest.getOptions()) {
                        QuestionOption option = new QuestionOption();
                        option.setQuestion(savedQuestion);
                        option.setOptionCode(optReq.getOptionCode() != null ? optReq.getOptionCode().trim().toUpperCase() : String.valueOf((char) ('A' + order - 1)));
                        option.setOptionText(optReq.getOptionText() != null ? optReq.getOptionText().trim() : "");
                        option.setOptionImageUrl(optReq.getOptionImageUrl());
                        option.setCorrect(optReq.getIsCorrect() != null ? optReq.getIsCorrect() : false);
                        option.setDisplayOrder(optReq.getDisplayOrder() != null ? optReq.getDisplayOrder() : order++);
                        option.setActive(true);
                        optionsToSave.add(option);
                    }
                    questionOptionRepository.saveAll(optionsToSave);
                }

                responseList.add(mapToQuestionResponse(savedQuestion));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage(responseList.size() + " question(s) saved successfully");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in saveQuestions: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to save questions: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<QuestionResponse> getQuestionById(Long id) {
        BaseResponse<QuestionResponse> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Question ID cannot be null");
                return baseResponse;
            }

            Optional<QuestionMaster> questionOptional = questionRepository.findById(id);
            if (questionOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Question not found with ID: " + id);
                return baseResponse;
            }

            QuestionResponse response = mapToQuestionResponse(questionOptional.get());

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Question fetched successfully");
            baseResponse.setData(Collections.singletonList(response));

        } catch (Exception exception) {
            log.error("Exception in getQuestionById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch question: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<QuestionResponse> getQuestionsByMockTestId(Long mockTestId) {
        BaseResponse<QuestionResponse> baseResponse = new BaseResponse<>();
        try {
            if (mockTestId == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Mock Test ID cannot be null");
                return baseResponse;
            }

            List<QuestionMaster> questions = questionRepository.findByMockTest_IdAndActiveTrue(mockTestId);
            List<QuestionResponse> responseList = new ArrayList<>();
            for (QuestionMaster q : questions) {
                responseList.add(mapToQuestionResponse(q));
            }

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Questions for mock test ID " + mockTestId + " fetched successfully (" + responseList.size() + " questions)");
            baseResponse.setData(responseList);

        } catch (Exception exception) {
            log.error("Exception in getQuestionsByMockTestId: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to fetch questions: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public BaseResponse<String> deleteQuestionById(Long id) {
        BaseResponse<String> baseResponse = new BaseResponse<>();
        try {
            if (id == null) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Question ID cannot be null");
                return baseResponse;
            }

            Optional<QuestionMaster> questionOptional = questionRepository.findById(id);
            if (questionOptional.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(404);
                baseResponse.setMessage("Question not found with ID: " + id);
                return baseResponse;
            }

            QuestionMaster question = questionOptional.get();
            question.setActive(false);
            questionRepository.save(question);

            baseResponse.setStatus(1);
            baseResponse.setCode(200);
            baseResponse.setMessage("Question deactivated successfully");
            baseResponse.setData(Collections.singletonList("Question with ID " + id + " has been deactivated successfully"));

        } catch (Exception exception) {
            log.error("Exception in deleteQuestionById: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to delete question: " + exception.getMessage());
            baseResponse.setData(null);
        }
        return baseResponse;
    }

    @Override
    public byte[] generateExcelTemplate() {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            // Sheet 1: Template for Entering Questions
            Sheet sheet = workbook.createSheet("Questions_Template");

            // Sheet 2: Lookup Data for dropdowns
            Sheet lookupSheet = workbook.createSheet("Lookup_Data");

            // Header Style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.WHITE.getIndex());
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.ROYAL_BLUE.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setAlignment(HorizontalAlignment.CENTER);
            headerStyle.setVerticalAlignment(VerticalAlignment.CENTER);

            // Columns
            String[] headers = {
                    "Exam (Dropdown)",
                    "Mock Test (Dropdown)",
                    "Subject Name",
                    "Question Text*",
                    "Question Type",
                    "Difficulty Level",
                    "Answer Type",
                    "Option A*",
                    "Option B*",
                    "Option C*",
                    "Option D*",
                    "Correct Option*",
                    "Marks",
                    "Negative Marks",
                    "Explanation"
            };

            Row headerRow = sheet.createRow(0);
            headerRow.setHeightInPoints(28);
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            // Populate Lookup Data
            List<ExamMaster> exams = examRepository.findByActiveTrue();
            List<MockTestMaster> mockTests = mockTestRepository.findByActiveTrue();
            List<Subject> subjects = subjectRepository.findByActiveTrue();

            Row lookupHeader = lookupSheet.createRow(0);
            lookupHeader.createCell(0).setCellValue("Exams");
            lookupHeader.createCell(1).setCellValue("Mock Tests");
            lookupHeader.createCell(2).setCellValue("Subjects");

            int maxRows = Math.max(exams.size(), Math.max(mockTests.size(), subjects.size()));
            for (int i = 0; i < maxRows; i++) {
                Row row = lookupSheet.createRow(i + 1);
                if (i < exams.size()) {
                    ExamMaster e = exams.get(i);
                    row.createCell(0).setCellValue(e.getId() + " - " + e.getExamName());
                }
                if (i < mockTests.size()) {
                    MockTestMaster mt = mockTests.get(i);
                    row.createCell(1).setCellValue(mt.getId() + " - " + mt.getTestName());
                }
                if (i < subjects.size()) {
                    Subject s = subjects.get(i);
                    row.createCell(2).setCellValue(s.getId() + " - " + s.getSubjectName());
                }
            }

            DataValidationHelper validationHelper = sheet.getDataValidationHelper();

            // Dropdown 1: Exam (Col 0)
            if (!exams.isEmpty()) {
                DataValidationConstraint examConstraint = validationHelper.createFormulaListConstraint("Lookup_Data!$A$2:$A$" + (exams.size() + 1));
                CellRangeAddressList examRange = new CellRangeAddressList(1, 1000, 0, 0);
                DataValidation examValidation = validationHelper.createValidation(examConstraint, examRange);
                examValidation.setShowErrorBox(true);
                sheet.addValidationData(examValidation);
            }

            // Dropdown 2: Mock Test (Col 1)
            if (!mockTests.isEmpty()) {
                DataValidationConstraint mockConstraint = validationHelper.createFormulaListConstraint("Lookup_Data!$B$2:$B$" + (mockTests.size() + 1));
                CellRangeAddressList mockRange = new CellRangeAddressList(1, 1000, 1, 1);
                DataValidation mockValidation = validationHelper.createValidation(mockConstraint, mockRange);
                mockValidation.setShowErrorBox(true);
                sheet.addValidationData(mockValidation);
            }

            // Dropdown 3: Question Type (Col 4)
            DataValidationConstraint typeConstraint = validationHelper.createExplicitListConstraint(new String[]{"TEXT", "IMAGE"});
            sheet.addValidationData(validationHelper.createValidation(typeConstraint, new CellRangeAddressList(1, 1000, 4, 4)));

            // Dropdown 4: Difficulty Level (Col 5)
            DataValidationConstraint diffConstraint = validationHelper.createExplicitListConstraint(new String[]{"EASY", "MEDIUM", "HARD"});
            sheet.addValidationData(validationHelper.createValidation(diffConstraint, new CellRangeAddressList(1, 1000, 5, 5)));

            // Dropdown 5: Answer Type (Col 6)
            DataValidationConstraint ansTypeConstraint = validationHelper.createExplicitListConstraint(new String[]{"SINGLE_CHOICE", "MULTIPLE_CHOICE"});
            sheet.addValidationData(validationHelper.createValidation(ansTypeConstraint, new CellRangeAddressList(1, 1000, 6, 6)));

            // Dropdown 6: Correct Option (Col 11)
            DataValidationConstraint correctOptionConstraint = validationHelper.createExplicitListConstraint(new String[]{"A", "B", "C", "D"});
            sheet.addValidationData(validationHelper.createValidation(correctOptionConstraint, new CellRangeAddressList(1, 1000, 11, 11)));

            // Pre-fill Sample Row 1
            Row sampleRow1 = sheet.createRow(1);
            sampleRow1.createCell(0).setCellValue(!exams.isEmpty() ? exams.get(0).getId() + " - " + exams.get(0).getExamName() : "1 - SSC CGL");
            sampleRow1.createCell(1).setCellValue(!mockTests.isEmpty() ? mockTests.get(0).getId() + " - " + mockTests.get(0).getTestName() : "1 - SSC CGL Full Test 1");
            sampleRow1.createCell(2).setCellValue("General Awareness");
            sampleRow1.createCell(3).setCellValue("What is the capital of India?");
            sampleRow1.createCell(4).setCellValue("TEXT");
            sampleRow1.createCell(5).setCellValue("EASY");
            sampleRow1.createCell(6).setCellValue("SINGLE_CHOICE");
            sampleRow1.createCell(7).setCellValue("Mumbai");
            sampleRow1.createCell(8).setCellValue("New Delhi");
            sampleRow1.createCell(9).setCellValue("Kolkata");
            sampleRow1.createCell(10).setCellValue("Chennai");
            sampleRow1.createCell(11).setCellValue("B");
            sampleRow1.createCell(12).setCellValue(2.0);
            sampleRow1.createCell(13).setCellValue(0.5);
            sampleRow1.createCell(14).setCellValue("New Delhi is the official capital of India.");

            // Auto-size columns
            for (int i = 0; i < headers.length; i++) {
                sheet.setColumnWidth(i, 20 * 256);
            }
            sheet.setColumnWidth(3, 40 * 256); // Question text wider
            sheet.setColumnWidth(14, 30 * 256); // Explanation wider

            workbook.write(out);
            return out.toByteArray();

        } catch (Exception e) {
            log.error("Error generating Excel template: ", e);
            throw new RuntimeException("Could not generate Excel template: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public BaseResponse<BulkUploadResponse> uploadQuestionsExcel(MultipartFile file, Long defaultMockTestId) {
        BaseResponse<BulkUploadResponse> baseResponse = new BaseResponse<>();
        BulkUploadResponse bulkResponse = new BulkUploadResponse();

        try {
            if (file == null || file.isEmpty()) {
                baseResponse.setStatus(0);
                baseResponse.setCode(400);
                baseResponse.setMessage("Uploaded file cannot be empty");
                baseResponse.setData(Collections.singletonList(bulkResponse));
                return baseResponse;
            }

            try (InputStream is = file.getInputStream(); Workbook workbook = WorkbookFactory.create(is)) {
                Sheet sheet = workbook.getSheetAt(0);

                int totalRows = 0;
                int successCount = 0;
                int failureCount = 0;
                List<String> errors = new ArrayList<>();

                for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                    Row row = sheet.getRow(r);
                    if (row == null || isRowEmpty(row)) {
                        continue;
                    }
                    totalRows++;

                    try {
                        // 1. Mock Test identification
                        Long mockTestId = defaultMockTestId;
                        String mockTestCellVal = getCellValueAsString(row.getCell(1));
                        if (mockTestCellVal != null && !mockTestCellVal.trim().isEmpty()) {
                            Long parsedId = parseIdFromDropdownString(mockTestCellVal);
                            if (parsedId != null) {
                                mockTestId = parsedId;
                            }
                        }

                        if (mockTestId == null) {
                            throw new IllegalArgumentException("Mock Test ID not specified in row or default parameter");
                        }

                        Optional<MockTestMaster> mockTestOpt = mockTestRepository.findById(mockTestId);
                        if (mockTestOpt.isEmpty()) {
                            throw new IllegalArgumentException("Mock Test with ID " + mockTestId + " does not exist");
                        }
                        MockTestMaster mockTest = mockTestOpt.get();

                        // 2. Question Text
                        String questionText = getCellValueAsString(row.getCell(3));
                        if (questionText == null || questionText.trim().isEmpty()) {
                            throw new IllegalArgumentException("Question text is empty");
                        }

                        // 3. Question Metadata
                        String qType = getCellValueAsString(row.getCell(4));
                        String diff = getCellValueAsString(row.getCell(5));
                        String ansType = getCellValueAsString(row.getCell(6));

                        // 4. Options
                        String optA = getCellValueAsString(row.getCell(7));
                        String optB = getCellValueAsString(row.getCell(8));
                        String optC = getCellValueAsString(row.getCell(9));
                        String optD = getCellValueAsString(row.getCell(10));
                        String correctOpt = getCellValueAsString(row.getCell(11));

                        if (optA == null || optB == null || optA.trim().isEmpty() || optB.trim().isEmpty()) {
                            throw new IllegalArgumentException("At least Option A and Option B must be provided");
                        }
                        if (correctOpt == null || correctOpt.trim().isEmpty()) {
                            throw new IllegalArgumentException("Correct Option (A, B, C, D) is required");
                        }
                        String correctOptionNormalized = correctOpt.trim().toUpperCase();

                        // 5. Marks & Explanation
                        Double marks = getCellValueAsDouble(row.getCell(12), 1.0);
                        Double negMarks = getCellValueAsDouble(row.getCell(13), 0.25);
                        String explanation = getCellValueAsString(row.getCell(14));

                        // 6. Subject (Optional)
                        String subjectCellVal = getCellValueAsString(row.getCell(2));
                        Subject subject = null;
                        if (subjectCellVal != null && !subjectCellVal.trim().isEmpty()) {
                            Long subId = parseIdFromDropdownString(subjectCellVal);
                            if (subId != null) {
                                subject = subjectRepository.findById(subId).orElse(null);
                            }
                            if (subject == null) {
                                subject = subjectRepository.findBySubjectNameIgnoreCase(subjectCellVal.trim()).orElse(null);
                            }
                        }

                        // Save Question
                        QuestionMaster q = new QuestionMaster();
                        q.setMockTest(mockTest);
                        q.setSubject(subject);
                        q.setQuestionText(questionText.trim());
                        q.setQuestionType(qType != null && !qType.trim().isEmpty() ? qType.trim().toUpperCase() : "TEXT");
                        q.setDifficultyLevel(diff != null && !diff.trim().isEmpty() ? diff.trim().toUpperCase() : "MEDIUM");
                        q.setAnswerType(ansType != null && !ansType.trim().isEmpty() ? ansType.trim().toUpperCase() : "SINGLE_CHOICE");
                        q.setMarks(marks);
                        q.setNegativeMarks(negMarks);
                        q.setExplanation(explanation);
                        q.setActive(true);

                        QuestionMaster savedQ = questionRepository.save(q);

                        // Save Options
                        List<QuestionOption> optionsList = new ArrayList<>();
                        optionsList.add(createOption(savedQ, "A", optA, correctOptionNormalized.contains("A"), 1));
                        optionsList.add(createOption(savedQ, "B", optB, correctOptionNormalized.contains("B"), 2));
                        if (optC != null && !optC.trim().isEmpty()) {
                            optionsList.add(createOption(savedQ, "C", optC, correctOptionNormalized.contains("C"), 3));
                        }
                        if (optD != null && !optD.trim().isEmpty()) {
                            optionsList.add(createOption(savedQ, "D", optD, correctOptionNormalized.contains("D"), 4));
                        }
                        questionOptionRepository.saveAll(optionsList);

                        successCount++;

                    } catch (Exception rowEx) {
                        failureCount++;
                        String errMsg = "Row " + (r + 1) + ": " + rowEx.getMessage();
                        errors.add(errMsg);
                        log.warn(errMsg);
                    }
                }

                bulkResponse.setTotalRows(totalRows);
                bulkResponse.setSuccessCount(successCount);
                bulkResponse.setFailureCount(failureCount);
                bulkResponse.setErrorMessages(errors);

                baseResponse.setStatus(successCount > 0 ? 1 : 0);
                baseResponse.setCode(successCount > 0 ? 200 : 400);
                baseResponse.setMessage("Excel processed: " + successCount + " questions imported successfully, " + failureCount + " failed.");
                baseResponse.setData(Collections.singletonList(bulkResponse));
            }

        } catch (Exception exception) {
            log.error("Exception in uploadQuestionsExcel: ", exception);
            baseResponse.setStatus(0);
            baseResponse.setCode(500);
            baseResponse.setMessage("Failed to process Excel file: " + exception.getMessage());
            baseResponse.setData(Collections.singletonList(bulkResponse));
        }
        return baseResponse;
    }

    private QuestionOption createOption(QuestionMaster q, String code, String text, boolean isCorrect, int order) {
        QuestionOption opt = new QuestionOption();
        opt.setQuestion(q);
        opt.setOptionCode(code);
        opt.setOptionText(text.trim());
        opt.setCorrect(isCorrect);
        opt.setDisplayOrder(order);
        opt.setActive(true);
        return opt;
    }

    private QuestionResponse mapToQuestionResponse(QuestionMaster q) {
        QuestionResponse response = new QuestionResponse();
        response.setId(q.getId());

        if (q.getMockTest() != null) {
            response.setMockTestId(q.getMockTest().getId());
            response.setTestName(q.getMockTest().getTestName());
            if (q.getMockTest().getExam() != null) {
                response.setExamId(q.getMockTest().getExam().getId());
                response.setExamName(q.getMockTest().getExam().getExamName());
            }
        }

        if (q.getSubject() != null) {
            response.setSubjectId(q.getSubject().getId());
            response.setSubjectName(q.getSubject().getSubjectName());
        }

        response.setQuestionText(q.getQuestionText());
        response.setQuestionType(q.getQuestionType());
        response.setDifficultyLevel(q.getDifficultyLevel());
        response.setAnswerType(q.getAnswerType());
        response.setMarks(q.getMarks());
        response.setNegativeMarks(q.getNegativeMarks());
        response.setExplanation(q.getExplanation());
        response.setQuestionImageUrl(q.getQuestionImageUrl());
        response.setActive(q.getActive());
        response.setCreatedAt(q.getCreatedAt());
        response.setUpdatedAt(q.getUpdatedAt());

        // Map options
        List<QuestionOption> options = questionOptionRepository.findByQuestion_Id(q.getId());
        List<QuestionOptionResponse> optionResponses = new ArrayList<>();
        for (QuestionOption opt : options) {
            QuestionOptionResponse optResp = new QuestionOptionResponse();
            optResp.setId(opt.getId());
            optResp.setOptionCode(opt.getOptionCode());
            optResp.setOptionText(opt.getOptionText());
            optResp.setOptionImageUrl(opt.getOptionImageUrl());
            optResp.setDisplayOrder(opt.getDisplayOrder());
            optionResponses.add(optResp);
        }
        response.setOptions(optionResponses);

        return response;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return null;
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double val = cell.getNumericCellValue();
                    if (val == (long) val) {
                        return String.valueOf((long) val);
                    }
                    return String.valueOf(val);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    return cell.getStringCellValue();
                } catch (Exception e) {
                    return String.valueOf(cell.getNumericCellValue());
                }
            default:
                return "";
        }
    }

    private Double getCellValueAsDouble(Cell cell, Double defaultValue) {
        if (cell == null) return defaultValue;
        try {
            if (cell.getCellType() == CellType.NUMERIC) {
                return cell.getNumericCellValue();
            } else if (cell.getCellType() == CellType.STRING) {
                return Double.parseDouble(cell.getStringCellValue().trim());
            }
        } catch (Exception ignored) {
        }
        return defaultValue;
    }

    private Long parseIdFromDropdownString(String val) {
        if (val == null || val.trim().isEmpty()) return null;
        val = val.trim();
        try {
            if (val.contains("-")) {
                String firstPart = val.split("-")[0].trim();
                return Long.parseLong(firstPart);
            }
            return Long.parseLong(val);
        } catch (Exception e) {
            return null;
        }
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK && !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }
}

