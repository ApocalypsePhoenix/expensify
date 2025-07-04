package group3.expensify.init;

import group3.expensify.model.Category;
import group3.expensify.model.Currency;
import group3.expensify.repository.CategoryRepository;
import group3.expensify.repository.CurrencyRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer {

    @Autowired
    private CurrencyRepository currencyRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @PostConstruct
    public void seedData() {
        if (currencyRepository.count() == 0) {
            System.out.println("Seeding currencies...");
            currencyRepository.save(new Currency("MYR", "RM"));
            currencyRepository.save(new Currency("USD", "$"));
            currencyRepository.save(new Currency("EUR", "€"));
            currencyRepository.save(new Currency("GBP", "£"));
        } else {
            System.out.println("Currencies already seeded.");
        }

        if (categoryRepository.count() == 0) {
            System.out.println("Seeding categories...");
            categoryRepository.save(new Category("Food & Drink", "Meals, coffee, snacks, etc."));
            categoryRepository.save(new Category("Income", "Salary, freelance, bonuses"));
            categoryRepository.save(new Category("Shopping", "Clothes, groceries, etc."));
            categoryRepository.save(new Category("Entertainment", "Movies, games, subscriptions"));
            categoryRepository.save(new Category("Education", "Books, tuition, courses"));
            categoryRepository.save(new Category("Receipts", "Uploaded receipts"));
        } else {
            System.out.println("Categories already seeded.");
        }
    }
}
