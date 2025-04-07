package book.store.controller;

import book.store.dto.cartitem.CartItemRequestDto;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.User;
import book.store.service.shoppingcart.ShoppingCartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping cart management", description = "Endpoint for managing shopping carts")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Get a shopping cart", description = "Find a shopping cart for a user")
    @PreAuthorize("hasRole('USER')")
    @GetMapping
    public ShoppingCartDto getCart(@AuthenticationPrincipal User user) {
        return shoppingCartService.getByUserId(user.getId());
    }

    @Operation(summary = "Add a book to cart item",
            description = "Add a book to cart item for a user")
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ShoppingCartDto addBookToCart(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CartItemRequestDto requestDto) {
        return shoppingCartService.addBook(user.getId(), requestDto);
    }

    @Operation(summary = "Update cart item", description = "Update cart item for a user")
    @PreAuthorize("hasRole('USER')")
    @PutMapping("/items/{id}")
    public ShoppingCartDto updateCartItem(@PathVariable Long id,
                               @RequestBody @Valid CartItemRequestDto requestDto,
                                          @AuthenticationPrincipal User user) {
        return shoppingCartService.updateCartItem(id, requestDto, user.getId());
    }

    @Operation(summary = "Remove cart item", description = "Remove cart item for a user")
    @PreAuthorize("hasRole('USER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/items/{id}")
    public void removeCartItem(@PathVariable Long id) {
        shoppingCartService.removeCartItem(id);
    }
}
