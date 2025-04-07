package book.store.dto.shoppingcart;

import book.store.model.CartItem;
import book.store.model.User;
import java.util.Set;
import lombok.Data;

@Data
public class ShoppingCartDto {
    private Long id;
    private User user;
    private Set<CartItem> cartItems;
}
