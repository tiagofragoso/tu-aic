import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";

@Component({
  selector: 'app-event-map',
  templateUrl: './event-map.component.html',
  styleUrls: ['./event-map.component.css']
})
export class EventMapComponent implements OnInit {

  constructor(public router: Router,
              private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
  }

  public eventClicked(id: string) {
    this.router.navigate(['/event-details/' + id], {relativeTo: this.activatedRoute}).catch(console.error);
  }
}
