package book.store.repository;

import book.store.model.Book;
import book.store.model.CartItem;
import book.store.model.ShoppingCart;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    CartItem findByShoppingCartAndBook(ShoppingCart shoppingCart, Book book);

    Optional<CartItem> findByIdAndShoppingCartId(Long id, Long shoppingCartId);
}
