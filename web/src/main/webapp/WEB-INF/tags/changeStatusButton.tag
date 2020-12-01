<%@ tag trimDirectiveWhitespaces="true" %>
<%@ attribute name="label" required="true" type="java.lang.String" %>
<%@ attribute name="orderStatus" required="true" type="java.lang.String" %>
<%@ attribute name="order" required="true" type="com.es.core.model.entity.order.Order" %>

<form action="${pageContext.request.contextPath}/admin/orders/${order.id}" method="post">
    <button style="padding-left: 30px; padding-right: 30px"
            type="submit"
            class="btn btn-outline-dark justify-content-lg-start">
        ${label}
    </button>
    <input type="hidden" name="orderStatus" value=${orderStatus}>
    <input type="hidden" name="_method" value="put"/>
</form>