import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {EventTableComponent} from './event-table/event-table.component';
import {EventMapComponent} from './event-map/event-map.component';
import {EventDetailsComponent} from './event-details/event-details.component';

@NgModule({
  declarations: [
    AppComponent,
    EventTableComponent,
    EventMapComponent,
    EventDetailsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
