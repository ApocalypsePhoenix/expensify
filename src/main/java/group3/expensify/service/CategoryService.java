package group3.expensify.service;

import group3.expensify.model.Category;
import group3.expensify.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    // Get all categories from the database
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();  // This retrieves all categories from the database
    }

    // Get category by name
    public Category getCategoryByName(String name) {
        return categoryRepository.findByName(name);  // This retrieves a category by its name
    }

    // Create a new category
    public Category createCategory(Category category) {
        return categoryRepository.save(category);  // This saves the new category to the database
    }

    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id).orElse(null);  // Fetch category by ID
    }
}
