package org.example.langchain.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

@Data
public class UserDto {
    private Integer id;
    private String name;
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date birthday;
}