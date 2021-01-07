import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {EventTableComponent} from "./components/event-table/event-table.component";
import {EventMapComponent} from "./components/event-map/event-map.component";
import {EventDetailsComponent} from "./components/event-details/event-details.component";


const routes: Routes = [
  {path: 'events', component: EventTableComponent},
  {path: 'events/map', component: EventMapComponent},
  {path: 'events/map/:id', component: EventMapComponent},
  {path: 'events/:id', component: EventDetailsComponent},
  {path: '**', redirectTo: 'events', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
