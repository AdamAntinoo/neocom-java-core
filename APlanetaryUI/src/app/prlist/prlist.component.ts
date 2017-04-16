import { Component }                    from '@angular/core';
import { OnInit }                       from '@angular/core';
// import { OnChanges, SimpleChange }      from '@angular/core';
import { AppCoreDataService }           from '../services/app-core-data.service';
import { PlanetaryResourceListService } from '../services/planetary-resource-list.service';
import { PlanetaryResource }            from '../models/planetary-resource';

@Component({
  selector: 'app-prlist',
  templateUrl: './prlist.component.html',
  styleUrls: ['./prlist.component.css']
})
export class PRListComponent implements OnInit {
  public listTitle: string = "ResourceList";
  public prList: PlanetaryResource[] = [];
  public transformList: PlanetaryResource[] = [];
  public doingDownload: boolean = true;
  public idIsValid: boolean = false;

  private showNewResourceForm: boolean = false;
  private newResource: PlanetaryResource = new PlanetaryResource();
//  public typeid: number;

  constructor(private resourceListService: PlanetaryResourceListService) { }
  /**
  // This method is called during initialization of the component a single time. So the code should load the last list that was being used. We are going to use a backend service so this service that has persistence will be able to identify that last list and the return it as the initial list for the POC.
  */
  ngOnInit() {
    console.log(">>[PRListComponent.ngOnInit]");
    // Read the list of Planetry Resources from the backend service.
    this.resourceListService.getPRList(this.listTitle)
      .subscribe(result => {
        //    this.listTitle = result.name;
        console.log("--[PRListComponent.ngOnInit.subscribe]> setting name: " + JSON.stringify(result));
        let first = result["name"];
        let second = result["data"];
        console.log("--[PRListComponent.ngOnInit.subscribe]> first: " + JSON.stringify(first));
        console.log("--[PRListComponent.ngOnInit.subscribe]> second: " + JSON.stringify(second));
        // Convert the list of resource to the new list of resources.
        let list = [];
        for (let key of second) {
          let resource = new PlanetaryResource();
          resource.typeid = key.id;
          resource.setName(key.name);
          resource.setQuantity(key.quantity);
          list.push(resource);
        }
        // Set the conponent exported fields to the new data to update the ui.
        this.listTitle = first;
        this.prList = list;
      });
    console.log("<<[PRListComponent.ngOnInit]");
  }
  public newIdChange(event) {
    console.log(">>[PRListComponent.newIdChange]> event: " + JSON.stringify(event));
    // Search for the resource name that matches the new id.
    this.idIsValid = false;
    // Check the range for valid resource values
    if (event == 44) {
      this.resourceListService.searchTypeName(event)
        .subscribe(result => {
          console.log("--[PRListComponent.newIdChange]> result: " + JSON.stringify(result));
          let category = result.category;
          if (category == "Planetary Resources") this.idIsValid = true;
          if (category == "Planetary Commodities") this.idIsValid = true;
          if (this.idIsValid) this.newResource.name = result.name;
        });
    }
    if (event > 2072) {
      if (event < 28975) {
        this.resourceListService.searchTypeName(event)
          .subscribe(result => {
            console.log("--[PRListComponent.newIdChange]> result: " + JSON.stringify(result));
            let category = result.category;
            if (category == "Planetary Resources") this.idIsValid = true;
            if (category == "Planetary Commodities") this.idIsValid = true;
            if (this.idIsValid) this.newResource.name = result.name;
          });
      }
    }
  }
  public openNewResource() {
    // Open the new resource component to enter the forms data
    this.newResource = new PlanetaryResource();
    this.idIsValid = false;
    this.showNewResourceForm = true;
  }

  public deleteResource(target: PlanetaryResource) {
    console.log("--[PRListComponent.deleteResource]> target: " + JSON.stringify(target));
    // Clear the resoource from the resurce list.
    let hit = this.search4id(target.getTypeid());
    if (undefined != hit) {
      this.showNewResourceForm = false;
      var index = this.prList.indexOf(target, 0);
      if (index > -1) {
        this.prList.splice(index, 1);
      }
      // Send the deletion message to the backend.
      this.resourceListService.deleteResource(this.listTitle, hit.getTypeid());
    }
  }
  public onSubmit() {
    console.log(">>[PRListComponent.onSubmit]");
    // Check if this resource already exists. If so add them. Otherwise add the resource.
  //  this.newResource.typeid = this.typeid;
    let hit = this.search4id(this.newResource.getTypeid());
    console.log("--[PlanetaryResourceListService.addResource]> hit: " + JSON.stringify(hit));
    if (undefined == hit) {
      this.prList.push(this.newResource);
    } else {
      let newQty = hit.getQuantity() * 1.0;
      newQty = newQty + this.newResource.getQuantity() * 1.0;
      hit.setQuantity(newQty);
    }
    let counter = this.prList.length;
    // Hide again the new resource form.
    this.showNewResourceForm = false;

    // Save the new list of resources on the backend list.
    this.resourceListService.addResource2List(this.listTitle, this.newResource)
    .subscribe(result => {
      console.log("--[PRListComponent.onSubmit.subscribe]> result: " + JSON.stringify(result));
      this.transformList=[];
      for (let resource of result){
        let trresource = new PlanetaryResource();
        trresource.typeid=resource.typeID;
        trresource.quantity=resource.quantity;
        trresource.name=resource.name;
        this.transformList.push(trresource);
      }
    });
  }
  // private addResource(newres: PlanetaryResource) {
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
  private removeResource(target: PlanetaryResource) {
    console.log("--[PlanetaryResourceListService.removeResource]> newres: " + JSON.stringify(target));
    let hit = this.search4id(target.getTypeid());
    if (undefined != hit) {
      var index = this.prList.indexOf(target, 0);
      if (index > -1) {
        this.prList.splice(index, 1);
      }
    }
  }
  private search4id(targetid: number) {
    for (let res of this.prList) {
      if (res.getTypeid() == targetid) return res;
    }
    return undefined;
  }
}
