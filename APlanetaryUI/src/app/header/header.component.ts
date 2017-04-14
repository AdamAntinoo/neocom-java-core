import { Component, OnInit } from '@angular/core';
import { AppCoreDataService } from '../services/app-core-data.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
  public version: string = "<VERSION>";
  constructor(private appCoreData: AppCoreDataService) { }

  ngOnInit() {
    this.version = this.appCoreData.getVersion();
  }

}
