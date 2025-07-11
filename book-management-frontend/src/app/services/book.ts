import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Book {
  isbn: number;
  title: string;
  author: string;
  publicationYear: number;
}

@Injectable({
  providedIn: 'root'
})
export class BookService {
  
  private readonly baseUrl = '/api/books';

  constructor(private readonly http: HttpClient) {}

  // Get all books
  getAllBooks(): Observable<Book[]> {
    console.log('BookService: Getting all books from', `${this.baseUrl}/all`);
    return this.http.get<Book[]>(`${this.baseUrl}/all`);
  }

  // Get book by ISBN
  getBookByIsbn(isbn: number): Observable<Book> {
    console.log('BookService: Getting book by ISBN', isbn);
    return this.http.get<Book>(`${this.baseUrl}/${isbn}`);
  }

  // Add new book
  addBook(book: Book): Observable<Book> {
    console.log('BookService: Adding book', book);
    return this.http.post<Book>(this.baseUrl, book);
  }

  // Update book
  updateBook(isbn: number, book: Book): Observable<Book> {
    console.log('BookService: Updating book', isbn, book);
    return this.http.put<Book>(`${this.baseUrl}/${isbn}`, book);
  }

  // Delete book
  deleteBook(isbn: number): Observable<any> {
    console.log('BookService: Deleting book with ISBN', isbn);
    return this.http.delete(`${this.baseUrl}/${isbn}`);
  }
}
