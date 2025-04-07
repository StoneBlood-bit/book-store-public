package book.store.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import book.store.model.Book;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Sql(
        scripts = "classpath:database/create-book-with-category.sql",
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS
)
@Sql(
        scripts = "classpath:database/delete-book-with-category.sql",
        executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS
)
public class BookRepositoryTest {
    @Autowired
    private BookRepository bookRepository;

    @Test
    @DisplayName("Find book by valid categories id")
    void findByCategoriesId_ValidCategoryId_ShouldReturnBooks() {
        Long validCategoryId = 1L;
        List<Book> books = bookRepository.findByCategoriesId(validCategoryId);
        assertEquals(1, books.size());
        assertEquals("Sherlock Holmes", books.get(0).getTitle());
        assertEquals("Arthur Ignatius Conan Doyle", books.get(0).getAuthor());
    }

    @Test
    @DisplayName("Find book by invalid categories id")
    void findByCategoriesId_InvalidCategoryId_ShouldReturnEmptyList() {
        Long invalidCategoryId = -1L;

        List<Book> books = bookRepository.findByCategoriesId(invalidCategoryId);

        assertTrue(
                books.isEmpty(),
                "Expected an empty list for an invalid category ID");
    }
}
