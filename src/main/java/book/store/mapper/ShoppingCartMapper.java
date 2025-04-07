package book.store.mapper;

import book.store.config.MapperConfig;
import book.store.dto.shoppingcart.ShoppingCartDto;
import book.store.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface ShoppingCartMapper {
    ShoppingCartDto toDto(ShoppingCart shoppingCart);

    @Mapping(target = "id", ignore = true)
    ShoppingCart toModel(ShoppingCartDto shoppingCartDto);
}
