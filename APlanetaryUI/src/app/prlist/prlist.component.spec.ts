import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PRListComponent } from './prlist.component';

describe('PRListComponent', () => {
  let component: PRListComponent;
  let fixture: ComponentFixture<PRListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PRListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PRListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
