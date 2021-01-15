import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HttpClientModule} from '@angular/common/http';

import {NgbDateParserFormatter, NgbModalModule, NgbModule, NgbToastModule, NgbTooltipModule} from '@ng-bootstrap/ng-bootstrap';
import {LeafletModule} from '@asymmetrik/ngx-leaflet';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {EventTableComponent, SortableHeader} from './components/event-table/event-table.component';
import {EventMapComponent} from './components/event-map/event-map.component';
import {EventDetailsComponent} from './components/event-details/event-details.component';
import {CommonModule} from "@angular/common";
import {NgbDateCustomParserFormatter} from "./utils/NgbDateCustomParserFormatter";
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {ToastsContainerComponent} from "./utils/Toast/toast-container.component";

@NgModule({
  declarations: [
    AppComponent,
    EventTableComponent,
    SortableHeader,
    EventMapComponent,
    EventDetailsComponent,
    ToastsContainerComponent,
  ],
  imports: [
    BrowserModule,
    CommonModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    HttpClientModule,
    NgbModule,
    LeafletModule,
    NgbModalModule,
    BrowserAnimationsModule,
    NgbToastModule,
    NgbTooltipModule
  ],
  providers: [
    {provide: NgbDateParserFormatter, useClass: NgbDateCustomParserFormatter}
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
}
