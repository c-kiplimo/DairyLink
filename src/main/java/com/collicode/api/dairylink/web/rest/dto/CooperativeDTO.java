package com.collicode.api.dairylink.web.rest.dto;


import com.collicode.api.dairylink.domain.enums.CooperativeStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CooperativeDTO {
    private Long id;
    private String cooperativeName;
    private String firstName;
    private String lastName;
    private String fullName;
    private String msisdn;
    private String email;
    private String county;
    private String subCounty;
    private String ward;
    private CooperativeStatus cooperativeStatus;
    private LocalDateTime createdAt;
    private String createdBy;
    private LocalDateTime lastUpdatedAt;
    private String lastUpdatedBy;


}
