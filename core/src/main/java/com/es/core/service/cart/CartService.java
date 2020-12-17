package com.es.core.service.cart;

import com.es.core.model.entity.cart.Cart;
import com.es.core.service.order.OutOfStockException;

import javax.servlet.http.HttpSession;
import java.util.Map;

public interface CartService {

    Cart getCart(HttpSession httpSession);

    void addPhone(Cart cart, Long phoneId, Long quantity) throws OutOfStockException;

    Map<Long, String> update(Cart cart, Map<Long, Long> items, Map<Long, String> errors);

    void remove(Cart cart, Long phoneId);

    Map<Long, String> trimRedundantProducts(Cart cart);
}
