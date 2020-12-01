package com.es.core.service.order;

import com.es.core.model.DAO.order.OrderDao;
import com.es.core.model.DAO.stock.StockDao;
import com.es.core.model.entity.cart.Cart;
import com.es.core.model.entity.order.Order;
import com.es.core.model.entity.order.OrderItem;
import com.es.core.model.entity.order.OrderStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@PropertySource("classpath:/config/application.properties")
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private StockDao stockDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private Environment environment;

    @Override
    public Order createOrder(Cart cart) {
        Order order = new Order();
        order.setOrderItems(getOrderItems(cart, order));
        calculateAndSetPriceInfo(order, cart);
        return order;
    }

    @Override
    public void placeOrder(Order order) throws OutOfStockException {
        for (var orderItem : order.getOrderItems()) {
            if (!isInStock(orderItem)) {
                throw new OutOfStockException();
            }
        }
        order.setOrderStatus(OrderStatus.NEW);
        order.setSecureId(UUID.randomUUID().toString());
        order.setOrderDate(LocalDate.now());
        orderDao.save(order);
    }

    private void calculateAndSetPriceInfo(Order order, Cart cart) {
        order.setDeliveryPrice(new BigDecimal(environment.getProperty("delivery.price")));
        order.setSubtotal(cart.getTotalCost());
        order.setTotalPrice(order.getSubtotal().add(order.getDeliveryPrice()));
    }

    public boolean isInStock(OrderItem orderItem) {
        return stockDao
                .get(orderItem.getPhone().getId())
                .filter(stock -> stock.getStock() >= orderItem.getQuantity())
                .isPresent();
    }

    @Override
    public void changeOrderStatus(OrderStatus orderStatus, Order order) {
        switch (orderStatus) {
            case NEW:
                setOrderStatusNew(order);
                break;
            case REJECTED:
                setOrderStatusRejected(order);
                break;
            case DELIVERED:
                setOrderStatusDelivered(order);
                break;
        }
    }

    private void setOrderStatusRejected(Order order) {
        order.setOrderStatus(OrderStatus.REJECTED);
        orderDao.save(order);
    }

    private void setOrderStatusDelivered(Order order) {
        order.setOrderStatus(OrderStatus.DELIVERED);
        orderDao.save(order);
    }

    private void setOrderStatusNew(Order order) {
        order.setOrderStatus(OrderStatus.NEW);
        orderDao.save(order);
    }

    private List<OrderItem> getOrderItems(Cart cart, Order order) {
        List<OrderItem> orderItems = new LinkedList<>();
        for (var item : cart.getItems()) {
            var orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setPhone(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItems.add(orderItem);
        }
        return orderItems;
    }

}
