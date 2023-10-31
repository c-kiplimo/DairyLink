package com.collicode.api.dairylink.web.rest.request;

import lombok.Data;

@Data
public class ForgotPassword {
private String msisdn;
private String otp;
private String password;
}
