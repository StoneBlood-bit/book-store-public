package book.store.service.order;

import book.store.dto.order.OrderDto;
import book.store.dto.order.OrderRequestDto;
import book.store.model.Order;
import java.util.List;

public interface OrderService {
    OrderDto createOrder(OrderRequestDto requestDto, Long userId);

    List<OrderDto> getUserOrderHistory(Long userId);

    OrderDto getOrderById(Long id);

    OrderDto updateOrderStatus(Long id, Order.Status status);
}
