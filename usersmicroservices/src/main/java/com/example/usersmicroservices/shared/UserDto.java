package com.example.usersmicroservices.shared;

import com.example.usersmicroservices.models.AlbumResponseModel;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
    private String userId;
    private String password;
    private String encryptedPassword;

    List<AlbumResponseModel> albums;
}
