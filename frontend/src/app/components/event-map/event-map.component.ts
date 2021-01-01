import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

import {latLng, tileLayer, icon, marker, Layer, LatLng, Map, circle, point} from 'leaflet';

import {EventService} from 'src/app/services/event.service';
import {EventTableRow} from '../../models/event-table-data';

@Component({
  selector: 'app-event-map',
  templateUrl: './event-map.component.html',
  styleUrls: ['./event-map.component.css']
})
export class EventMapComponent {

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
    m.bindPopup(`<span (click)="eventClicked(${event.event_id})">${event.event_id}</span>`);
    return m;
  }

  public eventClicked(id: string) {
    this.router.navigate(['/events/' + id], {relativeTo: this.activatedRoute}).catch(console.error);
  }
}
