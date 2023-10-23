package com.collicode.api.dairylink.web.rest.dto;


import com.collicode.api.dairylink.domain.enums.FarmerStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FarmerDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String fullName;
    private String msisdn;
    private String email;
    private FarmerStatus farmerStatus;
    private String county;
    private String subCounty;
    private String ward;
    private String village;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastUpdatedAt;
    private String lastUpdatedBy;
}
