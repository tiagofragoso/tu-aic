import {Component} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Location} from '@angular/common'
import {EventDetailsComponent} from "./components/event-details/event-details.component";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'AIC Web Frontend';
  id: string | null | undefined = '-1';
  constructor(public router: Router,
              private activatedRoute: ActivatedRoute, private location: Location) {
  }

  public logoClicked() {
    this.router.navigate(['/events'], {relativeTo: this.activatedRoute}).catch(console.error);
  }

  public goBack() {
    this.location.back();
  }

  onActivate(component: any) {
    if (component instanceof EventDetailsComponent) {
      this.id = component.id;
    }
  }

}
