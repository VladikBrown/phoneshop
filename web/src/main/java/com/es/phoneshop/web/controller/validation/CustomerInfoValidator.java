package com.es.phoneshop.web.controller.validation;

import com.es.phoneshop.web.controller.dto.CustomerInfoDto;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Service
public class CustomerInfoValidator implements Validator {
    @Override
    public boolean supports(Class<?> aClass) {
        return CustomerInfoDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        CustomerInfoDto customerInfoDto = ((CustomerInfoDto) o);

        if (isPlainStringInvalid(customerInfoDto.getFirstName())) {
            errors.reject("firstName", "The value is required");
        }
        if (isPlainStringInvalid(customerInfoDto.getLastName())) {
            errors.reject("lastName", "The value is required");
        }
        if (isPlainStringInvalid(customerInfoDto.getDeliveryAddress())) {
            errors.reject("deliveryAddress", "The value is required");
        }
        if (isPlainStringInvalid(customerInfoDto.getContactPhoneNo())) {
            errors.reject("contactPhoneNo", "The value is required");
        } else if (!belarusPhoneNumberValidationPredicate(customerInfoDto.getContactPhoneNo())) {
            errors.reject("contactPhoneNo", "Wrong number format, e.g. +375(77)7777777");
        }
    }

    private boolean belarusPhoneNumberValidationPredicate(String phoneNumber) {
        return phoneNumber.matches("^[+]375[(](17|29|33|44)[)]*[\\s./0-9]{7}$");
    }

    private boolean isPlainStringInvalid(String plainString) {
        return plainString == null || plainString.replaceAll("[\\s]{2,}", "").isEmpty();
    }
}
