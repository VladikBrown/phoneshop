package com.es.phoneshop.web.controller.validation;

import com.es.phoneshop.web.controller.dto.QuickOrderRequestDto;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@Service
public class QuickOrderEntryDtoValidator implements Validator {

    @Override
    public boolean supports(Class<?> aClass) {
        return QuickOrderRequestDto.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        QuickOrderRequestDto quickOrderRequestDto = ((QuickOrderRequestDto) o);
        for (var row : quickOrderRequestDto.getQuickOrderEntryRows()) {
            var index = row.getInputFormIndex();
            var phoneModel = row.getPhoneModel();
            var quantity = row.getQuantity();

            if (StringUtils.isEmpty(StringUtils.trimAllWhitespace(phoneModel))
                    && (StringUtils.isEmpty(StringUtils.trimAllWhitespace(quantity)))) {
                continue;
            }

            if (isEmpty(trimAllWhitespace(phoneModel)) && !isEmpty(trimAllWhitespace(quantity))) {
                errors.rejectValue("quickOrderEntryRows[" + index + "].phoneModel", "commonForms.emptyField",
                        "both fields must contain values");
                continue;
            }
            if (isEmpty(trimAllWhitespace(quantity)) && !isEmpty(trimAllWhitespace(phoneModel))) {
                errors.rejectValue("quickOrderEntryRows[" + index + "].quantity", "commonForms.emptyField",
                        "both fields must contain values");
                continue;
            }

            try {
                Long.parseLong(quantity);
            } catch (NumberFormatException e) {
                errors.rejectValue("quickOrderEntryRows[" + index + "].quantity", "quantity.notNumber",
                        "Not a number");
            }
        }
    }
}
