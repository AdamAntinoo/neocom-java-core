// PROJECT:     POC-ASB-Planetary (POC.ASBP)
// AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
// COPYRIGHT:   (c) 2017 by Dimensinfin Industries, all rights reserved.
// ENVIRONMENT: Angular - CLASS
// DESCRIPTION: Defines the PlanetaryResource equivalent to store the data forr a sresource.
import { ReflectiveInjector } from '@angular/core';
import { EveItemService } from '../services/eve-item.service';

export class PlanetaryResource {
  private typeid: number;
  private quantity: number;
  private name: string = "<NAME>";
  static eveItemService: EveItemService = null; // ReflectiveInjector.resolveAndCreate([EveItemService]).get(EveItemService);

  // constructor(private newresourceid: number, private newquantity: number) {
  //   this.typeid = newresourceid;
  //   this.quantity = newquantity;
  // }
  constructor() { }

  public getId(): number {
    return this.typeid;
  }
  public getName(): string {
    return this.name;
  }
  public getQuantity(): number {
    return this.quantity;
  }
  public setName(newname: string) {
    this.name = newname;
  }
  public setQuantity(newq: number) {
    this.quantity = newq;
  }
}
