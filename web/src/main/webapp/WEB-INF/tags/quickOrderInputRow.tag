<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="index" type="java.lang.Integer" required="true" %>
<%@ attribute name="rowResponse" type="com.es.phoneshop.web.controller.dto.QuickOrderEntryRowResponse"
              required="true" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="form-group row order-info-row">
    <input type="hidden"
           name="inputFormIndex"
           value="${index}"/>
    <span>
        <div>
            <label for="phoneModelInput-${index}">
                ${not empty rowResponse ? (not empty rowResponse.productCodeError ? rowResponse.productCodeError : "") : ""}
            </label>
        </div>
        <div>
             <input id="phoneModelInput-${index}" name="phoneModel"
                    value="${not empty rowResponse ? rowResponse.productCodeInput : ""}">
        </div>
    </span>

    <span style="padding-right: 100px">
        <div>
            <label for="quantityInput-${index}">
                ${not empty rowResponse ? (not empty rowResponse.quantityError ? rowResponse.quantityError : "") : ""}
            </label>
        </div>
        <div>
             <input id="quantityInput-${index}" name="quantity"
                    value="${not empty rowResponse ? rowResponse.quantityInput : ""}">
        </div>
    </span>
</div>