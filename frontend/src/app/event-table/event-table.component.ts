import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {environment} from '../../environments/environment';

@Component({
  selector: 'app-event-table',
  templateUrl: './event-table.component.html',
  styleUrls: ['./event-table.component.css']
})
export class EventTableComponent implements OnInit {

  environment: any;

  constructor(public router: Router,
              private activatedRoute: ActivatedRoute) {
                this.environment = environment;
  }

  ngOnInit(): void {
  }

  public eventClicked(id: string) {
    this.router.navigate(['/event-details/' + id], {relativeTo: this.activatedRoute}).catch(console.error);
  }
}
