package book.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import book.store.dto.category.CategoryDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.CategoryMapper;
import book.store.model.Category;
import book.store.repository.CategoryRepository;
import book.store.service.category.CategoryServiceImpl;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {
    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCategoryDto_ShouldReturnCategoryDto() {
        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Detective");

        Category category = new Category();
        category.setName(categoryDto.getName());

        CategoryDto savedCategory = new CategoryDto();
        savedCategory.setId(1L);
        savedCategory.setName(category.getName());

        Mockito.when(categoryMapper.toEntity(categoryDto)).thenReturn(category);
        Mockito.when(categoryRepository.save(category)).thenReturn(category);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(savedCategory);

        CategoryDto result = categoryService.save(categoryDto);

        assertThat(result).isEqualTo(savedCategory);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    void findAll_ValidPageable_ShouldReturnsAllCategories() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Detective");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());

        Pageable pageable = PageRequest.of(0, 10);
        List<Category> categories = List.of(category);
        Page<Category> categoryPage = new PageImpl<>(categories, pageable, categories.size());

        Mockito.when(categoryRepository.findAll(pageable)).thenReturn(categoryPage);
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        Page<CategoryDto> actualPage = categoryService.findAll(pageable);

        assertThat(actualPage).hasSize(1);
        assertThat(actualPage.getContent().get(0)).isEqualTo(categoryDto);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify getById() method works")
    void getById_ValidId_ShouldReturnCategoryDto() {
        Long validId = 1L;

        Category category = new Category();
        category.setId(validId);
        category.setName("Detective");

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());

        Mockito.when(categoryRepository.findById(validId)).thenReturn(Optional.of(category));
        Mockito.when(categoryMapper.toDto(category)).thenReturn(categoryDto);

        CategoryDto actualCategoryDto = categoryService.getById(validId);

        assertThat(actualCategoryDto).isEqualTo(categoryDto);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Return EntityNotFoundException for invalid id")
    void getById_InvalidId_ShouldReturnEntityNotFoundException() {
        Long invalidId = 99L;

        Mockito.when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.getById(invalidId)
        );

        assertThat(exception.getMessage()).isEqualTo("Can't get category by id:" + invalidId);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify update() method works")
    void update_ValidIdAndDto_ShouldUpdateAndReturnCategoryDto() {
        Long validId = 1L;

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setName("Detective");

        Category existingCategory = new Category();
        existingCategory.setId(validId);
        existingCategory.setName(categoryDto.getName());

        Category updatedCategory = new Category();
        updatedCategory.setId(validId);
        updatedCategory.setName(categoryDto.getName());

        CategoryDto savedCategoryDto = new CategoryDto();
        savedCategoryDto.setId(validId);
        savedCategoryDto.setName(updatedCategory.getName());

        Mockito.when(categoryRepository.findById(validId))
                .thenReturn(Optional.of(existingCategory));
        Mockito.doNothing().when(categoryMapper)
                .updateCategoryFromDto(categoryDto, existingCategory);
        Mockito.when(categoryRepository.save(existingCategory))
                .thenReturn(updatedCategory);
        Mockito.when(categoryMapper.toDto(updatedCategory)).thenReturn(savedCategoryDto);

        CategoryDto result = categoryService.update(validId, categoryDto);

        assertThat(result).isEqualTo(savedCategoryDto);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Return EntityNotFoundException for invalid id")
    void update_InvalidId_ShouldThrowEntityNotFoundException() {
        Long invalidId = 99L;

        CategoryDto categoryDto = new CategoryDto();

        Mockito.when(categoryRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> categoryService.update(invalidId, categoryDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Can't get category by id: " + invalidId);
        Mockito.verifyNoMoreInteractions(categoryRepository, categoryMapper);
    }

    @Test
    @DisplayName("Verify findByIds() method works")
    void findByIds_ValidIds_ShouldReturnCategories() {
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Detective");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Fiction");

        List<Long> ids = List.of(1L, 2L);

        Mockito.when(categoryRepository.findAllById(ids)).thenReturn(List.of(category1, category2));

        Set<Category> categories = categoryService.findByIds(ids);

        assertThat(categories).containsExactlyInAnyOrder(category1, category2);
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }

    @Test
    @DisplayName("Return empty set for invalid category IDs")
    void findByIds_InvalidIds_ShouldReturnEmptyList() {
        List<Long> invalidIds = List.of(9999L, 10000L);

        Mockito.when(categoryRepository.findAllById(invalidIds)).thenReturn(List.of());

        Set<Category> categories = categoryService.findByIds(invalidIds);

        assertThat(categories).isEmpty();
        Mockito.verifyNoMoreInteractions(categoryRepository);
    }
}
