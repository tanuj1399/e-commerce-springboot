package com.tanuj.EcommerceApp.service.impl;

import com.tanuj.EcommerceApp.dto.LoginRequest;
import com.tanuj.EcommerceApp.dto.Response;
import com.tanuj.EcommerceApp.dto.UserDto;
import com.tanuj.EcommerceApp.entity.User;
import com.tanuj.EcommerceApp.enums.UserRole;
import com.tanuj.EcommerceApp.exception.InvalidCredentialsException;
import com.tanuj.EcommerceApp.exception.NotFoundException;
import com.tanuj.EcommerceApp.mapper.EntityDtoMapper;
import com.tanuj.EcommerceApp.repository.UserRepo;
import com.tanuj.EcommerceApp.security.JwtUtils;
import com.tanuj.EcommerceApp.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final EntityDtoMapper entityDtoMapper;


    @Override
    public Response registerUser(UserDto registrationRequest) {
        UserRole role = UserRole.USER;

        if (registrationRequest.getRole() != null && registrationRequest.getRole().equalsIgnoreCase("admin")){
            role = UserRole.ADMIN;
        }

        User user = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .phoneNumber((registrationRequest.getPhoneNumber()))
                .role(role)
                .build();

        User savedUser = userRepo.save(user);

        UserDto userDto = entityDtoMapper.mapUserToDtoBasic(user);

        return Response.builder()
                .status(200)
                .message("User Successfully Added")
                .user(userDto)
                .build();
    }

    @Override
    public Response loginUser(LoginRequest loginRequest) {

        User user = userRepo.findByEmail(loginRequest.getEmail()).orElseThrow(()-> new NotFoundException("Email not found"));
        if(!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new InvalidCredentialsException("Incorrect password");
        }

        String token = jwtUtils.generateToken(user);

        return Response.builder()
                .status(200)
                .message("User Successfully logged in")
                .token(token)
                .expirationTime("6 months")
                .role(user.getRole().name())
                .build();
    }

    @Override
    public Response getAllUser() {

        List<User> users = userRepo.findAll();
        List<UserDto> userDtos = users.stream()
                .map(entityDtoMapper::mapUserToDtoBasic)
                .toList();

        return Response.builder()
                .status(200)
                .message("Successfull")
                .userList(userDtos)
                .build();
    }

    @Override
    public User getLoginUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        log.info("User email is: "+ email);
        return userRepo.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User not found"));
    }

    @Override
    public Response getUserInfoAndOrderHistory() {
        User user = getLoginUser();
        UserDto userDto = entityDtoMapper.mapUserToDtoPlusAddressAndOrderHistory(user);

        return Response.builder()
                .status(200)
                .user(userDto)
                .build();
    }
}
