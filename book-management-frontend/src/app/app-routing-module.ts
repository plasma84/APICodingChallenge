import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login';
import { BookList } from './components/book-list/book-list';
import { BookAdd } from './components/book-add/book-add';
import { BookUpdate } from './components/book-update/book-update';
import { BookDetail } from './components/book-detail/book-detail';

const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'books', component: BookList },
  { path: 'books/add', component: BookAdd },
  { path: 'books/edit/:isbn', component: BookUpdate },
  { path: 'books/detail/:isbn', component: BookDetail }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {}
