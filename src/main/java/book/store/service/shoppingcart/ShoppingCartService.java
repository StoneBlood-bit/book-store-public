package book.store.service.shoppingcart;

import book.store.dto.cartitem.CartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.ShoppingCart;
import book.store.model.User;

public interface ShoppingCartService {
    ShoppingCartDto getByUserId(Long userId);

    ShoppingCartDto addBook(Long userId, CartItemRequestDto requestDto);

    ShoppingCartDto updateCartItem(Long cartItemId, CartItemRequestDto requestDto, Long userId);

    ShoppingCart createShoppingCart(User user);

    void removeCartItem(Long cartItemId);
}
