package book.store.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import book.store.dto.book.BookDto;
import book.store.dto.book.BookDtoWithoutCategoryIds;
import book.store.dto.book.CreateBookRequestDto;
import book.store.exception.EntityNotFoundException;
import book.store.mapper.BookMapper;
import book.store.model.Book;
import book.store.repository.BookRepository;
import book.store.service.book.BookServiceImpl;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private BookMapper bookMapper;

    @Test
    @DisplayName("Verify save() method works")
    void save_ValidCreateBookRequestDto_ShouldReturnBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Sherlock Holmes");
        requestDto.setAuthor("Arthur Ignatius Conan Doyle");
        requestDto.setIsbn("674-465-23");
        requestDto.setPrice(BigDecimal.TEN);

        Book book = new Book();
        book.setTitle(requestDto.getTitle());
        book.setAuthor(requestDto.getAuthor());
        book.setIsbn(requestDto.getIsbn());
        book.setPrice(requestDto.getPrice());

        BookDto bookDto = new BookDto();
        bookDto.setId(1L);
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());

        when(bookMapper.toModel(requestDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(book);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto savedBookDto = bookService.save(requestDto);

        assertThat(savedBookDto).isEqualTo(bookDto);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify findAll() method works")
    void findAll_ValidPageable_ShouldReturnsAllBooks() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("Sherlock Holmes");
        book.setAuthor("Arthur Ignatius Conan Doyle");
        book.setIsbn("674-465-23");
        book.setPrice(BigDecimal.TEN);
        
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());

        Pageable pageable = PageRequest.of(0, 10);
        List<Book> books = List.of(book);
        Page<Book> booksPage = new PageImpl<>(books, pageable, books.size());
        
        when(bookRepository.findAll(pageable)).thenReturn(booksPage);
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        Page<BookDto> actualPage = bookService.findAll(pageable);

        assertThat(actualPage).hasSize(1);
        assertThat(actualPage.getContent().get(0)).isEqualTo(bookDto);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify getBookById() method works")
    void getBookById_ValidId_ShouldReturnBookDto() {
        Long validId = 1L;

        Book book = new Book();
        book.setId(validId);
        book.setTitle("Sherlock Holmes");
        book.setAuthor("Arthur Ignatius Conan Doyle");
        book.setIsbn("674-465-23");
        book.setPrice(BigDecimal.TEN);

        BookDto bookDto = new BookDto();
        bookDto.setId(book.getId());
        bookDto.setTitle(book.getTitle());
        bookDto.setAuthor(book.getAuthor());
        bookDto.setIsbn(book.getIsbn());
        bookDto.setPrice(book.getPrice());

        when(bookRepository.findById(validId)).thenReturn(Optional.of(book));
        when(bookMapper.toDto(book)).thenReturn(bookDto);

        BookDto actualBookDto = bookService.getBookById(validId);

        assertThat(actualBookDto).isEqualTo(bookDto);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Return EntityNotFoundException for invalid id")
    void getBookById_InvalidId_ShouldReturnEntityNotFoundException() {
        Long invalidId = 99L;

        when(bookRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.getBookById(invalidId)
        );

        assertThat(exception.getMessage()).isEqualTo("Can't get book by id:" + invalidId);
        verifyNoMoreInteractions(bookMapper, bookRepository);
    }

    @Test
    @DisplayName("Verify updateBook() method works")
    void updateBook_ValidIdAndRequestDto_ShouldUpdateAndReturnBookDto() {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Title");
        requestDto.setAuthor("Updated Author");
        requestDto.setIsbn("123-456-789");
        requestDto.setPrice(BigDecimal.valueOf(20));

        Long validId = 1L;

        Book existingBook = new Book();
        existingBook.setId(validId);
        existingBook.setTitle("Original Title");
        existingBook.setAuthor("Original Author");
        existingBook.setIsbn("987-654-321");
        existingBook.setPrice(BigDecimal.TEN);

        Book updatedBook = new Book();
        updatedBook.setId(validId);
        updatedBook.setTitle(requestDto.getTitle());
        updatedBook.setAuthor(requestDto.getAuthor());
        updatedBook.setIsbn(requestDto.getIsbn());
        updatedBook.setPrice(requestDto.getPrice());

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(validId);
        updatedBookDto.setTitle(updatedBook.getTitle());
        updatedBookDto.setAuthor(updatedBook.getAuthor());
        updatedBookDto.setIsbn(updatedBook.getIsbn());
        updatedBookDto.setPrice(updatedBook.getPrice());

        when(bookRepository.findById(validId)).thenReturn(Optional.of(existingBook));
        doNothing().when(bookMapper).updateBookFromDto(requestDto, existingBook);
        when(bookRepository.save(existingBook)).thenReturn(updatedBook);
        when(bookMapper.toDto(updatedBook)).thenReturn(updatedBookDto);

        BookDto result = bookService.updateBook(validId, requestDto);

        assertThat(result).isEqualTo(updatedBookDto);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Return EntityNotFoundException for invalid id")
    void updateBook_InvalidId_ShouldThrowEntityNotFoundException() {
        Long invalidId = 99L;
        CreateBookRequestDto requestDto = new CreateBookRequestDto();

        when(bookRepository.findById(invalidId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> bookService.updateBook(invalidId, requestDto)
        );

        assertThat(exception.getMessage()).isEqualTo("Can't get book by id:" + invalidId);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }

    @Test
    @DisplayName("Verify getBooksByCategoryId() method works")
    void getBooksByCategoryId_ValidCategoryId_ShouldReturnListOfBooks() {
        Book book1 = new Book();
        book1.setId(1L);
        book1.setTitle("first book");
        book1.setAuthor("first author");
        book1.setIsbn("234-234");
        book1.setPrice(BigDecimal.TEN);

        Book book2 = new Book();
        book2.setId(2L);
        book2.setTitle("second book");
        book2.setAuthor("second author");
        book2.setIsbn("345-345");
        book2.setPrice(BigDecimal.TEN);

        BookDtoWithoutCategoryIds dto1 = new BookDtoWithoutCategoryIds();
        dto1.setId(book1.getId());
        dto1.setTitle(book1.getTitle());
        dto1.setAuthor(book1.getAuthor());
        dto1.setIsbn(book1.getIsbn());
        dto1.setPrice(book1.getPrice());

        BookDtoWithoutCategoryIds dto2 = new BookDtoWithoutCategoryIds();
        dto2.setId(book2.getId());
        dto2.setTitle(book2.getTitle());
        dto2.setAuthor(book2.getAuthor());
        dto2.setIsbn(book2.getIsbn());
        dto2.setPrice(book2.getPrice());

        Long validCategoryId = 1L;
        List<Book> bookList = List.of(book1, book2);

        when(bookRepository.findByCategoriesId(validCategoryId)).thenReturn(bookList);
        when(bookMapper.toDtoWithoutCategories(book1)).thenReturn(dto1);
        when(bookMapper.toDtoWithoutCategories(book2)).thenReturn(dto2);

        List<BookDtoWithoutCategoryIds> booksByCategoryId = bookService
                .getBooksByCategoryId(validCategoryId);

        assertThat(booksByCategoryId).containsExactlyInAnyOrder(dto1, dto2);
        verifyNoMoreInteractions(bookRepository, bookMapper);
    }
}
