import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { Http } from '@angular/http';
import { AppCoreDataService } from './services/app-core-data.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  private appVersion: string;
  private appName: string;
  private title: string = 'POC-WEB-Planetary Optimizer';

  constructor(private http: Http, private appCoreData:AppCoreDataService) { }

  ngOnInit() {
    this.http.get('./package.json')
      .map(res => res.json())
      .subscribe(data => {
        this.appVersion = data.version;
        this.appCoreData.setVersion(data.version);
      }
      );
    this.http.get('./package.json')
      .map(res => res.json())
      .subscribe(data => this.appName = data.name);
      this.appCoreData.setTitle(this.title);
  }
}
