import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {environment} from '../../environments/environment';
import {events} from './events'; // mock data
import {formatDate} from '../../utils/date';

@Component({
  selector: 'app-event-table',
  templateUrl: './event-table.component.html',
  styleUrls: ['./event-table.component.css']
})
export class EventTableComponent implements OnInit {

  environment: any;
  events: any[];

  constructor(public router: Router,
              private activatedRoute: ActivatedRoute) {
                this.environment = environment;
                this.events = events.map((e) => ({
                  ...e, 
                  tags: e.tags.join(", "),
                  created: formatDate(e.created),
                  updated: formatDate(e.updated)
                }));
  }

  ngOnInit(): void {
  }

  public eventClicked(id: string) {
    this.router.navigate(['/event-details/' + id], {relativeTo: this.activatedRoute}).catch(console.error);
  }
}
