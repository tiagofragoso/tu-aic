import {Component, HostListener, Input} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

import {circle, icon, LatLng, latLng, Layer, Map, marker, tileLayer} from 'leaflet';

import {EventService} from 'src/app/services/event.service';
import {EventTableRow} from '../../models/event-table-data';
import {convertUnixDateToString} from "../../utils/date";
import {Event} from 'src/app/models/event';

const CONSTANTS = Object.freeze({
  MIN_ZOOM: 1,
  MAX_ZOOM: 14,
  DEFAULT_ZOOM: 6,
  CURR_EVENT_ZOOM: 10,
  DEFAULT_CENTER_LAT: 42.854912879552664,
  DEFAULT_CENTER_LON: -115.06896547973157,
  REQUEST_WAIT_TIME: 250,
  M_TO_KM: 1/1000,
  DEAFULT_RADIUS_FACTOR: 0.95
});

@Component({
  selector: 'app-event-map',
  templateUrl: './event-map.component.html',
  styleUrls: ['./event-map.component.css']
})
export class EventMapComponent {

  // Popup content is rendered as static HTML so we can't bind (click), see used fix below:
  @Input() singleEvent: Event | undefined;

  baseLayer = tileLayer('http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', { maxZoom: CONSTANTS.MAX_ZOOM, minZoom: CONSTANTS.MIN_ZOOM });
  center: LatLng = latLng(CONSTANTS.DEFAULT_CENTER_LAT, CONSTANTS.DEFAULT_CENTER_LON);
  layers: Layer[] = [this.baseLayer];
  zoom: number = CONSTANTS.DEFAULT_ZOOM;
  map?: Map;
  events: EventTableRow[] = [];
  loading = true;
  showSearchCircle = true;
  radius = 0;
  id: string | null;
  radiusFactor = CONSTANTS.DEAFULT_RADIUS_FACTOR;

  options = {
    zoom: this.zoom,
    center: this.center,
  };

// TODO: sidebar with all visible events, adjust popup style, radius so that max is map visible and a slider for its radius
  constructor(public router: Router,
              private activatedRoute: ActivatedRoute,
              private eventService: EventService) {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
  }

  private static createPopupHtml(event: EventTableRow) {
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

  // https://github.com/Asymmetrik/ngx-leaflet/issues/60#issuecomment-493716598
  @HostListener('document:click', ['$event'])
  clickout(event: any) {
    if (event.target.classList.contains("popup-link")) {
      this.eventClicked(event.target.dataset.eventId);
    }
  }

  onMapMove() {
    setTimeout(() => this.getEvents(), CONSTANTS.REQUEST_WAIT_TIME);
  }

  onMapReady(map: Map) {
    this.map = map;
    if (this.id) {
      this.eventService.getById(this.id)
        .subscribe((data: Event) => {
          if (data.metadata?.latitude && data.metadata?.longitude) {
            this.map?.flyTo(latLng(data.metadata.latitude, data.metadata.longitude), CONSTANTS.CURR_EVENT_ZOOM, {noMoveStart: true});
          } else {
            console.error("Invalid event details");
          }
        });
    } else {
      this.getEvents();
    }
  }

  onMapZoom() {
    setTimeout(() => this.getEvents(), CONSTANTS.REQUEST_WAIT_TIME);
  }

  onShowSearchCircleChange() {
    this.refreshMap();
  }

  onChangeRadiusFactor() {
    this.getEvents();
  }

  getEvents() {
    if (!this.map)
      return;

    this.loading = true;

    const centerNorth = latLng(this.map.getBounds().getNorth(), this.center.lng);
    const dist = this.center.distanceTo(centerNorth);
    this.radius = dist * this.radiusFactor * CONSTANTS.M_TO_KM;

    this.eventService.findInRadius(this.radius, this.center.lat, this.center.lng)
      .subscribe((data: EventTableRow[]) => {
          this.events = data;
          this.refreshMap();
          this.loading = false;
        },
        () => {
          // this.events = null;
          this.loading = false;
        },);
  }

  refreshMap() {
    this.layers = [this.baseLayer];
    if (this.showSearchCircle) {
      this.layers.push(circle(this.center, {radius: this.radius / CONSTANTS.M_TO_KM, fillOpacity: 0.15, opacity: 0.2}));
    }
    this.layers = [...this.layers, ...this.events.map((e) => this.createMarker(e))];
  }

  private createMarker(event: EventTableRow) {
    const m = marker([event.latitude, event.longitude], {
      icon: icon({
        iconSize: [25, 41],
        iconAnchor: [13, 41],
        iconUrl: (this.id && this.id === event.event_id) ?
          'https://raw.githubusercontent.com/pointhi/leaflet-color-markers/master/img/marker-icon-2x-green.png'
          : 'leaflet/marker-icon-2x.png',
        shadowUrl: 'leaflet/marker-shadow.png'
      })
    });
    m.bindPopup(EventMapComponent.createPopupHtml(event), {minWidth: 200});
    return m;
  }

  public eventClicked(id: string) {
    this.router.navigate(['/events/' + id], {relativeTo: this.activatedRoute}).catch(console.error);
  }
}
