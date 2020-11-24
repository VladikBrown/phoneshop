<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="tags" tagdir="/WEB-INF/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>


<head>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css">
</head>
<tags:master pageTitle="Product details">
    <body>
    <nav class="navbar navbar-light bg-light">
        <a class="navbar-brand">Cart</a>
    </nav>
    <div class="d-flex">
        <div class="mr-auto p-2">
            <a href="${pageContext.request.contextPath}/productList"
               class="btn btn-outline-dark justify-content-lg-start">
                Back to Product List
            </a>
        </div>
        <div class="p-2">
            <a href="${pageContext.request.contextPath}/order"
               class="order-btn btn btn-outline-success justify-content-lg-end my-2 my-sm-0">
                Order
            </a>
        </div>
    </div>
    <table class="table">
        <thead>
        <tr>
            <th scope="col">Image</th>
            <th scope="col">Brand</th>
            <th scope="col">Model</th>
            <th scope="col">Color</th>
            <th scope="col">Display size</th>
            <th scope="col">Price</th>
            <th scope="col">Quantity</th>
            <th scope="col">Action</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="orderItem" items="${cart.items}" varStatus="statusOrderItems">
            <tr class="row-${statusOrderItems.index % 2 == 0 ? "even" : ""}">
                <th scope="row">
                    <img class="product-tile"
                         src="https://raw.githubusercontent.com/andrewosipenko/phoneshop-ext-images/master/${orderItem.phone.imageUrl}">
                </th>
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
                <td class="price">${orderItem.phone.price} $</td>
                <td style="max-width: 120px">
                    <fmt:formatNumber value="${orderItem.quantity}" var="quantity"/>
                    <c:set var="error" value="${errors[orderItem.phone.id]}"/>
                    <input id="quantity-${orderItem.phone.id}"
                           form="updateForm"
                           class="quantityInput quantity"
                           type="text"
                           name="quantity"
                           value="${not empty error ? paramValues['quantity'][statusOrderItems.index] : orderItem.quantity}"/>
                    <input type="hidden"
                           form="updateForm"
                           name="phoneId"
                           value="${orderItem.phone.id}"/>
                    <c:choose>
                        <c:when test="${not empty error}">
                            <div class="error">
                                    ${error}
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="success">
                                    ${empty errors ? "Successfully updated" : ""}
                            </div>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td>
                    <form action="${pageContext.servletContext.contextPath}/cart/${orderItem.phone.id}" method="post">
                        <button id="btn-addPhoneToCart-${orderItem.phone.id}"
                                type="submit"
                                class="btn btn-outline-danger">
                            Delete
                        </button>
                        <input type="hidden" name="_method" value="delete"/>
                    </form>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <div class="d-flex flex-row-reverse">
        <div class="p-2">
            <a href="${pageContext.request.contextPath}/order"
               class="order-btn btn btn-outline-success justify-content-lg-end my-2 my-sm-0">
                Order
            </a>
        </div>
        <div class="p-2">
            <button type="submit"
                    form="updateForm"
                    class="btn btn-outline-dark justify-content-lg-end my-2 my-lg-0">
                Update
            </button>
        </div>
    </div>

    <form method="post" action="${pageContext.request.contextPath}/cart" id="deleteForm">
        <input type="hidden" name="_method" value="delete"/>
    </form>

    <form method="post" action="${pageContext.request.contextPath}/cart" id="updateForm">
        <input type="hidden" name="_method" value="put"/>
    </form>

    </body>
</tags:master>