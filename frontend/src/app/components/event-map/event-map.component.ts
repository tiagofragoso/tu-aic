import {Component, HostListener} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

import {latLng, tileLayer, icon, marker, Layer, LatLng, Map, circle, point} from 'leaflet';

import {EventService} from 'src/app/services/event.service';
import {EventTableRow} from '../../models/event-table-data';
import {convertUnixDateToString} from "../../utils/date";

@Component({
  selector: 'app-event-map',
  templateUrl: './event-map.component.html',
  styleUrls: ['./event-map.component.css']
})
export class EventMapComponent {

  // Popup content is rendered as static HTML so we can't bind (click), see used fix below:
  // https://github.com/Asymmetrik/ngx-leaflet/issues/60#issuecomment-493716598
  @HostListener('document:click', ['$event']) 
    clickout(event: any) {
      if (event.target.classList.contains("popup-link")){ 
        this.eventClicked(event.target.dataset.eventId); 
      } 
    }

  baseLayer = tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: 14, minZoom: 1 });
  center: LatLng = latLng(42.854912879552664, -115.06896547973157);
  layers: Layer[] = [this.baseLayer];
  zoom: number = 6;
  map?: Map;
  events: EventTableRow[] = [];
  loading = true;
  showSearchCircle = true;
  radius = 0;

  options = {
    zoom: this.zoom,
    center: this.center,
  };

  constructor(public router: Router,
              private activatedRoute: ActivatedRoute,
              private eventService: EventService) {
  }

  onMapReady(map: Map) {
    this.map = map;
    this.getEvents();
  }

  onMapMove() {
    this.getEvents();
  }

  onMapZoom() {
    this.getEvents();
  }

  getEvents() {
    if (!this.map)
      return;

    this.loading = true;

    const centerEast = latLng(this.center.lat, this.map.getBounds().getEast());
    const dist = this.center.distanceTo(centerEast);
    this.radius = dist * 0.9 / 1000;

    this.eventService.findInRadius(this.radius, this.center.lat, this.center.lng)
    .subscribe((data: EventTableRow[]) => {
      this.events = data;
      this.refreshMap();
      this.loading = false;
    });
  }

  onShowSearchCircleChange() {
    this.refreshMap();
  }

  refreshMap() {
    this.layers = [this.baseLayer];
    if (this.showSearchCircle) {
      this.layers.push(circle(this.center, {radius: this.radius * 1000, fillOpacity: 0.15, opacity: 0.2}));
    }
    this.layers = [...this.layers, ...this.events.map((e) => this.createMarker(e))]; 
  }

  private createMarker(event: EventTableRow) {
    const m = marker([event.latitude, event.longitude], {
      icon: icon({
        iconSize: [ 25, 41 ],
        iconAnchor: [ 13, 41 ],
        iconUrl: 'leaflet/marker-icon.png',
        shadowUrl: 'leaflet/marker-shadow.png'
      })  
    });
    m.bindPopup(this.createPopupHtml(event), {minWidth: 200});
    return m;
  }

  private createPopupHtml(event: EventTableRow) {
    return `<div>
      <div class="row">
        <div class="col-4 text-muted">
          Name:
        </div>
        <div class="col-8 fw-bold">
          ${event.name}
        </div>
      </div>
      <div class="row">
        <div class="col-4 text-muted">
          State:
        </div>
        <div class="col-8">
          ${event.state}
        </div>
      </div>
      <div class="row mt-4">
        <div class="col-4 text-muted">
          Place ID:
        </div>
        <div class="col-8">
          ${event.place_ident}
        </div>
      </div>
      <div class="row">
        <div class="col-4 text-muted">
          Created: 
        </div>
        <div class="col-8">
          ${convertUnixDateToString(event.created)}
        </div>
      </div>
      <div class="row">
        <div class="col-4 text-muted">
          Updated: 
        </div>
        <div class="col-8">
          ${convertUnixDateToString(event.updated)}
        </div>
      </div>
      <div class="row">
        <div class="col-4 text-muted">
          Tags: 
        </div>
        <div class="col-8">
          ${event.tags.length > 1 ? event.tags.length - 1 : 'Not tagged yet'}
        </div>
      </div>
      <div class="row mt-2 justify-content-center">
        <button class="btn btn-link btn-sm popup-link" data-event-id="${event.event_id}">See event details</button>
      </div>
    </div>`;
  }

  public eventClicked(id: string) {
    this.router.navigate(['/events/' + id], {relativeTo: this.activatedRoute}).catch(console.error);
  }
}
