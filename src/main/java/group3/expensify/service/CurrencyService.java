package group3.expensify.service;

import group3.expensify.model.Currency;
import group3.expensify.repository.CurrencyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {
    @Autowired
    private CurrencyRepository currencyRepository;

    public Currency getCurrencyByCode(String currencyCode) {
        return currencyRepository.findByCurrencyCode(currencyCode).orElse(null);
    }

    public Currency createCurrency(Currency currency) {
        return currencyRepository.save(currency);
    }

    public Currency getCurrencyById(Long id) {
        return currencyRepository.findById(id).orElse(null);
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }
}