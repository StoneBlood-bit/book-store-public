package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.orderitem.OrderItemDto;
import book.store.model.OrderItem;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    OrderItemDto toDto(OrderItem orderItem);
}
