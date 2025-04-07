package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.order.OrderDto;
import book.store.dto.order.OrderRequestDto;
import book.store.model.Order;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface OrderMapper {
    OrderDto toDto(Order order);

    Order toModel(OrderRequestDto requestDto);
}
