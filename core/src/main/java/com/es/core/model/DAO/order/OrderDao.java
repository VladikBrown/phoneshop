package com.es.core.model.DAO.order;

import com.es.core.model.entity.order.Order;

import java.util.List;
import java.util.Optional;

public interface OrderDao {

    Optional<Order> getBySecureId(String key);

    void save(Order order);

    List<Order> findAll();

    Optional<Order> getByOrderNumber(Long orderId);
}
