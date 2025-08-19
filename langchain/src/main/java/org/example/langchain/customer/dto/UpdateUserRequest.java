package org.example.langchain.customer.dto;

import lombok.Data;
import java.util.Date;

@Data
public class UpdateUserRequest {
    private String username;
    private String email;
    private Date birthday;
}