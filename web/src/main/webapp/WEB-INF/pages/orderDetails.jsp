<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jstl/fmt" %>

<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>


<tags:master pageTitle="Order Overview">
    <body>
    <nav class="navbar navbar-light bg-light">
        <div class="navbar-brand">
            <h4>
                Order number: ${order.id}
            </h4>
        </div>
        <div class="navbar-brand">
            <h4>
                Order status: ${order.orderStatus}
            </h4>
        </div>
    </nav>
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
            <tr class="row-${statusOrders.index % 2 == 0 ? "even" : ""}">
                <td>${orderItem.phone.brand}</td>
                <td>${orderItem.phone.model}</td>
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

    <tags:orderOverviewRow name="firstName" label="First name" order="${order}"/>
    <tags:orderOverviewRow name="lastName" label="Last name" order="${order}"/>
    <tags:orderOverviewRow name="deliveryAddress" label="Address" order="${order}"/>
    <tags:orderOverviewRow name="contactPhoneNo" label="Phone" order="${order}"/>
    <div class="form-group">
        <div class="col-sm-2">
                ${order.additionalInformation}
        </div>
    </div>

    <div class="d-flex">
        <nav class="navbar navbar-light bg-white">
            <ul class="navbar-nav mr-auto">
                <li>
                    <button form="backForm"
                            style="padding-left: 30px; padding-right: 30px"
                            class="btn btn-outline-dark justify-content-lg-start">
                        Back
                    </button>
                    <form method="get" action="${pageContext.request.contextPath}/admin/orders" id="backForm"></form>
                </li>
                <li class="nav-item active">
                    <tags:changeStatusButton label="${order.orderStatus.toString() == 'REJECTED' ? 'New' : 'Rejected'}"
                                             orderStatus="${order.orderStatus.toString() == 'REJECTED' ? 'NEW' : 'REJECTED'}"
                                             order="${order}"/>
                    <tags:changeStatusButton
                            label="${order.orderStatus.toString() == 'DELIVERED' ? 'New' : 'Delivered'}"
                            orderStatus="${order.orderStatus.toString() == 'DELIVERED' ? 'NEW' : 'DELIVERED'}"
                            order="${order}"/>
                </li>
                <li class="nav-item active">

                </li>
            </ul>
        </nav>
    </div>

    </body>
</tags:master>