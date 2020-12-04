package com.es.phoneshop.web.controller.pages.admin;

import com.es.core.model.DAO.order.OrderDao;
import com.es.core.model.entity.order.Order;
import com.es.core.model.entity.order.OrderStatus;
import com.es.core.service.order.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping(value = "/admin/orders")
public class OrdersPageController {

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private OrderService orderService;

    @GetMapping
    public String showOrders(Model model) {
        model.addAttribute("orders", orderDao.findAll());
        return "orderList";
    }

    @GetMapping(value = "/{orderId}")
    public String getOrder(Model model, @PathVariable Long orderId) {
        Optional<Order> optionalOrder = orderDao.getByOrderNumber(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            model.addAttribute("order", order);
        }
        return "orderDetails";
    }

    @PutMapping(value = "/{orderId}")
    public String changeOrderStatus(@PathVariable Long orderId, @RequestParam(name = "orderStatus") String orderStatus, Model model) {
        Optional<Order> optionalOrder = orderDao.getByOrderNumber(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            orderService.changeOrderStatus(OrderStatus.valueOf(orderStatus), order);
            model.addAttribute("order", order);
        }
        return "orderDetails";
    }
}
