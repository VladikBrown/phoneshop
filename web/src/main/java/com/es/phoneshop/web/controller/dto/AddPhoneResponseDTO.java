package com.es.phoneshop.web.controller.dto;

import java.io.Serializable;

public class AddPhoneResponseDTO implements Serializable {

    private MiniCartDTO miniCart;

    private String message;

    private boolean isSuccessful;

    public MiniCartDTO getMiniCart() {
        return miniCart;
    }

    public void setMiniCart(MiniCartDTO miniCart) {
        this.miniCart = miniCart;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setSuccessStatus(boolean status) {
        this.isSuccessful = status;
    }

    public boolean isSuccessful() {
        return isSuccessful;
    }
}