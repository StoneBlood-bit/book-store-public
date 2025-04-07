package book.store.dto.order;

import book.store.model.Order;
import lombok.Data;

@Data
public class UpdateOrderStatusDto {
    private Order.Status status;
}
