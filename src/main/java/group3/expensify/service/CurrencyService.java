package group3.expensify.service;

import group3.expensify.model.Currency;
import group3.expensify.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CurrencyService {

    @Autowired
    private CurrencyRepository currencyRepository;

    // Get currency by currency code
    public Currency getCurrencyByCode(String currencyCode) {
        return currencyRepository.findByCurrencyCode(currencyCode);  // Fetch currency by its code
    }

    // Create a new currency
    public Currency createCurrency(Currency currency) {
        return currencyRepository.save(currency);  // Save the new currency to the database
    }

    public Currency getCurrencyById(Long id) {
        return currencyRepository.findById(id).orElse(null);  // Fetch currency by ID
    }
}
