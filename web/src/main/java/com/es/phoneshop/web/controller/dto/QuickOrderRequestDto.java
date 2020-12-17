package com.es.phoneshop.web.controller.dto;

import org.apache.commons.collections.FactoryUtils;
import org.apache.commons.collections.list.LazyList;

import java.util.ArrayList;
import java.util.List;

public class QuickOrderRequestDto {

    private List<QuickOrderEntryRow> quickOrderEntryRows;

    public QuickOrderRequestDto() {
        quickOrderEntryRows = LazyList.decorate(new ArrayList<QuickOrderEntryRow>(),
                FactoryUtils.instantiateFactory(QuickOrderEntryRow.class));
    }

    public List<QuickOrderEntryRow> getQuickOrderEntryRows() {
        return quickOrderEntryRows;
    }

    public void setQuickOrderEntryRows(List<QuickOrderEntryRow> quickOrderEntryRows) {
        this.quickOrderEntryRows = quickOrderEntryRows;
    }
}
