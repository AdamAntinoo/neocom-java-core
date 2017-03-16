import { TestBed, inject } from '@angular/core/testing';

import { EveItemServiceService } from './eve-item-service.service';

describe('EveItemServiceService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [EveItemServiceService]
    });
  });

  it('should ...', inject([EveItemServiceService], (service: EveItemServiceService) => {
    expect(service).toBeTruthy();
  }));
});
