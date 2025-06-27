package com.bookstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "books")
public class Book {
    
    @Id
    @Column(unique = true, nullable = false)
    @NotBlank(message = "ISBN is required")
    @Pattern(regexp = "^\\d{10}(\\d{3})?$", message = "ISBN must be 10 or 13 digits")
    private String isbn;
    
    @Column(nullable = false)
    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;
    
    @Column(nullable = false)
    @NotBlank(message = "Author is required")
    @Size(max = 255, message = "Author must not exceed 255 characters")
    private String author;
    
    @Column(name = "publication_year", nullable = false)
    @NotNull(message = "Publication year is required")
    @Min(value = 1000, message = "Publication year must be a valid year")
    @Max(value = 2030, message = "Publication year cannot be in the future")
    private Integer publicationYear;
    
    // Default constructor
    public Book() {}
    
    // Constructor with parameters
    public Book(String isbn, String title, String author, Integer publicationYear) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
    }
    
    // Getters and Setters
    public String getIsbn() {
        return isbn;
    }
    
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public Integer getPublicationYear() {
        return publicationYear;
    }
    
    public void setPublicationYear(Integer publicationYear) {
        this.publicationYear = publicationYear;
    }
    
    @Override
    public String toString() {
        return "Book{" +
                "isbn='" + isbn + '\'' +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", publicationYear=" + publicationYear +
                '}';
    }
}
