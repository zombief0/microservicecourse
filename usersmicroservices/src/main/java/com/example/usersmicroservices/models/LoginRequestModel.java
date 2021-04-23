package com.example.usersmicroservices.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequestModel {
    private String email;
    private String password;
}
