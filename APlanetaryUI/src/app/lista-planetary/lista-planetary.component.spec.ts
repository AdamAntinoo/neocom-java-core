import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListaPlanetaryComponent } from './lista-planetary.component';

describe('ListaPlanetaryComponent', () => {
  let component: ListaPlanetaryComponent;
  let fixture: ComponentFixture<ListaPlanetaryComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListaPlanetaryComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListaPlanetaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
