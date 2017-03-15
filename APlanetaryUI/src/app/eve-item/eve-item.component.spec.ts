import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { EveItemComponent } from './eve-item.component';

describe('EveItemComponent', () => {
  let component: EveItemComponent;
  let fixture: ComponentFixture<EveItemComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ EveItemComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(EveItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
