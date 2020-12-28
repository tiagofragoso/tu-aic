import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {environment} from '../../../environments/environment';
import {events} from './events'; // mock data
import {formatDate} from '../../utils/date';

const PAGE_SIZE = 10; // TODO: discuss this

@Component({
  selector: 'app-event-table',
  templateUrl: './event-table.component.html',
  styleUrls: ['./event-table.component.css']
})
export class EventTableComponent implements OnInit {

  environment: any;
  state = {
    page: 1,
    pageSize: PAGE_SIZE,
    totalResults: 3
  }

  constructor(public router: Router,
              private activatedRoute: ActivatedRoute) {
                this.environment = environment;
                this.state.events = events.map((e) => ({
                  ...e,
                  tags: e.tags.join(", "),
                  created: formatDate(e.created),
                  updated: formatDate(e.updated)
                }));
  }

  ngOnInit(): void {
    this.getEvents();
  }

  public getEvents() {
    // make api request if state changed
  }

  public eventClicked(id: string) {
    this.router.navigate(['/events/' + id], {relativeTo: this.activatedRoute}).catch(console.error);
  }
}
