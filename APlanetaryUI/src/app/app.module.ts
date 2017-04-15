import { BrowserModule }        from '@angular/platform-browser';
import { NgModule }             from '@angular/core';
import { FormsModule }          from '@angular/forms';
import { HttpModule }           from '@angular/http';
import { RouterModule, Routes } from '@angular/router';
import { AppComponent }         from './app.component';
//--- SERVICES
import { AppCoreDataService }           from './services/app-core-data.service';
import { EveItemService }               from './services/eve-item.service';
import { PlanetaryResourceListService } from './services/planetary-resource-list.service';
//import { URLSearchParams, Jsonp }       from '@angular/http';
//--- COMPONENTS
import { PRListComponent } from './prlist/prlist.component';
import { HeaderComponent } from './header/header.component';

import { EveItemComponent }         from './eve-item/eve-item.component';
//import { ListaPlanetaryComponent }  from './lista-planetary/lista-planetary.component';

//--- ROUTES
const appRoutes: Routes = [
  { path: 'home', component: PRListComponent },
  {
    path: '',
    redirectTo: '/home',
    pathMatch: 'full'
  }
];

@NgModule({
  declarations: [
    AppComponent,
    EveItemComponent,
    //  ListaPlanetaryComponent,
    PRListComponent,
    HeaderComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    RouterModule.forRoot(appRoutes)
  ],
  providers: [
    //    Jsonp,
    AppCoreDataService,
    EveItemService,
    PlanetaryResourceListService
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
