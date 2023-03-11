import { TestBed } from '@angular/core/testing';

import { YelpAjaxService } from './yelp-ajax.service';

describe('YelpAjaxService', () => {
  let service: YelpAjaxService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(YelpAjaxService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
