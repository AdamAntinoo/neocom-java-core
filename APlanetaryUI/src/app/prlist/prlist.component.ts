import { Component }                    from '@angular/core';
import { OnInit }                       from '@angular/core';
import { AppCoreDataService }           from '../services/app-core-data.service';
import { PlanetaryResourceListService } from '../services/planetary-resource-list.service';
import { PlanetaryResource }            from '../models/planetary-resource';

@Component({
  selector: 'app-prlist',
  templateUrl: './prlist.component.html',
  styleUrls: ['./prlist.component.css']
})
export class PRListComponent implements OnInit {
  public listTitle: string = "<TITLE>";

  private prList: PlanetaryResource[] = [];
  private showNewresourceForm: boolean = false;
  private newResource: PlanetaryResource = new PlanetaryResource();
  public typeid: number;

  constructor(private resourceListService: PlanetaryResourceListService) { }

  ngOnInit() {
    // Read the list of Planetry Resources fromt the backend service.
    this.prList = this.resourceListService.getPRList();
  }
  public addNewResource() {
    // Open the new resource component to enter the forms data
    this.newResource = new PlanetaryResource();
    this.showNewresourceForm = true;
  }
  public removeOldResource(target: PlanetaryResource) {
    console.log("--[PRListComponent.removeResource]> newres: " + JSON.stringify(target));
    this.removeResource(target);
  }
  public onSubmit() {
    console.log(">>[PRListComponent.onSubmit]");
    // Add the new resource to the list of resources.
    this.addResource(this.newResource);
    // Hide again the new resource form.
    this.showNewresourceForm = false;

    // Save the new list of resources on the backend list.
    this.resourceListService.savePRList(this.prList);
  }
  private addResource(newres: PlanetaryResource) {
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
  private removeResource(target: PlanetaryResource) {
    console.log("--[PlanetaryResourceListService.removeResource]> newres: " + JSON.stringify(target));
    let hit = this.search4id(target.getId());
    if (undefined != hit) {
      var index = this.prList.indexOf(target, 0);
      if (index > -1) {
        this.prList.splice(index, 1);
      }
    }
  }
  private search4id(targetid: number) {
    for (let res of this.prList) {
      if (res.getId() == targetid) return res;
    }
    return undefined;
  }
}
