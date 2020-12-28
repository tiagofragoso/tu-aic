import {Component, OnInit, Output} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Event} from "../../models/event";
import {mockEvents} from "../../models/mockEvents";

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.css']
})
export class EventDetailsComponent implements OnInit {
  @Output()
  public id!: string | null;

  constructor(public router: Router,
              public activatedRoute: ActivatedRoute) {
  }

  tmpEvent: Event = new Event();

  ngOnInit(): void {
    // TODO: Use this id for the getEvent(id) service call
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    // TODO: If id could not be found redirect or separate 404-page?
    this.tmpEvent = mockEvents[0];
  }

}
