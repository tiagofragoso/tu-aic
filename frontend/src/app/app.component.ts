import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from '@angular/common'

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'AIC Web Frontend';

  constructor(public router: Router,
              private activatedRoute: ActivatedRoute, private location: Location) {
  }

  public logoClicked() {
    this.router.navigate(['/event-table'], {relativeTo: this.activatedRoute}).catch(console.error);
  }

  public goBack() {
    this.location.back();
  }
}
