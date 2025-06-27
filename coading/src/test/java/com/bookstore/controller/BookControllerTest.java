package com.bookstore.controller;

import com.bookstore.dto.BookDTO;
import com.bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.springframework.context.annotation.Import;
import com.bookstore.config.WebSecurityConfig;

@WebMvcTest(BookController.class)
@Import(WebSecurityConfig.class)
class BookControllerTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookService bookService;
    
    @MockBean
    private com.bookstore.service.UserDetailsServiceImpl userDetailsService;
    
    @MockBean
    private com.bookstore.security.JwtUtils jwtUtils;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private BookDTO testBookDTO;
    
    @BeforeEach
    void setUp() {
        testBookDTO = new BookDTO("1234567890", "Test Book", "Test Author", 2023);
        
        // Configure mocked security components for @PreAuthorize to work
        when(jwtUtils.validateJwtToken(any())).thenReturn(true);
        when(jwtUtils.getUserNameFromJwtToken(any())).thenReturn("testuser");
    }
    
    @Test
    @WithMockUser
    void getAllBooks_ShouldReturnListOfBooks() throws Exception {
        // Given
        List<BookDTO> books = Arrays.asList(testBookDTO);
        when(bookService.getAllBooks()).thenReturn(books);
        
        // When & Then
        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].isbn").value("1234567890"))
                .andExpect(jsonPath("$[0].title").value("Test Book"));
        
        verify(bookService, times(1)).getAllBooks();
    }
    
    @Test
    @WithMockUser
    void getBookByIsbn_ShouldReturnBook() throws Exception {
        // Given
        when(bookService.getBookByIsbn("1234567890")).thenReturn(testBookDTO);
        
        // When & Then
        mockMvc.perform(get("/api/books/1234567890"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isbn").value("1234567890"))
                .andExpect(jsonPath("$.title").value("Test Book"));
        
        verify(bookService, times(1)).getBookByIsbn("1234567890");
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createBook_WithValidData_ShouldCreateBook() throws Exception {
        // Given
        when(bookService.createBook(any(BookDTO.class))).thenReturn(testBookDTO);
        
        // When & Then
        mockMvc.perform(post("/api/books")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.isbn").value("1234567890"))
                .andExpect(jsonPath("$.title").value("Test Book"));
        
        verify(bookService, times(1)).createBook(any(BookDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_WithValidData_ShouldUpdateBook() throws Exception {
        // Given
        BookDTO updatedBookDTO = new BookDTO("1234567890", "Updated Title", "Updated Author", 2024);
        when(bookService.updateBook(eq("1234567890"), any(BookDTO.class))).thenReturn(updatedBookDTO);
        
        // When & Then
        mockMvc.perform(put("/api/books/1234567890")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updatedBookDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Updated Title"));
        
        verify(bookService, times(1)).updateBook(eq("1234567890"), any(BookDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_ShouldDeleteBook() throws Exception {
        // Given
        doNothing().when(bookService).deleteBook("1234567890");
        
        // When & Then
        mockMvc.perform(delete("/api/books/1234567890")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Book deleted successfully"));
        
        verify(bookService, times(1)).deleteBook("1234567890");
    }
    
    @Test
    void createBook_WithoutAuthentication_ShouldReturn401() throws Exception {
        // When & Then
        mockMvc.perform(post("/api/books")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isUnauthorized());
        
        verify(bookService, never()).createBook(any(BookDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void updateBook_WithUserRole_ShouldReturn403() throws Exception {
        // Given - no mocking needed since service should not be called
        
        // When & Then
        mockMvc.perform(put("/api/books/1234567890")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isForbidden());
        
        // Security should prevent the service from being called
        verify(bookService, never()).updateBook(anyString(), any(BookDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void deleteBook_WithUserRole_ShouldReturn403() throws Exception {
        // Given - no mocking needed since service should not be called
        
        // When & Then
        mockMvc.perform(delete("/api/books/1234567890")
                .with(csrf()))
                .andExpect(status().isForbidden());
        
        // Security should prevent the service from being called
        verify(bookService, never()).deleteBook(anyString());
    }
}
