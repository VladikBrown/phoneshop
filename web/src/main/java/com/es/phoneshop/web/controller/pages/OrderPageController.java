package com.es.phoneshop.web.controller.pages;

import com.es.core.model.entity.cart.Cart;
import com.es.core.model.entity.order.Order;
import com.es.core.service.cart.CartService;
import com.es.core.service.order.OrderService;
import com.es.core.service.order.OutOfStockException;
import com.es.phoneshop.web.controller.dto.CustomerInfoDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "/order")
public class OrderPageController {

    public static final String CUSTOMER_INFO_ERRORS_ATTRIBUTE_NAME = "customerInfoErrors";
    public static final String QUANTITY_ERRORS_ATTRIBUTE_NAME = "quantityErrors";
    public static final String CUSTOMER_INFO_ATTRIBUTE_NAME = "customerInfo";
    public static final String ORDER_ATTRIBUTE_NAME = "order";

    @Resource
    private OrderService orderService;

    @Resource
    private Validator customerInfoValidator;

    @Resource
    private CartService cartService;

    @InitBinder("consumerInfoDto")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(customerInfoValidator);
    }

    @GetMapping
    public String getOrder(Model model, HttpSession httpSession) {
        model.addAttribute("order", orderService.createOrder(cartService.getCart(httpSession)));
        return "order";
    }

    @PostMapping
    public String placeOrder(CustomerInfoDto customerInfoDto, Model model,
                             HttpSession httpSession, BindingResult bindingResult) {

        Map<String, String> customerInfoErrors = new HashMap<>();
        Map<Long, String> quantityErrors;

        Cart cart = cartService.getCart(httpSession);
        quantityErrors = cartService.trimRedundantProducts(cart);
        Order order = orderService.createOrder(cart);

        customerInfoValidator.validate(customerInfoDto, bindingResult);
        if (bindingResult.hasErrors()) {
            bindingResult.getAllErrors().forEach(objectError -> customerInfoErrors.put(objectError.getCode(), objectError.getDefaultMessage()));
        } else {
            order.setFirstName(customerInfoDto.getFirstName());
            order.setLastName(customerInfoDto.getLastName());
            order.setDeliveryAddress(customerInfoDto.getDeliveryAddress());
            order.setContactPhoneNo(customerInfoDto.getContactPhoneNo());
            order.setAdditionalInformation(customerInfoDto.getAdditionalInformation());

            try {
                orderService.placeOrder(order);
            } catch (OutOfStockException e) {
                quantityErrors = cartService.trimRedundantProducts(cartService.getCart(httpSession));
            }
        }

        model.addAttribute(CUSTOMER_INFO_ERRORS_ATTRIBUTE_NAME, customerInfoErrors);
        model.addAttribute(QUANTITY_ERRORS_ATTRIBUTE_NAME, quantityErrors);
        model.addAttribute(CUSTOMER_INFO_ATTRIBUTE_NAME, customerInfoDto);
        model.addAttribute(ORDER_ATTRIBUTE_NAME, order);

        if (customerInfoErrors.isEmpty() && quantityErrors.isEmpty()) {
            return "redirect:/orderOverview/" + order.getSecureId();
        } else {
            return "order";
        }
    }
}
