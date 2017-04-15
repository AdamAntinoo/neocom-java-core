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

  constructor(private http: Http) { }

  public getPRList(name: string) {
    // Read from the backend the list with this name.
    return this.http.get("http://localhost:8090/api/v1/resourcelist/" + name)
      .map(res => res.json())
      .map(result => {
        console.log("--[PlanetaryResourceListService.getPRList.map]> result: " + JSON.stringify(result));
        return result;
      }
      );
  }
  public savePRList(name: string, newList: PlanetaryResource[]) {
    let prListData = { name: name, data: newList };
    this.http.post("http://localhost:8090/api/v1/addresourcelist", prListData)
      .subscribe(function() {
        console.log("createProject called successfully")
      });
    //  .catch((error: any) => Observable.throw(error.json().error || 'Server error'))
  }
  // public addResource(newres: PlanetaryResource) {
  //   // Check if this resource already exists. If so add them. Otherwise add the resource.
  //   console.log("--[PlanetaryResourceListService.addResource]> newres: " + JSON.stringify(newres));
  //   let hit = this.search4id(newres.getId());
  //   console.log("--[PlanetaryResourceListService.addResource]> hit: " + JSON.stringify(hit));
  //   if (undefined == hit) {
  //     this.prList.push(newres);
  //   } else {
  //     hit.setQuantity(hit.getQuantity() + newres.getQuantity());
  //   }
  //   let counter = this.prList.length;
  // }
  // public removeResource(target: PlanetaryResource) {
  //   console.log("--[PlanetaryResourceListService.removeResource]> newres: " + JSON.stringify(target));
  //   let hit = this.search4id(target.getId());
  //   if (undefined != hit) {
  //     var index = this.prList.indexOf(target, 0);
  //     if (index > -1) {
  //       this.prList.splice(index, 1);
  //     }
  //   }
  // }
  // private search4id(targetid: number) {
  //   for (let res of this.prList) {
  //     if (res.getId() == targetid) return res;
  //   }
  //   return undefined;
  // }
}
