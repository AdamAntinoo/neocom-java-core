import { Component } from '@angular/core';
import { OnInit } from '@angular/core';
import { Http } from '@angular/http';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  private appVersion: String;
  private appName: String;
  private title: String = 'app works!';

  constructor(private http: Http) { }

  ngOnInit() {
    this.http.get('./package.json')
      .map(res => res.json())
      .subscribe(data => this.appVersion = data.version);
    this.http.get('./package.json')
      .map(res => res.json())
      .subscribe(data => this.appName = data.name);
  }
}
