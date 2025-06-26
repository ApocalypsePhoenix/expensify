package group3.expensify.controller;

import group3.expensify.model.Currency;
import group3.expensify.service.CurrencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/currencies")
public class CurrencyController {
    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/{currencyCode}")
    public Currency getCurrencyByCode(@PathVariable String currencyCode) {
        return currencyService.getCurrencyByCode(currencyCode);
    }

    @PostMapping
    public Currency createCurrency(@RequestBody Currency currency) {
        return currencyService.createCurrency(currency);
    }
}
