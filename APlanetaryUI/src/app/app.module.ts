import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { AppComponent } from './app.component';
import { EveItemComponent } from './eve-item/eve-item.component';

import { EveItemService } from './services/eve-item.service';
import { ListaPlanetaryComponent } from './lista-planetary/lista-planetary.component';

@NgModule({
  declarations: [
    AppComponent,
    EveItemComponent,
    ListaPlanetaryComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule
  ],
  providers: [EveItemService],
  bootstrap: [AppComponent]
})
export class AppModule { }
