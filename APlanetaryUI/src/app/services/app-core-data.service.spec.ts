import { TestBed, inject } from '@angular/core/testing';

import { AppCoreDataService } from './app-core-data.service';

describe('AppCoreDataService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AppCoreDataService]
    });
  });

  it('should ...', inject([AppCoreDataService], (service: AppCoreDataService) => {
    expect(service).toBeTruthy();
  }));
});
