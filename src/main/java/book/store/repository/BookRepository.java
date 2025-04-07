package book.store.repository;

import book.store.model.Book;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
    List<Book> findByCategoriesId(Long categoryId);

}
