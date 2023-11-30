package com.collicode.api.dairylink.web.rest.request;

import lombok.Data;

@Data
public class UserTokenConfirmRequest {
    private String username;
    private String password;
    private String  passwordConfirm;
    private Long id;
    private String token;
}
