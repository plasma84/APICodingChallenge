package com.bookstore.config;

import com.bookstore.entity.Book;
import com.bookstore.entity.User;
import com.bookstore.repository.BookRepository;
import com.bookstore.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {
    
    @Bean
    public CommandLineRunner initData(BookRepository bookRepository, 
                                    UserRepository userRepository,
                                    PasswordEncoder passwordEncoder) {
        return args -> {
            // Create admin user
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User(
                    "admin",
                    passwordEncoder.encode("admin123"),
                    "admin@bookstore.com",
                    User.Role.ADMIN
                );
                userRepository.save(admin);
            }
            
            // Create regular user
            if (!userRepository.existsByUsername("user")) {
                User user = new User(
                    "user",
                    passwordEncoder.encode("user123"),
                    "user@bookstore.com",
                    User.Role.USER
                );
                userRepository.save(user);
            }
            
            // Create sample books
            if (bookRepository.count() == 0) {
                bookRepository.save(new Book("9780743273565", "The Great Gatsby", "F. Scott Fitzgerald", 1925));
                bookRepository.save(new Book("9780061120084", "To Kill a Mockingbird", "Harper Lee", 1960));
                bookRepository.save(new Book("9780451524935", "1984", "George Orwell", 1949));
                bookRepository.save(new Book("9780140283334", "Brave New World", "Aldous Huxley", 1932));
                bookRepository.save(new Book("9780316769488", "The Catcher in the Rye", "J.D. Salinger", 1951));
            }
        };
    }
}
