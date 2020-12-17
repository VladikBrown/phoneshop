<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<%--<jsp:useBean id="responseDto" type="com.es.phoneshop.web.controller.dto.QuickOrderResponseDTO"/>--%>

<tags:master pageTitle="Quick Order">
    <div class="container">
        <br/>
        <c:if test="${not empty successProducts}">
            <c:forEach var="phoneModel" items="${successProducts}">
                <div class="success">
                        ${phoneModel} product added successfully
                </div>
            </c:forEach>
        </c:if>

        <c:if test="${errors.hasErrors()}">
            <div class="error">
                There was errors
            </div>
        </c:if>
        <table class="table" style="width: 50%">
            <tr>
                <th scope="col">
                    Product Code
                </th>
                <th scope="col">
                    Qty
                </th>
            </tr>
            <form:form action="${pageContext.request.contextPath}/quickOrderEntry" modelAttribute="quickOrderRequestDto"
                       method="post">
                <c:forEach begin="0" end="10" varStatus="loopStatus">
                    <c:set var="index">
                        ${loopStatus.index}
                    </c:set>
                    <tr>
                        <th scope="row" style="max-width: 20px">
                            <form:hidden path="quickOrderEntryRows[${index}].inputFormIndex" value="${index}"/>
                            <form:input id="inputModel-${index}" path="quickOrderEntryRows[${index}].phoneModel"/>
                            <div>
                                <form:errors path="quickOrderEntryRows[${index}].phoneModel" cssClass="error"/>
                            </div>
                        </th>
                        <th scope="row" style="max-width: 120px">
                            <form:input id="inputQuantity-${index}" path="quickOrderEntryRows[${index}].quantity"/>
                            <div>
                                <form:errors path="quickOrderEntryRows[${index}].quantity" cssClass="error"/>
                            </div>
                        </th>
                    </tr>
                </c:forEach>
                <tr>
                    <th>
                        <button class="btn btn-primary" type="submit">Add 2 Cart</button>
                    </th>
                    <th></th>
                </tr>
            </form:form>
        </table>
    </div>
</tags:master>