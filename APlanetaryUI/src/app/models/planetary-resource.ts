import { EveItemService } from '../services/eve-item.service';

export class PlanetaryResource {
  private typeid: number = 34;
  private name: string = "<NO NAME>";
  private quantity: number = 0;

  constructor(resourceid: number, quantity: number) {
    // Get the item information from the backend.
    //  var item = this.eveItemService.getEveItem(resourceid);
    this.name = "UNDEFINED";
    this.typeid = resourceid;
    this.quantity = quantity;
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
}
