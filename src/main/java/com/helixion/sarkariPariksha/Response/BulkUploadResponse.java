package com.helixion.sarkariPariksha.Response;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BulkUploadResponse {
    private Integer totalRows = 0;
    private Integer successCount = 0;
    private Integer failureCount = 0;
    private List<String> errorMessages = new ArrayList<>();
}

