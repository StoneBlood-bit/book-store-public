package book.store.service.order;

import book.store.dto.order.OrderDto;
import book.store.dto.order.OrderRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.OrderMapper;
import book.store.model.Order;
import book.store.model.OrderItem;
import book.store.model.ShoppingCart;
import book.store.repository.OrderRepository;
import book.store.repository.ShoppingCartRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ShoppingCartRepository shoppingCartRepository;

    @Override
    public OrderDto createOrder(OrderRequestDto requestDto, Long userId) {
        ShoppingCart shoppingCart = getShoppingCartForUser(userId);
        Order order = createNewOrder(requestDto, shoppingCart);

        Set<OrderItem> orderItems = buildOrderItems(shoppingCart, order);
        BigDecimal total = calculateTotal(orderItems);

        order.setTotal(total);
        order.setOrderItems(orderItems);

        orderRepository.save(order);
        clearShoppingCart(shoppingCart);

        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderDto> getUserOrderHistory(Long userId) {
        return orderRepository.findByUserId(userId).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderDto getOrderById(Long id) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can not find order by id: " + id)
        );
        return orderMapper.toDto(order);
    }

    @Override
    public OrderDto updateOrderStatus(Long id, Order.Status status) {
        Order order = orderRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Can not find order by id: " + id)
        );
        order.setStatus(status);
        return orderMapper.toDto(orderRepository.save(order));
    }

    private ShoppingCart getShoppingCartForUser(Long userId) {
        return shoppingCartRepository.findByUserId(userId).orElseThrow(
                () -> new EntityNotFoundException("Can not find user with id: " + userId)
        );
    }

    private Order createNewOrder(OrderRequestDto requestDto, ShoppingCart shoppingCart) {
        Order order = new Order();
        order.setUser(shoppingCart.getUser());
        order.setStatus(Order.Status.PENDING);
        order.setShippingAddress(requestDto.getShippingAddress());
        return order;
    }

    private Set<OrderItem> buildOrderItems(ShoppingCart shoppingCart, Order order) {
        return shoppingCart.getCartItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    BigDecimal price = cartItem.getBook()
                            .getPrice()
                            .multiply(BigDecimal.valueOf(cartItem.getQuantity()));
                    orderItem.setPrice(price);
                    return orderItem;
                })
                .collect(Collectors.toSet());
    }

    private BigDecimal calculateTotal(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(OrderItem::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private void clearShoppingCart(ShoppingCart shoppingCart) {
        shoppingCart.getCartItems().clear();
        shoppingCartRepository.save(shoppingCart);
    }
}
