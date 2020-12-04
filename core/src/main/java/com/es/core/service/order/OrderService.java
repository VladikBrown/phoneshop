package com.es.core.service.order;

import com.es.core.model.entity.cart.Cart;
import com.es.core.model.entity.order.Order;
import com.es.core.model.entity.order.OrderStatus;

public interface OrderService {

    Order createOrder(Cart cart);

    void placeOrder(Order order) throws OutOfStockException;

    void changeOrderStatus(OrderStatus orderStatus, Order order);
}
