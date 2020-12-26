import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {EventTableComponent} from "./event-table/event-table.component";
import {EventMapComponent} from "./event-map/event-map.component";
import {EventDetailsComponent} from "./event-details/event-details.component";


const routes: Routes = [
  {path: 'event-table', component: EventTableComponent},
  {path: 'event-map', component: EventMapComponent},
  {path: 'event-details/:id', component: EventDetailsComponent},
  {path: '**', redirectTo: 'event-table', pathMatch: 'full'}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
