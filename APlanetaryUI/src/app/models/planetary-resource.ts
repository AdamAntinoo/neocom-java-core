import { OnInit } from '@angular/core';
import { ReflectiveInjector } from '@angular/core';
import { EveItemService } from '../services/eve-item.service';

export class PlanetaryResource {
  static eveItemService: EveItemService = null; // ReflectiveInjector.resolveAndCreate([EveItemService]).get(EveItemService);

  private typeid: number = 34;
  private name: string = "<NO NAME>";
  private quantity: number = 0;

  constructor(resourceid: number, quantity: number) {
    // Check global existence of the Service. If not instantiated then create it.
    // if (null == PlanetaryResource.eveItemService) {
    //   let injector = ReflectiveInjector.resolveAndCreate([EveItemService]);
    //   PlanetaryResource.eveItemService = injector.get(EveItemService);
    // }
    this.name = "UNDEFINED";
    this.typeid = resourceid;
    this.quantity = quantity;
    // Get the item information from the backend.
    // var item = PlanetaryResource.eveItemService.getEveItem(resourceid);
    // this.name = item["name"];
  }
  public getName() {
    return this.name;
  }
  public getQuantity() {
    return this.quantity;
  }
  public setName(newname: string) {
    this.name = newname;
  }
  public setQuantity(newq: number) {
    this.quantity = newq;
  }
  // ngOnInit() {
  //   let injector = ReflectiveInjector.resolveAndCreate([EveItemService]);
  //   this.eveItemService = injector.get(EveItemService);
  // }
}
