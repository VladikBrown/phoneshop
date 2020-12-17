package com.es.phoneshop.web.controller.pages;

import com.es.core.model.DAO.phone.PhoneDao;
import com.es.core.model.entity.cart.Cart;
import com.es.core.service.cart.CartService;
import com.es.core.service.order.OutOfStockException;
import com.es.phoneshop.web.controller.dto.QuickOrderEntryRow;
import com.es.phoneshop.web.controller.dto.QuickOrderRequestDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.util.StringUtils.isEmpty;
import static org.springframework.util.StringUtils.trimAllWhitespace;

@Controller
@RequestMapping(value = "/quickOrderEntry")
public class QuickOrderEntryPageController {

    @Resource
    private PhoneDao phoneDao;

    @Resource
    private CartService cartService;

    @Resource
    private Validator quickOrderEntryDtoValidator;

    public QuickOrderEntryPageController() {
    }

    @InitBinder("quickOrderRequestDto")
    protected void initBinder(WebDataBinder binder) {
        binder.setValidator(quickOrderEntryDtoValidator);
    }

    @GetMapping
    public String showQuickOrderEntry(Model model) {

        model.addAttribute("quickOrderRequestDto", new QuickOrderRequestDto());
        return "/quickOrderEntry";
    }

    @PostMapping
    public String makeQuickOrder(@ModelAttribute QuickOrderRequestDto quickOrderRequestDto,
                                 BindingResult bindingResult,
                                 HttpSession httpSession,
                                 Model model) {

        Cart cart = cartService.getCart(httpSession);
        List<String> successProducts = new LinkedList<>();

        quickOrderEntryDtoValidator.validate(quickOrderRequestDto, bindingResult);

        for (int i = 0; i < quickOrderRequestDto.getQuickOrderEntryRows().size(); i++) {
            if (!bindingResult.hasFieldErrors("quickOrderEntryRows[" + i + "].phoneModel")
                    && !bindingResult.hasFieldErrors("quickOrderEntryRows[" + i + "].quantity")) {

                var phoneModel = quickOrderRequestDto.getQuickOrderEntryRows().get(i).getPhoneModel();
                var quantity = quickOrderRequestDto.getQuickOrderEntryRows().get(i).getQuantity();
                var phone = phoneDao.get(phoneModel);

                if (isEmpty(trimAllWhitespace(phoneModel))
                        && (isEmpty(trimAllWhitespace(quantity)))) {
                    continue;
                }

                if (phone.isPresent()) {
                    try {
                        cartService.addPhone(cart, phone.get().getId(), Long.parseLong(quantity));
                        successProducts.add(phoneModel);
                        quickOrderRequestDto.getQuickOrderEntryRows().set(i, new QuickOrderEntryRow());
                    } catch (OutOfStockException outOfStockException) {
                        bindingResult.rejectValue("quickOrderEntryRows[" + i + "].quantity", "quantity.OutOfStock",
                                "Out of stock");
                    }
                } else {
                    bindingResult.rejectValue("quickOrderEntryRows[" + i + "].phoneModel", "quantity.ProductNotFound",
                            "Product not found");
                }

            }
        }

        model.addAttribute("quickOrderRequestDto", quickOrderRequestDto);
        model.addAttribute("successProducts", successProducts);
        model.addAttribute("errors", bindingResult);

        return "/quickOrderEntry";
    }
}
