<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="name" required="true" %>
<%@ attribute name="label" required="true" %>
<%@ attribute name="order" required="true" type="com.es.core.model.entity.order.Order" %>
<%@ attribute name="customer" required="true" type="com.es.phoneshop.web.controller.dto.CustomerInfoDto" %>
<%@ attribute name="errors" required="true" type="java.util.Map" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<div class="form-group row order-info-row">
    <c:set var="error" value="${errors[name]}"/>
    <label for="${name}" class="col-sm-1">${label}*</label>
    <div class="col-sm-2">
        <input type="text" class="form-control" id="${name}" name="${name}"
               value="${not empty error ? customer[name] : order[name]}">
    </div>
    <c:if test="${not empty errors}">
        <div class="error">
                ${error}
        </div>
    </c:if>
</div>