import { Injectable } from '@angular/core';
import { PlanetaryResource } from '../models/planetary-resource';

@Injectable()
export class PlanetaryResourceListService {
  private prList: PlanetaryResource[] = [];

  constructor() { }
  public getPRList() {
    return this.prList;
  }
  public addResource(newres: PlanetaryResource) {
    // Check if this resource already exists. If so add them. Otherwise add the resource.
    console.log("--[PlanetaryResourceListService.addResource]> newres: " + JSON.stringify(newres));
    let hit = this.search4id(newres.getId());
    console.log("--[PlanetaryResourceListService.addResource]> hit: " + JSON.stringify(hit));
    if (undefined == hit) {
      this.prList.push(newres);
    } else {
      hit.setQuantity(hit.getQuantity() + newres.getQuantity());
    }
    let counter = this.prList.length;
  }
  public removeResource(target: PlanetaryResource) {
    console.log("--[PlanetaryResourceListService.removeResource]> newres: " + JSON.stringify(target));
    let hit = this.search4id(target.getId());
    if (undefined != hit) {
      var index = this.prList.indexOf(target, 0);
      if (index > -1) {
        this.prList.splice(index, 1);
      }
    }
  }
  public savePRList(newList: PlanetaryResource[]) {

  }
  private search4id(targetid: number) {
    for (let res of this.prList) {
      if (res.getId() == targetid) return res;
    }
    return undefined;
  }
}
