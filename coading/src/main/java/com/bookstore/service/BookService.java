package com.bookstore.service;

import com.bookstore.dto.BookDTO;
import com.bookstore.entity.Book;
import com.bookstore.exception.BookNotFoundException;
import com.bookstore.exception.DuplicateIsbnException;
import com.bookstore.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class BookService {
    
    private final BookRepository bookRepository;
    
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }
    
    public List<BookDTO> getAllBooks() {
        return bookRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .toList();
    }
    
    public BookDTO getBookByIsbn(String isbn) {
        Book book = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
        return convertToDTO(book);
    }
    
    public BookDTO createBook(BookDTO bookDTO) {
        if (bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateIsbnException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }
        
        Book book = convertToEntity(bookDTO);
        Book savedBook = bookRepository.save(book);
        return convertToDTO(savedBook);
    }
    
    public BookDTO updateBook(String isbn, BookDTO bookDTO) {
        Book existingBook = bookRepository.findByIsbn(isbn)
                .orElseThrow(() -> new BookNotFoundException("Book not found with ISBN: " + isbn));
        
        // Check if the new ISBN conflicts with another book
        if (!isbn.equals(bookDTO.getIsbn()) && bookRepository.existsByIsbn(bookDTO.getIsbn())) {
            throw new DuplicateIsbnException("Book with ISBN " + bookDTO.getIsbn() + " already exists");
        }
        
        existingBook.setIsbn(bookDTO.getIsbn());
        existingBook.setTitle(bookDTO.getTitle());
        existingBook.setAuthor(bookDTO.getAuthor());
        existingBook.setPublicationYear(bookDTO.getPublicationYear());
        
        Book updatedBook = bookRepository.save(existingBook);
        return convertToDTO(updatedBook);
    }
    
    public void deleteBook(String isbn) {
        if (!bookRepository.existsByIsbn(isbn)) {
            throw new BookNotFoundException("Book not found with ISBN: " + isbn);
        }
        bookRepository.deleteById(isbn);
    }
    
    private BookDTO convertToDTO(Book book) {
        return new BookDTO(
            book.getIsbn(),
            book.getTitle(),
            book.getAuthor(),
            book.getPublicationYear()
        );
    }
    
    private Book convertToEntity(BookDTO bookDTO) {
        return new Book(
            bookDTO.getIsbn(),
            bookDTO.getTitle(),
            bookDTO.getAuthor(),
            bookDTO.getPublicationYear()
        );
    }
}
