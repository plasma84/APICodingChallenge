import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookService, Book } from '../../services/book';

@Component({
  selector: 'app-book-detail',
  standalone: false,
  templateUrl: './book-detail.html',
  styleUrl: './book-detail.css'
})
export class BookDetail implements OnInit {
  book: Book | null = null;
  loading = false;
  error = '';
  isbn: number = 0;

  constructor(
    private bookService: BookService,
    private route: ActivatedRoute,
    private router: Router,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit() {
    console.log('BookDetail component initialized');
    this.isbn = Number(this.route.snapshot.paramMap.get('isbn'));
    console.log('ISBN from route:', this.isbn);
    console.log('Initial component state:', {
      loading: this.loading,
      error: this.error,
      book: this.book
    });
    
    if (this.isbn) {
      this.loadBook();
    } else {
      this.error = 'Invalid book ISBN';
      console.log('Invalid ISBN, setting error');
    }
  }

  loadBook() {
    console.log('Starting to load book details for ISBN:', this.isbn);
    this.loading = true;
    this.error = '';
    this.book = null; // Reset book data

    this.bookService.getBookByIsbn(this.isbn).subscribe({
      next: (book: Book) => {
        console.log('Book details loaded successfully:', book);
        this.book = book;
        this.loading = false;
        console.log('Component state after loading:', {
          loading: this.loading,
          error: this.error,
          book: this.book,
          showDetails: !this.loading && !this.error && !!this.book
        });
        this.cdr.detectChanges(); // Force change detection
      },
      error: (err: any) => {
        console.error('Error loading book details:', err);
        this.error = 'Failed to load book details';
        this.loading = false;
        this.book = null;
        console.log('Error state:', {
          loading: this.loading,
          error: this.error,
          book: this.book
        });
        this.cdr.detectChanges();
      }
    });
  }

  editBook() {
    this.router.navigate(['/books/edit', this.isbn]);
  }

  goBack() {
    this.router.navigate(['/books']);
  }
}
