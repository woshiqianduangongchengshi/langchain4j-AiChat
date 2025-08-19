package org.example.langchain.customer.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String type = "Bearer";
    private Integer id;
    private String name;

    public LoginResponse(String token, Integer id, String name) {
        this.token = token;
        this.id = id;
        this.name = name;
    }
}