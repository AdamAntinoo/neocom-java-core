import { TestBed, inject } from '@angular/core/testing';

import { PlanetaryResourceListService } from './planetary-resource-list.service';

describe('PlanetaryResourceListService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [PlanetaryResourceListService]
    });
  });

  it('should ...', inject([PlanetaryResourceListService], (service: PlanetaryResourceListService) => {
    expect(service).toBeTruthy();
  }));
});
