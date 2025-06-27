package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.DuplicateIsbnException;
import com.bookstore.repository.BookRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {
    
    @Mock
    private BookRepository bookRepository;
    
    @InjectMocks
    private BookService bookService;
    
    private Book testBook;
    private BookDTO testBookDTO;
    
    @BeforeEach
    void setUp() {
        testBook = new Book("1234567890", "Test Book", "Test Author", 2023);
        testBookDTO = new BookDTO("1234567890", "Test Book", "Test Author", 2023);
    }
    
    @Test
    void getAllBooks_ShouldReturnListOfBookDTOs() {
        // Given
        List<Book> books = Arrays.asList(testBook);
        when(bookRepository.findAll()).thenReturn(books);
        
        // When
        List<BookDTO> result = bookService.getAllBooks();
        
        // Then
        assertEquals(1, result.size());
        assertEquals(testBookDTO.getIsbn(), result.get(0).getIsbn());
        assertEquals(testBookDTO.getTitle(), result.get(0).getTitle());
        verify(bookRepository, times(1)).findAll();
    }
    
    @Test
    void getBookByIsbn_WhenBookExists_ShouldReturnBookDTO() {
        // Given
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(testBook));
        
        // When
        BookDTO result = bookService.getBookByIsbn("1234567890");
        
        // Then
        assertEquals(testBookDTO.getIsbn(), result.getIsbn());
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).findByIsbn("1234567890");
    }
    
    @Test
    void getBookByIsbn_WhenBookDoesNotExist_ShouldThrowBookNotFoundException() {
        // Given
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.getBookByIsbn("1234567890"));
        verify(bookRepository, times(1)).findByIsbn("1234567890");
    }
    
    @Test
    void createBook_WhenIsbnDoesNotExist_ShouldReturnCreatedBookDTO() {
        // Given
        when(bookRepository.existsByIsbn("1234567890")).thenReturn(false);
        when(bookRepository.save(any(Book.class))).thenReturn(testBook);
        
        // When
        BookDTO result = bookService.createBook(testBookDTO);
        
        // Then
        assertEquals(testBookDTO.getIsbn(), result.getIsbn());
        assertEquals(testBookDTO.getTitle(), result.getTitle());
        verify(bookRepository, times(1)).existsByIsbn("1234567890");
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    
    @Test
    void createBook_WhenIsbnAlreadyExists_ShouldThrowDuplicateIsbnException() {
        // Given
        when(bookRepository.existsByIsbn("1234567890")).thenReturn(true);
        
        // When & Then
        assertThrows(DuplicateIsbnException.class, () -> bookService.createBook(testBookDTO));
        verify(bookRepository, times(1)).existsByIsbn("1234567890");
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    void updateBook_WhenBookExists_ShouldReturnUpdatedBookDTO() {
        // Given
        BookDTO updatedBookDTO = new BookDTO("1234567890", "Updated Title", "Updated Author", 2024);
        Book updatedBook = new Book("1234567890", "Updated Title", "Updated Author", 2024);
        
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.of(testBook));
        when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
        
        // When
        BookDTO result = bookService.updateBook("1234567890", updatedBookDTO);
        
        // Then
        assertEquals(updatedBookDTO.getTitle(), result.getTitle());
        assertEquals(updatedBookDTO.getAuthor(), result.getAuthor());
        verify(bookRepository, times(1)).findByIsbn("1234567890");
        verify(bookRepository, times(1)).save(any(Book.class));
    }
    
    @Test
    void updateBook_WhenBookDoesNotExist_ShouldThrowBookNotFoundException() {
        // Given
        when(bookRepository.findByIsbn("1234567890")).thenReturn(Optional.empty());
        
        // When & Then
        assertThrows(BookNotFoundException.class, 
                    () -> bookService.updateBook("1234567890", testBookDTO));
        verify(bookRepository, times(1)).findByIsbn("1234567890");
        verify(bookRepository, never()).save(any(Book.class));
    }
    
    @Test
    void deleteBook_WhenBookExists_ShouldDeleteBook() {
        // Given
        when(bookRepository.existsByIsbn("1234567890")).thenReturn(true);
        
        // When
        bookService.deleteBook("1234567890");
        
        // Then
        verify(bookRepository, times(1)).existsByIsbn("1234567890");
        verify(bookRepository, times(1)).deleteById("1234567890");
    }
    
    @Test
    void deleteBook_WhenBookDoesNotExist_ShouldThrowBookNotFoundException() {
        // Given
        when(bookRepository.existsByIsbn("1234567890")).thenReturn(false);
        
        // When & Then
        assertThrows(BookNotFoundException.class, () -> bookService.deleteBook("1234567890"));
        verify(bookRepository, times(1)).existsByIsbn("1234567890");
        verify(bookRepository, never()).deleteById(anyString());
    }
}
