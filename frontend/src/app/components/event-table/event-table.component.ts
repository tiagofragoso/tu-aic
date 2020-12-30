import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {environment} from '../../../environments/environment';
import {mockEvents} from "../../models/mockEvents";
import {convertUnixDateToString} from "../../utils/date";
import {Tag} from "../../models/tag";

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
    totalResults: 3,
    events: mockEvents
  };

  constructor(public router: Router,
              private activatedRoute: ActivatedRoute) {
                this.environment = environment;
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

  public convertDate(date?: Date): string {
    if (!date) {
      return '';
    }
    return convertUnixDateToString(date);
  }

  public convertTagNames(tags?: Tag[]): string {
    if (!tags) return '';
    // TODO: If too many tags occur, append '...'
    return tags.map(tag => tag.name).toString().replace(/,/g, ", ");
  }
}
