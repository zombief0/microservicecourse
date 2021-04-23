package com.example.usersmicroservices.services;

import com.example.usersmicroservices.data.UserEntity;
import com.example.usersmicroservices.data.UsersRepository;
import com.example.usersmicroservices.models.AlbumResponseModel;
import com.example.usersmicroservices.models.UserResponseModel;
import com.example.usersmicroservices.shared.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {
    private final UsersRepository usersRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final RestTemplate restTemplate;
    private final Environment environment;
    @Override
    public UserDto createUser(UserDto userDetails) {
        userDetails.setUserId(UUID.randomUUID().toString());
        userDetails.setEncryptedPassword(bCryptPasswordEncoder.encode(userDetails.getPassword()));
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        UserEntity entity = modelMapper.map(userDetails, UserEntity.class);
        usersRepository.save(entity);

        UserDto rValue = modelMapper.map(entity, UserDto.class);
        return rValue;
    }

    @Override
    public UserDto getUserDetailsByEmail(String email) {
        UserEntity u = usersRepository.findByEmail(email);
        if (u == null) {
            throw new UsernameNotFoundException(email);
        }

        return new ModelMapper().map(u, UserDto.class);
    }

    @Override
    public UserDto getUserByUserId(String userId) {
        UserEntity userEntity = usersRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found");
        }
        UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);
        String albumsUrl = String.format(environment.getProperty("albums.url"), userId);
        log.info("Before sending request");
        log.debug("fetching start");
        ResponseEntity<List<AlbumResponseModel>> albumListResponse = restTemplate.exchange(albumsUrl, HttpMethod.GET, null, new ParameterizedTypeReference<List<AlbumResponseModel>>() {
        });
        List<AlbumResponseModel> albums = albumListResponse.getBody();
        userDto.setAlbums(albums);
        return userDto;

    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        UserEntity u = usersRepository.findByEmail(s);
        if (u == null) {
            throw new UsernameNotFoundException(s);
        }
        return new User(u.getEmail(), u.getEncryptedPassword(), true, true, true, true, new ArrayList<>());
    }
}
