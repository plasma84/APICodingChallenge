package com.bookstore.integration;

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.repository.BookRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class BookIntegrationTest {
    
    private MockMvc mockMvc;
    
    @Autowired
    private WebApplicationContext context;
    
    @Autowired
    private BookRepository bookRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private Book testBook;
    
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
                
        bookRepository.deleteAll();
        testBook = new Book("1234567890", "Integration Test Book", "Test Author", 2023);
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createAndRetrieveBook_ShouldWork() throws Exception {
        // Create a book
        BookDTO bookDTO = new BookDTO("9876543210", "New Book", "New Author", 2024);
        
        mockMvc.perform(post("/api/books")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.isbn").value("9876543210"))
                .andExpect(jsonPath("$.title").value("New Book"));
        
        // Retrieve the book
        mockMvc.perform(get("/api/books/9876543210"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.isbn").value("9876543210"))
                .andExpect(jsonPath("$.title").value("New Book"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void fullCrudOperations_ShouldWork() throws Exception {
        // Save a book directly to repository
        bookRepository.save(testBook);
        
        // Read
        mockMvc.perform(get("/api/books/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Integration Test Book"));
        
        // Update
        BookDTO updateDTO = new BookDTO("1234567890", "Updated Title", "Updated Author", 2023);
        
        mockMvc.perform(put("/api/books/1234567890")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
        
        // Verify update
        mockMvc.perform(get("/api/books/1234567890"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Title"));
        
        // Delete
        mockMvc.perform(delete("/api/books/1234567890")
                .with(csrf()))
                .andExpect(status().isOk());
        
        // Verify deletion
        mockMvc.perform(get("/api/books/1234567890"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    @WithMockUser(roles = "USER")
    void createBookWithDuplicateIsbn_ShouldReturnConflict() throws Exception {
        // Save a book directly to repository
        bookRepository.save(testBook);
        
        // Try to create another book with same ISBN
        BookDTO duplicateBook = new BookDTO("1234567890", "Duplicate Book", "Duplicate Author", 2024);
        
        mockMvc.perform(post("/api/books")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicateBook)))
                .andExpect(status().isConflict());
    }
}
