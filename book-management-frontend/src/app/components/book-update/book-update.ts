import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookService, Book } from '../../services/book';

@Component({
  selector: 'app-book-update',
  standalone: false,
  templateUrl: './book-update.html',
  styleUrl: './book-update.css'
})
export class BookUpdate implements OnInit {
  book: Book = {
    isbn: 0,
    title: '',
    author: '',
    publicationYear: new Date().getFullYear()
  };
  
  loading = false;
  loadingBook = false;
  error = '';
  isbn: number = 0;

  constructor(
    private bookService: BookService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    console.log('BookUpdate component initialized');
    this.isbn = Number(this.route.snapshot.paramMap.get('isbn'));
    console.log('ISBN from route:', this.isbn);
    console.log('Initial component state:', {
      loadingBook: this.loadingBook,
      error: this.error,
      bookTitle: this.book.title
    });
    
    if (this.isbn) {
      this.loadBook();
    } else {
      this.error = 'Invalid book ISBN';
      console.log('Invalid ISBN, setting error');
    }
  }

  loadBook() {
    console.log('Starting to load book with ISBN:', this.isbn);
    this.loadingBook = true;
    this.error = '';

    this.bookService.getBookByIsbn(this.isbn).subscribe({
      next: (book: Book) => {
        console.log('Book loaded for editing:', book);
        this.book = { ...book }; // Create a copy to avoid direct mutation
        this.loadingBook = false;
        console.log('LoadingBook set to false. Current state:', {
          loadingBook: this.loadingBook,
          bookTitle: this.book.title,
          bookData: this.book
        });
        this.cdr.detectChanges(); // Force change detection
      },
      error: (err: any) => {
        console.error('Error loading book:', err);
        this.error = 'Failed to load book details';
        this.loadingBook = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSubmit() {
    if (this.validateForm()) {
      this.loading = true;
      this.error = '';

      this.bookService.updateBook(this.isbn, this.book).subscribe({
        next: (response: Book) => {
          console.log('Book updated successfully:', response);
          this.router.navigate(['/books']);
        },
        error: (err: any) => {
          console.error('Error updating book:', err);
          this.error = 'Failed to update book. Please try again.';
          this.loading = false;
        }
      });
    }
  }

  validateForm(): boolean {
    if (!this.book.title || !this.book.author || !this.book.publicationYear) {
      this.error = 'All fields are required.';
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
