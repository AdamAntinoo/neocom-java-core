import { Component, OnInit } from '@angular/core';
import { PlanetaryResource } from '../models/planetary-resource';

@Component({
  selector: 'app-lista-planetary',
  templateUrl: './lista-planetary.component.html',
  styleUrls: ['./lista-planetary.component.css']
})
export class ListaPlanetaryComponent implements OnInit {
  public item: PlanetaryResource;
  public planetaryResources: PlanetaryResource[];

  constructor() { }

  ngOnInit() {
    this.planetaryResources.push(new PlanetaryResource(2393, 199.0));
    // this.item = new PlanetaryResource();
    // this.item.setName("<PRUEBA>");
    // this.item.setQuantity(123);
  }

}
