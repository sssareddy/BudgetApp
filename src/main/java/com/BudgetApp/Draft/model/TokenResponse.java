package com.BudgetApp.Draft.model;

import lombok.Data;

@Data
public class TokenResponse {
    private String access_token;
    private int expires_in;
    private String scope;
    private String token_type;
}

