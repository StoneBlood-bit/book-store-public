package book.store.controller;

import book.store.dto.order.OrderDto;
import book.store.dto.order.OrderRequestDto;
import book.store.dto.order.UpdateOrderStatusDto;
import book.store.dto.orderitem.OrderItemDto;
import book.store.model.User;
import book.store.service.order.OrderService;
import book.store.service.orderitem.OrderItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order management", description = "Endpoint for managing orders")
@RestController
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderItemService orderItemService;

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create order", description = "Create a new order")
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public OrderDto createOrder(@RequestBody @Valid OrderRequestDto requestDto,
                                @AuthenticationPrincipal User user) {
        return orderService.createOrder(requestDto, user.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get user's orders", description = "Get a history of orders for user")
    @GetMapping
    public List<OrderDto> getUserOrderHistory(@AuthenticationPrincipal User user) {
        return orderService.getUserOrderHistory(user.getId());
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get order", description = "Get order with a passed id")
    @GetMapping("/{id}")
    public OrderDto getOrderById(@PathVariable Long orderId) {
        return orderService.getOrderById(orderId);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update order's status",
            description = "Update status for order with a passed id")
    @PatchMapping("/{id}")
    public OrderDto updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody UpdateOrderStatusDto updateOrderStatusDto
    ) {
        return orderService.updateOrderStatus(orderId, updateOrderStatusDto.getStatus());
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get order items", description = "Get list of order items by order id")
    @GetMapping("/{orderId}/items")
    public List<OrderItemDto> getOrderItems(@PathVariable Long orderId) {
        return orderItemService.getOrderItemsByOrderId(orderId);
    }

    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get order item", description = "Get a specific item in the order")
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemDto getOrderItem(@PathVariable Long orderId, @PathVariable Long itemId) {
        return orderItemService.getOrderItemById(orderId, itemId);
    }
}
