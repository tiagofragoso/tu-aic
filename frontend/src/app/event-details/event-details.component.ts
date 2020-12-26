import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.css']
})
export class EventDetailsComponent implements OnInit {
  public id: string | null | undefined;

  constructor(public router: Router,
              public activatedRoute: ActivatedRoute) {
  }

  // TODO: If id could not be found redirect or separate 404-page?
  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
  }

}
