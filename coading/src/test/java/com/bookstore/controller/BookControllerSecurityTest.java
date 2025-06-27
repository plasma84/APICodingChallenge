package com.bookstore.controller;

import com.bookstore.dto.BookDTO;
import com.bookstore.service.BookService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class BookControllerSecurityTest {
    
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private BookService bookService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private BookDTO testBookDTO;
    
    @BeforeEach
    void setUp() {
        testBookDTO = new BookDTO("1234567890", "Test Book", "Test Author", 2023);
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
        // Given - Configure the mock to avoid NullPointerException if somehow called
        BookDTO responseDTO = new BookDTO("1234567890", "Mock Response", "Mock Author", 2023);
        when(bookService.updateBook(anyString(), any(BookDTO.class))).thenReturn(responseDTO);
        
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
        // Given - Configure the mock to avoid exceptions if somehow called
        doNothing().when(bookService).deleteBook(anyString());
        
        // When & Then
        mockMvc.perform(delete("/api/books/1234567890")
                .with(csrf()))
                .andExpect(status().isForbidden());
        
        // Security should prevent the service from being called
        verify(bookService, never()).deleteBook(anyString());
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBook_WithAdminRole_ShouldReturn200() throws Exception {
        // Given
        BookDTO updatedBookDTO = new BookDTO("1234567890", "Updated Book", "Updated Author", 2024);
        when(bookService.updateBook(eq("1234567890"), any(BookDTO.class))).thenReturn(updatedBookDTO);
        
        // When & Then
        mockMvc.perform(put("/api/books/1234567890")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testBookDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Book"));
        
        verify(bookService, times(1)).updateBook(eq("1234567890"), any(BookDTO.class));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBook_WithAdminRole_ShouldReturn200() throws Exception {
        // Given
        doNothing().when(bookService).deleteBook("1234567890");
        
        // When & Then
        mockMvc.perform(delete("/api/books/1234567890")
                .with(csrf()))
                .andExpect(status().isOk());
        
        verify(bookService, times(1)).deleteBook("1234567890");
    }
}
