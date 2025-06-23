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

    // Get currency by currency code
    @GetMapping("/{currencyCode}")
    public Currency getCurrencyByCode(@PathVariable String currencyCode) {
        return currencyService.getCurrencyByCode(currencyCode);  // Call service method to fetch currency by code
    }

    // Create a new currency
    @PostMapping
    public Currency createCurrency(@RequestBody Currency currency) {
        return currencyService.createCurrency(currency);  // Call service method to create new currency
    }
}
