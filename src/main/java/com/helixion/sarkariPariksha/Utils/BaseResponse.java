package com.helixion.sarkariPariksha.Utils;

import lombok.Data;

import java.util.List;

@Data
public class BaseResponse<T> {
    private Integer status;
    private Integer code;
    private String message;
    private List<T> data;
}
