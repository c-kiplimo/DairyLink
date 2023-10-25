package com.collicode.api.dairylink.web.rest.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestResponse {
    private boolean error;
    private String message;
}
