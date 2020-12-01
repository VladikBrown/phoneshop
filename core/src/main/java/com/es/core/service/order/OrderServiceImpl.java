package com.es.core.service.order;

import com.es.core.model.DAO.order.OrderDao;
import com.es.core.model.DAO.stock.StockDao;
import com.es.core.model.entity.cart.Cart;
import com.es.core.model.entity.order.Order;
import com.es.core.model.entity.order.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
        order.setSecureId(UUID.randomUUID().toString());
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
