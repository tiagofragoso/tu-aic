import {Component, OnInit, Output, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Event} from "../../models/event";
import {convertStringDateToDate, convertUnixDateToDate, convertUnixDateToString} from "../../utils/date";
import {States} from "../../models/states";
import {NgbCarousel, NgbDateStruct, NgbModal, NgbTimeStruct} from "@ng-bootstrap/ng-bootstrap";
import {EventService} from "../../services/event.service";
import {FormControl, FormGroup} from "@angular/forms";
import {Tag} from "../../models/tag";
import {ToastService} from "../../utils/Toast/toast.service";
import {HttpErrorResponse} from "@angular/common/http";

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.css']
})
export class EventDetailsComponent implements OnInit {
  // TODO: Validation in form
  @Output()
  public id: string | null = '';
  public eventForm = new FormGroup({
    name: new FormControl(''),
    dev_id: new FormControl(''),
    created_date: new FormControl(''),
    created_time: new FormControl(''),
    frame_num: new FormControl(''),
    event_frames: new FormControl(''),
    place_ident: new FormControl(''),
    longitude: new FormControl(''),
    latitude: new FormControl('')
  });
  statusEnum: typeof States = States;
  loading = true;
  edit = false;
  event: Event = new Event();
  tmpEvent!: Event;
  createdTime!: NgbTimeStruct;
  activeSliderId = 'ngb-slide-0';
  tags: Array<Tag> = [];
  error = false;
  noChanges = false;
  @ViewChild('tagCarousel', {static: false}) carousel!: NgbCarousel;

  constructor(public router: Router, public toastService: ToastService,
              public activatedRoute: ActivatedRoute, private modalService: NgbModal, private eventService: EventService) {
  }

  // TODO: Slider of tags (also should not display more than 5 before arrows appear so that one can navigate between them)

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    if (this.id != null) {
      this.eventService.getById(this.id).subscribe((event: Event) => {
        this.event = this.tmpEvent = event;
        if (this.event.image && this.event.image.length === 0) {
          this.event.metadata.state = States.MISSING;
        } else {
          this.event.metadata.state = States.CORRECT;
        }
        this.setCreatedTime(this.event.metadata.created);
        this.fillForm(this.event);
        this.tags = event.tags;

        for (let tag in event.tags) {
          this.tags[Number(tag)].image = this.event.image;
          this.eventService.getTag(event.metadata.event_id, event.tags[Number(tag)].tag_name).subscribe((tag2: Tag) => {
              this.tags[Number(tag)].image = tag2.image;
              if (Number(tag) === (Object.keys(event.tags).length - 1)) {
                this.loading = false;
              }
            },
            () => {
              console.error(tag);
            });
        }
      }, (error) => {
        this.error = true;
        this.loading = false;
        console.error(error);
      },);
    }
  }

  cancel() {
    this.event = {...this.tmpEvent};
    this.setCreatedTime(this.tmpEvent.metadata.created);
    this.fillForm(this.event);
    this.edit = false;
  }

  convertDate(date: any) {
    let tmpDate: Date;
    if (Number.isInteger(date)) {
      tmpDate = convertUnixDateToDate(Number.parseInt(date));
    } else {
      tmpDate = convertStringDateToDate(date);
    }
    return convertUnixDateToString(tmpDate);
  }

  openDeleteDialogue(content: any) {
    this.modalService.open(content);
  }

  saveUpdate() {
    if (!this.eventForm.valid) {

      //  not valid
      // TODO: Invalid inputs (longitude(-180 till 180), latitude(-90 till 90), created after updated or a date before 1970)
    } else {
      this.eventService.update(this.id!, this.getEventFormValues()).subscribe(() => {
          this.toastService.showToastSuccess('Event was successfully updated!');
          // TODO: Make map refresh itself if new latitude/longitude got set
          this.tmpEvent = this.event = this.getEventFormValues();
          this.edit = false;
        },
        (error: HttpErrorResponse) => {
          let message: string;
          if (error.status === 304) {
            message = "Event metadata was not modified!";
          } else {
            message = "An error occurred:" + error.message;
          }
          this.toastService.showToastError(message);
          this.cancel();
        });
    }
  }

  deleteEvent() {
    this.eventService.delete(this.event.metadata.event_id).subscribe(() => {
        this.toastService.showToastSuccess('Event ' + this.id + 'was successfully deleted!\nYou will be redirected in 5 seconds.');
        setTimeout(() => {
          this.router.navigate(['/events']);
        }, 5000);
      }
    );
  }

  fillForm(event: Event) {
    const date = new Date(event.metadata.created);
    this.eventForm.setValue({
      name: event.metadata.name,
      dev_id: event.metadata.dev_id,
      frame_num: event.metadata.frame_num,
      event_frames: event.metadata.event_frames,
      place_ident: event.metadata.place_ident,
      longitude: event.metadata.longitude,
      latitude: event.metadata.latitude,
      created_date: {
        year: date.getUTCFullYear(),
        month: date.getUTCMonth() + 1,
        day: date.getUTCDate()
      },
      created_time: {hour: date.getUTCHours(), minute: date.getUTCMinutes()}
    });
  }

  getEventFormValues(): Event {
    let editedEvent = {...this.tmpEvent};
    editedEvent.metadata.name = this.eventForm.get('name')?.value;
    editedEvent.metadata.dev_id = this.eventForm.get('dev_id')?.value;
    editedEvent.metadata.frame_num = this.eventForm.get('frame_num')?.value;
    editedEvent.metadata.event_frames = this.eventForm.get('event_frames')?.value;
    editedEvent.metadata.place_ident = this.eventForm.get('place_ident')?.value;
    editedEvent.metadata.longitude = this.eventForm.get('longitude')?.value;
    editedEvent.metadata.latitude = this.eventForm.get('latitude')?.value;
    const createdDate: NgbDateStruct = this.eventForm.get('created_date')?.value;
    const createdTime: NgbTimeStruct = this.eventForm.get('created_time')?.value;
    let date = new Date(editedEvent.metadata.created);
    date.setUTCFullYear(createdDate.year);
    date.setUTCMonth(createdDate.month - 1);
    date.setUTCDate(createdDate.day);
    date.setUTCHours(createdTime.hour);
    date.setUTCMinutes(createdTime.minute);
    editedEvent.metadata.created = date.valueOf();
    return editedEvent;
  }

  setCreatedTime(dateTime: number) {
    const date = new Date(dateTime);
    this.createdTime =
      {
        hour: date.getUTCHours(), minute: date.getUTCMinutes(), second: date.getUTCSeconds()
      };
  }

  findTagIndex(tag: Tag) {
    return this.event.tags.map(function (e) {
      return e;
    }).indexOf(tag);
  }

  goToSlide(tag: Tag) {
    const slideId: number = this.event.tags.indexOf(tag);
    this.carousel.select('ngb-slide-' + slideId);
  }
}
