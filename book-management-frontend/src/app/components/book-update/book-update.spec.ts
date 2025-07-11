import { ComponentFixture, TestBed } from '@angular/core/testing';

import { BookUpdate } from './book-update';

describe('BookUpdate', () => {
  let component: BookUpdate;
  let fixture: ComponentFixture<BookUpdate>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [BookUpdate]
    })
    .compileComponents();

    fixture = TestBed.createComponent(BookUpdate);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
