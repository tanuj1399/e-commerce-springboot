package com.tanuj.EcommerceApp.service.interf;

import com.tanuj.EcommerceApp.dto.LoginRequest;
import com.tanuj.EcommerceApp.dto.Response;
import com.tanuj.EcommerceApp.dto.UserDto;
import com.tanuj.EcommerceApp.entity.User;

public interface UserService {

    Response registerUser(UserDto registrationRequest);
    Response loginUser(LoginRequest loginRequest);
    Response getAllUser();
    User getLoginUser();
    Response getUserInfoAndOrderHistory();

}
