import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { BookService, Book } from '../../services/book';

@Component({
  selector: 'app-book-add',
  standalone: false,
  templateUrl: './book-add.html',
  styleUrl: './book-add.css'
})
export class BookAdd {
  book: Book = {
    isbn: 0,
    title: '',
    author: '',
    publicationYear: new Date().getFullYear()
  };
  
  loading = false;
  error = '';

  constructor(
    private bookService: BookService,
    private router: Router
  ) {}

  onSubmit() {
    if (this.validateForm()) {
      this.loading = true;
      this.error = '';

      this.bookService.addBook(this.book).subscribe({
        next: (response: Book) => {
          console.log('Book added successfully:', response);
          this.router.navigate(['/books']);
        },
        error: (err: any) => {
          console.error('Error adding book:', err);
          this.error = 'Failed to add book. Please try again.';
          this.loading = false;
        }
      });
    }
  }

  validateForm(): boolean {
    if (!this.book.isbn || !this.book.title || !this.book.author || !this.book.publicationYear) {
      this.error = 'All fields are required.';
      return false;
    }
    if (this.book.isbn <= 0) {
      this.error = 'ISBN must be a positive number.';
      return false;
    }
    if (this.book.publicationYear < 1000 || this.book.publicationYear > new Date().getFullYear()) {
      this.error = 'Publication year must be valid.';
      return false;
    }
    return true;
  }

  cancel() {
    this.router.navigate(['/books']);
  }
}
