package com.es.core.model.DAO.order;

import com.es.core.model.entity.order.Order;

import java.util.Optional;

public interface OrderDao {

    Optional<Order> get(String key);

    void save(Order order);

}
