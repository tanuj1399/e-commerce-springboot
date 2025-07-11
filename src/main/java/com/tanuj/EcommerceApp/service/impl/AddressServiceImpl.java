package com.tanuj.EcommerceApp.service.impl;

import com.tanuj.EcommerceApp.dto.AddressDto;
import com.tanuj.EcommerceApp.dto.Response;
import com.tanuj.EcommerceApp.entity.Address;
import com.tanuj.EcommerceApp.entity.User;
import com.tanuj.EcommerceApp.repository.AddressRepo;
import com.tanuj.EcommerceApp.service.interf.AddressService;
import com.tanuj.EcommerceApp.service.interf.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {

    private final AddressRepo addressRepo;
    private final UserService userService;


    @Override
    public Response saveAndUpdateAddress(AddressDto addressDto) {

        User user = userService.getLoginUser();
        Address address = user.getAddress();

        if (address == null){
            address = new Address();
            address.setUser(user);
        }

        if (addressDto.getStreet() != null) address.setStreet(addressDto.getStreet());
        if (addressDto.getCity() != null) address.setCity(addressDto.getCity());
        if (addressDto.getCountry() != null) address.setCountry(addressDto.getCountry());
        if (addressDto.getState() != null) address.setState(addressDto.getState());
        if (addressDto.getZipCode() != null) address.setZipCode(addressDto.getZipCode());

        addressRepo.save(address);

        String message = (user.getAddress() == null) ? "Address successfully added" : "Address successfully updated";

        return Response.builder()
                .status(200)
                .message(message)
                .build();
    }
}
