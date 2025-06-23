package group3.expensify.repository;

import group3.expensify.model.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {
    // Find currency by currency code
    Currency findByCurrencyCode(String currencyCode);  // Find a currency by its code
}
