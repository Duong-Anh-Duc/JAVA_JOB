package com.example.cv.common.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PermissionView {
    @JsonProperty("_id")
    private String id;
    private String name;
    private String apiPath;
    private String method;
    private String module;
}
