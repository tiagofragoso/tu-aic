import {Component, OnInit, Directive, EventEmitter, Input, Output, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {FormControl} from '@angular/forms';

import {EventService} from "../../services/event.service";

import {mockEvents} from "../../models/mockEvents";
import {convertUnixDateToString} from "../../utils/date";
import {Event} from "../../models/event";
import {Tag} from "../../models/tag";
import {States} from "../../models/states";

const PAGE_SIZE = 10; // TODO: discuss this

export type SortColumn = 'dev_name' | 'place_ident' | 'created' | 'udpated' | '';
export type SortDirection = 'asc' | 'desc' | '';
const rotate: {[key: string]: SortDirection} = { 'asc': 'desc', 'desc': '', '': 'asc' };

export interface SortEvent {
  column: SortColumn;
  direction: SortDirection;
}

@Directive({
  selector: 'th[sortable]',
  host: {
    '[class.asc]': 'direction === "asc"',
    '[class.desc]': 'direction === "desc"',
    '(click)': 'rotate()'
  }
})
export class SortableHeader {

  @Input() sortable: SortColumn = '';
  @Input() direction: SortDirection = '';
  @Output() sort = new EventEmitter<SortEvent>();

  rotate() {
    this.direction = rotate[this.direction];
    this.sort.emit({column: this.direction !== '' ? this.sortable: '', direction: this.direction});
  }
}

export interface QueryOptions {
  page: number,
  pageSize: number,
  sortColumn?: SortColumn,
  sortDirection?: SortDirection,
  searchTerm: FormControl
}

@Component({
  selector: 'app-event-table',
  templateUrl: './event-table.component.html',
  styleUrls: ['./event-table.component.css']
})
export class EventTableComponent implements OnInit {
  @ViewChildren(SortableHeader) headers: QueryList<SortableHeader>;
  queryOptions: QueryOptions;
  totalResults: number = 0;
  events: Event[] = [];
  statesColorMapping: {[key in States]: string} = { 
    CORRECT: 'success', 
    FAULTY: 'warning', 
    MISSING: 'danger' 
  };

  constructor(public router: Router,
              private activatedRoute: ActivatedRoute,
              private eventService: EventService) {
                this.queryOptions = {
                  page: 1,
                  pageSize: PAGE_SIZE,
                  searchTerm: new FormControl('')
                };
                this.headers = new QueryList<SortableHeader>();
              }

  ngOnInit(): void {
    this.getEvents();
  }

  onPageChange() {
    console.log(this.queryOptions);
    this.getEvents();
  }

  onSort({column, direction}: SortEvent) {

    // resetting other headers
    this.headers.forEach(header => {
      if (header.sortable !== column) {
        header.direction = '';
      }
    });

    this.queryOptions.sortColumn = column;
    this.queryOptions.sortDirection = direction;
    console.log(this.queryOptions);
    this.getEvents();
  }

  onSubmitSearch() {
    console.log(this.queryOptions.searchTerm?.value);
    this.getEvents();
  }

  public getEvents() {
    // make api request if query options changed

    this.eventService.getAll(this.queryOptions)
      .subscribe((data) => {console.log(data)});

    this.events = mockEvents;
    this.totalResults = mockEvents.length;
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
    // TODO: If too many tags occur, append ...
    return tags.slice(0, 2).map(tag => tag.name).join(", ");
  }
}
