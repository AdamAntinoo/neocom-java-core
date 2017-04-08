import { Component, OnInit } from '@angular/core';
import { PlanetaryResource } from '../models/planetary-resource';
import { EveItemService } from '../services/eve-item.service';

@Component({
  selector: 'app-lista-planetary',
  templateUrl: './lista-planetary.component.html',
  styleUrls: ['./lista-planetary.component.css']
})
export class ListaPlanetaryComponent implements OnInit {
  public item: PlanetaryResource;
  public planetaryResources: PlanetaryResource[] = [];

  constructor(private eveItemService: EveItemService) {
    this.eveItemService.getEveItem(2393)
      .subscribe(
      itemResponse => {
        // Do the actions of the completion of the subscription.
        let resource = new PlanetaryResource(/*2393.0, 199.0*/);
        if (null != itemResponse) {
          resource.setName(itemResponse["name"]);
        }
        this.planetaryResources.push(resource);
      },
      err => { console.log(err); }
      );

    this.eveItemService.getEveItem(2398)
      .subscribe(
      itemResponse => {
        // Do the actions of the completion of the subscription.
        let resource = new PlanetaryResource(/*2398, 299.0*/);
        if (null != itemResponse) {
          resource.setName(itemResponse["name"]);
        }
        this.planetaryResources.push(resource);
      },
      err => { console.log(err); }
      );

    this.eveItemService.getEveItem(2400)
      .subscribe(
      itemResponse => {
        // Do the actions of the completion of the subscription.
        let resource = new PlanetaryResource(/*2400, 399.0*/);
        if (null != itemResponse) {
          resource.setName(itemResponse["name"]);
        }
        this.planetaryResources.push(resource);
      },
      err => { console.log(err); }
      );
  }

  ngOnInit() {
    // this.item = new PlanetaryResource();
    // this.item.setName("<PRUEBA>");
    // this.item.setQuantity(123);
  }
  // addResource(resource: PlanetaryResource): ListaPlanetaryComponent {
  //   if (!todo.id) {
  //     todo.id = ++this.lastId;
  //   }
  //   this.todos.push(todo);
  //   return this;
  // }
}
