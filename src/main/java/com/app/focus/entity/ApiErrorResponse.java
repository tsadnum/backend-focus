package com.app.focus.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class ApiErrorResponse implements Serializable {

    private static final long serialVersionUID = 1L;

    private int status;
    private String error;
    private String message;
    private LocalDateTime timestamp;
    private Map<String, String> fieldErrors;
    private String traceId;
    private String path;
}
