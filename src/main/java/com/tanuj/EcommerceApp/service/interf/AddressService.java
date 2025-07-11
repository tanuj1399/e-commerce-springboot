package com.tanuj.EcommerceApp.service.interf;

import com.tanuj.EcommerceApp.dto.AddressDto;
import com.tanuj.EcommerceApp.dto.Response;

public interface AddressService {

    Response saveAndUpdateAddress(AddressDto addressDto);
}
