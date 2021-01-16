import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {NgbModule} from '@ng-bootstrap/ng-bootstrap';
import {LeafletModule} from '@asymmetrik/ngx-leaflet';
import {LeafletMarkerClusterModule} from '@asymmetrik/ngx-leaflet-markercluster';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {EventTableComponent, SortableHeader} from './components/event-table/event-table.component';
import {EventMapComponent} from './components/event-map/event-map.component';
import {EventDetailsComponent} from './components/event-details/event-details.component';

@NgModule({
  declarations: [
    AppComponent,
    EventTableComponent,
    SortableHeader,
    EventMapComponent,
    EventDetailsComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    LeafletModule,
    LeafletMarkerClusterModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule {
}
