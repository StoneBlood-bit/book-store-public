package book.store.dto.order;

import book.store.dto.orderitem.OrderItemDto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;

@Data
public class OrderDto {
    private Long id;
    private Long userId;
    private BigDecimal total;
    private String shippingAddress;
    private LocalDateTime orderDate;
    private String status;
    private List<OrderItemDto> orderItems;
}
