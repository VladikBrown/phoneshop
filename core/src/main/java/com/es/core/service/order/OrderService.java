package com.es.core.service.order;

import com.es.core.model.entity.cart.Cart;
import com.es.core.model.entity.order.Order;

public interface OrderService {

    Order createOrder(Cart cart);

    void placeOrder(Order order) throws OutOfStockException;
}
