import { Injectable } from '@angular/core';
import { Http, Response, Headers, RequestOptions } from '@angular/http';
import { Observable } from 'rxjs/Rx';
import { EveItem } from '../models/eve-item';
// Import RxJs required methods
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

@Injectable()
export class EveItemService {

  constructor(private http: Http) { }
  /**
  Returns the instance of an EveItem from the CCP database for the required item identifier.
  */
  getEveItem(identifier: number): Observable<Response> {
    return this.http.get("http://localhost:8080/api/v1/eveitem/" + identifier)
      .map((res: Response) => res.json())
      .catch((error: any) => Observable.throw(error.json().error || 'Server error'))
  }
}
