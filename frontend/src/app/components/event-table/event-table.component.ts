import {Component, OnInit, Directive, EventEmitter, Input, Output, QueryList, ViewChildren} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {FormControl} from '@angular/forms';

import {EventService} from "../../services/event.service";

import {mockEvents} from "../../models/mockEvents";
import {convertUnixDateToString} from "../../utils/date";
import {Event} from "../../models/event";
import {States} from "../../models/states";
import {EventTableData, EventTableRow, EventTableRowTag} from '../../models/event-table-data';

const BASE_TAG = "base";

export type SortColumn = 'name' | 'place_ident' | 'created' | 'udpated' | '';
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
  events: EventTableRow[] = [];
  loading: boolean = false;
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
                  pageSize: 5,
                  searchTerm: new FormControl('')
                };
                this.headers = new QueryList<SortableHeader>();
              }

  ngOnInit(): void {
    this.getEvents();
  }

  onPageChange() {
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
    this.getEvents();
  }

  onSubmitSearch() {
    this.getEvents();
  }

  public getEvents() {
    this.loading = true;
    this.eventService.getAll(this.queryOptions)
      .subscribe((data: EventTableData) => {
        this.events = data.events;
        this.totalResults = data.total_items;
        this.loading = false;
      });
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

  public convertTagNames(tags?: EventTableRowTag[]): string {
    if (!tags) return '';
    // TODO: If too many tags occur, append ...
    return tags.filter(({tag_name}) => tag_name !== BASE_TAG).slice(0, 2).map(({tag_name}) => tag_name).join(", ");
  }
}
