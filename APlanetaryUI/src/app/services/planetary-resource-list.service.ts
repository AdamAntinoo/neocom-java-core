import { Injectable }                         from '@angular/core';
import { Http }                               from '@angular/http';
import { Response, Headers, RequestOptions }  from '@angular/http';
//import { URLSearchParams, Jsonp }             from '@angular/http';
import { Observable }                         from 'rxjs/Rx';
import { EveItem }                            from '../models/eve-item';
// Import RxJs required methods
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/catch';

import { PlanetaryResource } from '../models/planetary-resource';

@Injectable()
export class PlanetaryResourceListService {
  static RESOURCE_SERVICE_URL: string = "http://localhost:8090/api/v1";

  constructor(private http: Http) { }

  public getPRList(name: string) {
    // Read from the backend the list with this name.
    return this.http.get(PlanetaryResourceListService.RESOURCE_SERVICE_URL + "/resourcelist/" + name)
      .map(res => res.json())
      .map(result => {
        console.log("--[PlanetaryResourceListService.getPRList.map]> result: " + JSON.stringify(result));
        return result;
      });
  }
  // public savePRList(name: string, newList: PlanetaryResource[]) {
  //   let prListData = { name: name, data: newList };
  //   this.http.post("http://localhost:8090/api/v1/addresourcelist", prListData)
  //     .subscribe(function() {
  //       console.log("createProject called successfully")
  //     });
  //   //  .catch((error: any) => Observable.throw(error.json().error || 'Server error'))
  // }
  public deleteResource(listName: string, resourceId: number) {
    console.log(">>[PlanetaryResourceListService.deleteResource]")
    console.log("--[PlanetaryResourceListService.deleteResource]> listName: " + listName)
    console.log("--[PlanetaryResourceListService.deleteResource]> resourceId: " + resourceId)
    return this.http.get(PlanetaryResourceListService.RESOURCE_SERVICE_URL + "/deleteResource/" + listName + "/" + resourceId)
      .map(res => res.json())
      .map(result => {
        console.log("--[PlanetaryResourceListService.getPRList.map]> result: " + JSON.stringify(result));
        return result;
      });
  }
  public searchTypeName(id: number) {
    return this.http.get(PlanetaryResourceListService.RESOURCE_SERVICE_URL + "/eveitem/" + id)
      .map(res => res.json())
      .map(result => {
        console.log("--[PlanetaryResourceListService.searchTypeName.map]> result: " + JSON.stringify(result));
        return result;
      });
    //    .catch(res => Observable.throw(res.json()));
  }
  public addResource2List(name: string, newResource: PlanetaryResource) {
    let prListData = { name: name, data: newResource };
    return this.http.post(PlanetaryResourceListService.RESOURCE_SERVICE_URL + "/newresource/", prListData)
      .map(res => res.json())
      .map(result => {
        console.log("--[PlanetaryResourceListService.addResource2List.map]> result: " + JSON.stringify(result));
        return result;
      });      
    //    .catch(res => Observable.throw(res.json()));
  }
}
