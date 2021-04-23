package com.example.usersmicroservices.models;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Getter
@Setter
public class CreateUserRequestModel {

    @NotNull(message = "First name cannot be null")
    @Size(min = 2, message = "First Name must not be less than 2 characters")
    private String firstName;

    @NotNull(message = "Last name cannot be null")
    @Size(min = 2, message = "Last name must not be less than 2 characters")
    private String lastName;

    @NotNull(message = "password cannot be null")
    @Size(min = 8, max = 16, message = "Password must be greater than 8 characters and less than 16")
    private String password;

    @NotNull(message = "email cannot be null")
    @Email
    private String email;
}
