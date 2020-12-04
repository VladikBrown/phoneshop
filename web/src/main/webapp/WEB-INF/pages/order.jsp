<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>


<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>


<tags:master pageTitle="Order">
    <c:if test="${not empty customerErrors or not empty customerInfo}">
        <div class="alert alert-danger" role="alert">
            Error occurred while placing order
        </div>
    </c:if>
    <body>
    <nav class="navbar navbar-light bg-light">
        <a class="navbar-brand">Order</a>
    </nav>
    <div class="d-flex">
        <div class="mr-auto p-2">
            <a href="${pageContext.request.contextPath}/cart"
               class="btn btn-outline-dark justify-content-lg-start">
                Back to Cart
            </a>
        </div>
    </div>
    <table class="table">
        <thead>
        <tr>
            <th scope="col">Brand</th>
            <th scope="col">Model</th>
            <th scope="col">Color</th>
            <th scope="col">Display size</th>
            <th scope="col">Quantity</th>
            <th scope="col">Price</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="orderItem" items="${order.orderItems}" varStatus="statusOrders">
            <c:set var="error" value="${quantityErrors[orderItem.phone.id]}"/>
            <tr class="row-${statusOrders.index % 2 == 0 ? "even" : ""}">
                <td>${orderItem.phone.brand}</td>
                <td>
                    <a href="${pageContext.servletContext.contextPath}/productDetails/${orderItem.phone.id}">
                            ${orderItem.phone.model}
                    </a>
                </td>
                <td>
                    <c:forEach var="color" items="${orderItem.phone.colors}" varStatus="statusColors">
                        <c:out value="${color.code}"/>
                        <c:if test="${not statusColors.last}">
                            <c:out value=","/>
                        </c:if>
                    </c:forEach>
                </td>
                <td>${orderItem.phone.displaySizeInches}</td>
                <td style="max-width: 120px">
                        ${orderItem.quantity}
                    <c:if test="${not empty error}">
                        <div class="error">
                                ${error}
                        </div>
                    </c:if>
                </td>
                <td class="price">${orderItem.phone.price} $</td>
            </tr>
        </c:forEach>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td>Subtotal</td>
            <td>${order.subtotal} $</td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td>Delivery</td>
            <td>${order.deliveryPrice} $</td>
        </tr>
        <tr>
            <td></td>
            <td></td>
            <td></td>
            <td></td>
            <td>TOTAL</td>
            <td>${order.totalPrice} $</td>
        </tr>
        </tbody>
    </table>

    <form method="post" action="${pageContext.request.contextPath}/order">
        <tags:orderFormRow name="firstName" label="First name" order="${order}" customer="${customerInfo}"
                           errors="${customerErrors}"/>
        <tags:orderFormRow name="lastName" label="Last name" order="${order}" customer="${customerInfo}"
                           errors="${customerErrors}"/>
        <tags:orderFormRow name="deliveryAddress" label="Address" order="${order}" customer="${customerInfo}"
                           errors="${customerErrors}"/>
        <tags:orderFormRow name="contactPhoneNo" label="Phone" order="${order}" customer="${customerInfo}"
                           errors="${customerErrors}"/>
        <div class="form-group row order-info-row">
            <textarea class="form-control col-sm-3" name="additionalInformation" rows="5"
                      placeholder="Additional information"></textarea>
        </div>
        <button type="submit" class="order-btn btn btn-outline-success">Order</button>
    </form>

    </body>
</tags:master>