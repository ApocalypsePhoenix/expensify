package group3.expensify.repository;

import group3.expensify.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Find category by name
    Category findByName(String name);  // Find a category by its name
}
