package com.es.phoneshop.web.controller.dto;

public class QuickOrderEntryRow {

    private Long inputFormIndex;

    private String phoneModel;

    private String quantity;

    public Long getInputFormIndex() {
        return inputFormIndex;
    }

    public void setInputFormIndex(Long inputFormIndex) {
        this.inputFormIndex = inputFormIndex;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }
}
