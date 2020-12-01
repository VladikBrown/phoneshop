package com.es.core.service.cart;

import com.es.core.model.DAO.phone.PhoneDao;
import com.es.core.model.DAO.stock.StockDao;
import com.es.core.model.entity.cart.Cart;
import com.es.core.model.entity.cart.CartItem;
import com.es.core.model.entity.phone.Phone;
import com.es.core.model.entity.phone.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class HttpSessionCartService implements CartService {

    private static final String CART_SESSION_ATTRIBUTE = HttpSessionCartService.class.getName() + ".cart";
    private static final String OUT_OF_STOCK_ERROR_MESSAGE = "Out of stock";
    private static final String NOT_PRESENT_STOCK_ERROR_MESSAGE = "This product isn't available now";
    private static final String UPDATE_SUCCESS_MESSAGE = "Successfully updated";
    private static final String QUANTITY_CHANGED_OUT_OF_STOCK_MESSAGE = "Quantity has been decreased";

    @Autowired
    private PhoneDao phoneDao;
    @Autowired
    private StockDao stockDao;

    @Override
    public Cart getCart(HttpSession httpSession) {
        synchronized (httpSession) {
            Cart cart = (Cart) httpSession.getAttribute(CART_SESSION_ATTRIBUTE);
            if (cart == null) {
                cart = new Cart();
                httpSession.setAttribute(CART_SESSION_ATTRIBUTE, cart);
            }
            return cart;
        }
    }

    @Override
    public void addPhone(Cart cart, Long phoneId, Long quantity) {
        try {
            Phone phone = phoneDao.get(phoneId).get();
            Optional<CartItem> optionalCartItem = findItemInCart(cart, phoneId);
            if (optionalCartItem.isPresent()) {
                var cartItem = optionalCartItem.get();
                increaseQuantity(cartItem, quantity);
            } else {
                cart.getItems().add(new CartItem(phone, quantity));
            }
            recalculateCart(cart);
        } catch (NoSuchElementException e) {
            throw new NoSuchElementException(String.valueOf(phoneId));
        }
    }

    @Override
    public Map<Long, String> update(Cart cart, Map<Long, Long> items, Map<Long, String> errors) {
        errors.putAll(validateUpdate(items));
        if (errors.isEmpty()) {
            updateCart(cart, items);
            recalculateCart(cart);
        }
        return errors;
    }

    private Map<Long, String> validateUpdate(Map<Long, Long> items) {
        Map<Long, String> errors = new HashMap<>();
        for (var tuple : items.entrySet()) {
            Optional<Stock> optionalStock = stockDao.get(tuple.getKey());
            Stock stock;
            if (optionalStock.isPresent()) {
                stock = optionalStock.get();
            } else {
                errors.put(tuple.getKey(), NOT_PRESENT_STOCK_ERROR_MESSAGE);
                continue;
            }
            if (tuple.getValue() > stock.getStock()) {
                errors.put(tuple.getKey(), OUT_OF_STOCK_ERROR_MESSAGE);
            }
        }
        return errors;
    }

    private void updateCart(Cart cart, Map<Long, Long> items) {
        items.forEach((key, value) -> cart.getItems()
                .stream()
                .filter(cartItem -> cartItem.getProduct().getId().equals(key))
                .findAny()
                .ifPresent(cartItem -> cartItem.setQuantity(value)));
    }

    @Override
    public void remove(Cart cart, Long phoneId) {
        cart.getItems().removeIf(cartItem -> cartItem.getProduct().getId().equals(phoneId));
        recalculateCart(cart);
    }

    private void recalculateCart(Cart cart) {
        recalculateTotalCost(cart);
        recalculateTotalQuantity(cart);
    }

    private void recalculateTotalCost(Cart cart) {
        cart.setTotalCost(cart.getItems()
                .stream()
                .map(this::getCartItemTotalPrice)
                .reduce(BigDecimal::add)
                .orElse(BigDecimal.ZERO));
    }

    private void recalculateTotalQuantity(Cart cart) {
        cart.setTotalQuantity(cart.getItems()
                .stream()
                .map(CartItem::getQuantity)
                .reduce(Long::sum)
                .orElse(0L));
    }

    private Optional<CartItem> findItemInCart(Cart cart, Long productId) {
        return cart.getItems()
                .stream()
                .filter(existingCartItem -> existingCartItem.getProduct().getId().equals(productId))
                .findAny();
    }

    private BigDecimal getCartItemTotalPrice(CartItem cartItem) {
        var phonePrice = cartItem.getProduct().getPrice();
        if (phonePrice != null) {
            return phonePrice.multiply(BigDecimal.valueOf(cartItem.getQuantity()));
        } else return BigDecimal.ZERO;
    }

    private void increaseQuantity(CartItem cartItem, Long quantity) {
        cartItem.setQuantity(cartItem.getQuantity() + quantity);
    }

    private void decreaseQuantity(CartItem cartItem, Long quantity) {
        if (cartItem.getQuantity() - quantity >= 0) {
            cartItem.setQuantity(cartItem.getQuantity() - quantity);
        } else cartItem.setQuantity(0L);
    }

    public Map<Long, String> trimRedundantProducts(Cart cart) {
        Map<Long, String> changesInfo = new HashMap<>();
        for (var cartItem : cart.getItems()) {
            var optionalStock = stockDao.get(cartItem.getProduct().getId());
            if (!optionalStock.isPresent()) {
                cartItem.setQuantity(0L);
                changesInfo.put(cartItem.getProduct().getId(), QUANTITY_CHANGED_OUT_OF_STOCK_MESSAGE);
                continue;
            }
            if (optionalStock.get().getStock() < cartItem.getQuantity()) {
                cartItem.setQuantity(Long.valueOf(optionalStock.get().getStock()));
                changesInfo.put(cartItem.getProduct().getId(), QUANTITY_CHANGED_OUT_OF_STOCK_MESSAGE);
            }
        }
        recalculateCart(cart);
        return changesInfo;
    }
}
