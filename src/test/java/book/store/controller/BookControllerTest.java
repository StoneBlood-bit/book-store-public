package book.store.controller;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.hamcrest.Matchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import book.store.dto.PageResponse;
import book.store.dto.book.BookDto;
import book.store.dto.book.CreateBookRequestDto;
import book.store.security.JwtUtil;
import book.store.service.book.BookServiceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BookControllerTest {
    protected static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private JwtUtil jwtUtil;

    @Mock
    private BookServiceImpl bookService;

    @BeforeAll
    static void beforeAll(
            @Autowired WebApplicationContext applicationContext
    ) {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/controller/book/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Create a new book")
    void createBook_ValidRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Sherlock Holmes");
        requestDto.setAuthor("Arthur Ignatius Conan Doyle");
        requestDto.setIsbn("674-465-23");
        requestDto.setPrice(BigDecimal.TEN);
        requestDto.setCategoryIds(List.of(1L));

        BookDto expected = new BookDto();
        expected.setTitle(requestDto.getTitle());
        expected.setAuthor(requestDto.getAuthor());
        expected.setIsbn(requestDto.getIsbn());
        expected.setPrice(requestDto.getPrice());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        MvcResult result = mockMvc.perform(post("/books")
                .content(jsonRequest)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn();

        BookDto actual = objectMapper
                .readValue(result.getResponse().getContentAsString(), BookDto.class);
        assertNotNull(actual);
        assertNotNull(actual.getId());
        assertTrue(reflectionEquals(expected, actual, "id"));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/controller/book/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Fail to create a book with invalid request")
    void createBook_InvalidRequestDto_Failure() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("");
        requestDto.setAuthor("A");
        requestDto.setIsbn("123");
        requestDto.setPrice(BigDecimal.valueOf(-10));
        requestDto.setCategoryIds(List.of());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/books")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasItems(
                        "price must be greater than or equal to 0",
                        "title must not be blank",
                        "categoryIds must not be empty"
                )))
                .andReturn();
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(
            scripts = "classpath:database/controller/book/add-three-books.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/book/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @Test
    @DisplayName("Get all books")
    void getAll_GivenBooksInCatalog_Success() throws Exception {
        BookDto bookDto1 = new BookDto();
        bookDto1.setId(1L);
        bookDto1.setTitle("Sherlock Holmes");
        bookDto1.setAuthor("Arthur Ignatius Conan Doyle");
        bookDto1.setIsbn("674-465-23");
        bookDto1.setPrice(BigDecimal.valueOf(65.99));

        BookDto bookDto2 = new BookDto();
        bookDto2.setId(2L);
        bookDto2.setTitle("Kobzar");
        bookDto2.setAuthor("THSh");
        bookDto2.setIsbn("5885-565-23");
        bookDto2.setPrice(BigDecimal.valueOf(34.78));

        BookDto bookDto3 = new BookDto();
        bookDto3.setId(3L);
        bookDto3.setTitle("Red book");
        bookDto3.setAuthor("the world");
        bookDto3.setIsbn("6565-5765-363");
        bookDto3.setPrice(BigDecimal.valueOf(65.99));

        List<BookDto> expected = new ArrayList<>();
        expected.add(bookDto1);
        expected.add(bookDto2);
        expected.add(bookDto3);

        MvcResult result = mockMvc.perform(get("/books")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        PageResponse<BookDto> actualPage = objectMapper
                .readValue(jsonResponse, new TypeReference<>() {});
        List<BookDto> actualList = actualPage.getContent();

        assertNotNull(actualPage);
        assertNotNull(actualPage.getContent());
        assertEquals(3, actualList.size());
        assertEquals(expected, actualList);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(
            scripts = "classpath:database/controller/book/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/book/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Get book by id")
    @Test
    void getBookById_ValidId_Success() throws Exception {
        Long validId = 1L;
        BookDto expected = new BookDto();
        expected.setId(validId);
        expected.setTitle("Sherlock Holmes");
        expected.setAuthor("Arthur Ignatius Conan Doyle");
        expected.setIsbn("674-465-23");
        expected.setPrice(BigDecimal.valueOf(65.99));

        MvcResult result = mockMvc.perform(get("/books/{id}", validId)
                    .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BookDto actualBook = objectMapper.readValue(jsonResponse, BookDto.class);

        assertEquals(expected, actualBook);
    }

    @WithMockUser(username = "user", roles = {"USER"})
    @Sql(
            scripts = "classpath:database/controller/book/add-one-book.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/book/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Get book by invalid id")
    @Test
    void getBookById_InvalidId_NotFound() throws Exception {
        Long invalidId = 999L;

        mockMvc.perform(get("/books/{id}", invalidId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/controller/book/add-book-for-update.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/book/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Update book")
    @Test
    void updateBook_GivenValidIdAndRequestDto_Success() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("Updated Title");
        requestDto.setAuthor("Updated Author");
        requestDto.setIsbn("123-456-789");
        requestDto.setPrice(BigDecimal.valueOf(49.99));
        requestDto.setCategoryIds(List.of(1L));

        Long bookId = 1L;

        BookDto updatedBookDto = new BookDto();
        updatedBookDto.setId(bookId);
        updatedBookDto.setTitle("Updated Title");
        updatedBookDto.setAuthor("Updated Author");
        updatedBookDto.setIsbn("123-456-789");
        updatedBookDto.setPrice(BigDecimal.valueOf(49.99));

        MvcResult result = mockMvc.perform(put("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();
        BookDto actualUpdatedBookDto = objectMapper.readValue(jsonResponse, BookDto.class);

        assertTrue(reflectionEquals(updatedBookDto, actualUpdatedBookDto));
    }

    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @Sql(
            scripts = "classpath:database/controller/book/add-book-for-update.sql",
            executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
    )
    @Sql(
            scripts = "classpath:database/controller/book/delete-books.sql",
            executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
    )
    @DisplayName("Fail to update book with invalid data")
    @Test
    void updateBook_InvalidData_Failure() throws Exception {
        CreateBookRequestDto requestDto = new CreateBookRequestDto();
        requestDto.setTitle("");
        requestDto.setAuthor("A");
        requestDto.setIsbn("123");
        requestDto.setPrice(BigDecimal.valueOf(-10));
        requestDto.setCategoryIds(List.of());

        Long bookId = 1L;

        String jsonRequest = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/books/{id}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists())
                .andExpect(jsonPath("$.errors", hasItems(
                        "price must be greater than or equal to 0",
                        "title must not be blank",
                        "categoryIds must not be empty"
                )))
                .andReturn();
    }

}
