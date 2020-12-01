<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<tags:master pageTitle="Order list">
    <nav class="navbar navbar-light bg-light">
        <a class="navbar-brand">Orders</a>
    </nav>

    <table class="table">
        <thead>
        <tr>
            <th scope="col">
                Order number
            </th>
            <th scope="col">
                Customer
            </th>
            <th scope="col">
                Phone
            </th>
            <th scope="col">
                Address
            </th>
            <th scope="col">
                Date
            </th>
            <th scope="col">
                Total price
            </th>
            <th scope="col">
                Status
            </th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="order" items="${orders}" varStatus="statusOrders">
            <tr class="row-${statusOrders.index % 2 == 0 ? "even" : ""}">
                <th scope="row">
                    <a href="${pageContext.request.contextPath}/admin/orders/${order.id}">
                            ${order.id}
                    </a>
                </th>
                <td>
                        ${order.firstName}
                        ${order.lastName}
                </td>
                <td>
                        ${order.contactPhoneNo}
                </td>
                <td>
                        ${order.deliveryAddress}
                </td>
                <td>${order.orderDate}</td>
                <td class="price">${order.totalPrice} $</td>
                <td>
                        ${order.orderStatus}
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</tags:master>