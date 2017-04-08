import { Component, OnInit }            from '@angular/core';
import { AppCoreDataService }           from '../services/app-core-data.service';
import { PlanetaryResourceListService } from '../services/planetary-resource-list.service';
import { PlanetaryResource }            from '../models/planetary-resource';

@Component({
  selector: 'app-prlist',
  templateUrl: './prlist.component.html',
  styleUrls: ['./prlist.component.css']
})
export class PRListComponent implements OnInit {
  private contents: PlanetaryResource[];
  private showNewresourceForm: boolean = false;
  private newResource: PlanetaryResource = new PlanetaryResource();
  public typeid: number;

  constructor(private resourceListService: PlanetaryResourceListService) { }

  ngOnInit() {
    // Copy the list of Planetry Resources fromt the core service.
    this.contents = this.resourceListService.getPRList();
  }
  public addResource() {
    // Open the new resource component to enter the forms data
    this.newResource = new PlanetaryResource();
    this.showNewresourceForm = true;
  }
  public removeResource(target: PlanetaryResource) {
    console.log("--[PRListComponent.removeResource]> newres: " + JSON.stringify(target));
    this.resourceListService.removeResource(target);
  }
  public onSubmit() {
    console.log(">>[PRListComponent.onSubmit]");
    // Add the new resource to the list of resources.
    this.resourceListService.addResource(this.newResource);
    // Hide again the new resource form.
    this.showNewresourceForm = false;
    // Reload the list of resources.
    this.contents = this.resourceListService.getPRList();
  }
}
