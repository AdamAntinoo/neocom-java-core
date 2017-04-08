//	PROJECT:     POC-ASB-Planetary (POC.ASBP)
//	AUTHORS:     Adam Antinoo - adamantinoo.git@gmail.com
//	COPYRIGHT:   (c) 2017 by Dimensinfin Industries, all rights reserved.
//	ENVIRONMENT: Angular
//	DESCRIPTION: Stores the interchange date for the application until I know a better way to share that
// information.

import { Injectable } from '@angular/core';
import { PlanetaryResource } from '../models/planetary-resource';
import { PlanetaryResourceListService }from '../services/planetary-resource-list.service';

@Injectable()
export class AppCoreDataService {
  //--- Application Globals
  private title: string = "<TITLE>";
//  private prList: PlanetaryResource[] = [];
  private version: String;

  constructor(private resourceListService:PlanetaryResourceListService) { }
  public setTitle(newtitle: string) {
    this.title = newtitle;
  }
  public getTitle() {
    return this.title;
  }
  public getVersion() {
    return this.version;
  }
  public setVersion(newversion:string){
    this.version=newversion;
  }
}
