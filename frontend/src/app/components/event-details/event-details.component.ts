import {Component, OnInit, Output, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {Event} from "../../models/event";
import {convertUnixDateToDate, convertUnixDateToString} from "../../utils/date";
import {State} from "../../models/state";
import {NgbCarousel, NgbDateStruct, NgbModal, NgbTimeStruct} from "@ng-bootstrap/ng-bootstrap";
import {EventService} from "../../services/event.service";
import {FormControl, FormGroup, ValidationErrors, Validators} from "@angular/forms";
import {Tag} from "../../models/tag";
import {ToastService} from "../../utils/Toast/toast.service";
import {HttpErrorResponse} from "@angular/common/http";
import {ColorCodes} from "../../utils/Color/color-codes";
import {EventMapComponent} from "../event-map/event-map.component";

export interface TagX {
  tag: Tag,
  index: number,
  loading: boolean
}

@Component({
  selector: 'app-event-details',
  templateUrl: './event-details.component.html',
  styleUrls: ['./event-details.component.css']
})
export class EventDetailsComponent implements OnInit {
  @Output()
  public id: string | null = '';
  public eventForm: FormGroup;
  statusEnum: typeof State = State;
  loading = true;
  edit = false;
  event: Event = new Event();
  tmpEvent!: Event;
  createdTime!: NgbTimeStruct;
  activeSliderId = 'ngb-slide-0';
  tags: Array<TagX> = [];
  error = false;
  @ViewChild('tagCarousel', {static: false}) carousel!: NgbCarousel;
  @ViewChild('mapPreview') mapPreview!: EventMapComponent;

  constructor(public router: Router, public toastService: ToastService,
              public activatedRoute: ActivatedRoute, private modalService: NgbModal, private eventService: EventService) {
    this.eventForm = new FormGroup({
      name: new FormControl(''),
      device_identifier: new FormControl(''),
      created_date: new FormControl(''),
      created_time: new FormControl(''),
      // update: new FormControl(''),
      frame_number: new FormControl(''),
      event_frames: new FormControl(''),
      place_identifier: new FormControl(''),
      longitude: new FormControl(''),
      latitude: new FormControl('')
    });
  }

  ngOnInit(): void {
    this.id = this.activatedRoute.snapshot.paramMap.get('id');
    if (this.id != null) {
      this.eventService.getById(this.id).subscribe((event: Event) => {
        this.setup(event);
        const baseTag = new Tag();
        baseTag.tag_name = 'base';
        baseTag.image = event.image;
        this.tags.push({tag: baseTag, index: 0, loading: false});

        for (const tag of event.tags) {
          if (tag.tag_name === 'base') {
            continue;
          }
          const curr = new Tag();
          curr.tag_name = tag.tag_name;
          curr.created = tag.created;
          const index = this.tags.length;
          this.tags.push({tag: curr, index, loading: true});
          this.eventService.getTag(event.metadata.event_id, curr.tag_name).subscribe((tag: Tag) => {
              this.tags[index].tag.image = tag.image;
              this.tags[index].loading = false;
            },
            () => {
              this.tags[index].loading = false;
              console.error(tag);
            });
        }
        this.loading = false;
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
    tmpDate = convertUnixDateToDate(Number.parseInt(date));
    return convertUnixDateToString(tmpDate);
  }

  openDeleteDialogue(content: any) {
    this.modalService.open(content);
  }

  getFormControlError(): string {
    let error = '';
    if (this.eventForm.invalid) {
      if (this.eventForm.errors) {
        const controlErrors: ValidationErrors = this.eventForm.errors;
        error = Object.keys(controlErrors).map(keyError => {
          return controlErrors[keyError];
        }).join('\n') + '\n ';
      }
      Object.keys(this.eventForm.controls).forEach((key: string) => {
        if (this.eventForm.get(key)?.invalid) {
          const controlErrors: ValidationErrors = this.eventForm.get(key)!.errors!;
          Object.keys(controlErrors).forEach((errorKey: string) => {
            if (errorKey === 'required') {
              error += '"' +
                key.split('_').map(value => {
                  return value.charAt(0).toUpperCase() + value.substring(1)
                }).join(' ') + '" cannot be empty!\n ';
            } else {
              error += controlErrors[errorKey] + '\n';
            }
          });
        }
      });
    }
    return error;
  }

  setup(event: Event) {
    this.event = this.tmpEvent = event;
    if (this.event.image.length === 0) {
      this.event.metadata.state = State.MISSING;
    } else {
      this.event.metadata.state = State.CORRECT;
    }
    this.setCreatedTime(this.event.metadata.created);
    this.fillForm(this.event);
  }

  updateMap(newEvent: Event) {
    if (this.mapPreview && this.mapPreview.events && this.mapPreview.events.length > 0) {
      const previousEvent = this.mapPreview.events.find(event => event.event_id === this.id);
      if (previousEvent && newEvent.metadata?.longitude && newEvent.metadata?.latitude &&
        previousEvent.latitude && previousEvent.longitude &&
        (newEvent.metadata.longitude != previousEvent.longitude ||
          newEvent.metadata.latitude != previousEvent.latitude)) {
        this.mapPreview.id = newEvent.metadata.event_id;
        this.mapPreview.onMapReady(this.mapPreview.map!);
      }
    }
  }

  saveUpdate() {
    this.eventService.update(this.id!, this.getEventFormValues()).subscribe(() => {
        this.toastService.showToast('Event was successfully updated!', ColorCodes.SUCCESS);
        this.eventService.getById(this.id!).subscribe((newEvent: Event) => {
          this.setup(newEvent);
          this.updateMap(newEvent);
          this.edit = false;
        });
      },
      (error: HttpErrorResponse) => {
        let message: string;
        let color: ColorCodes;
        if (error.status === 304) {
          message = "Event metadata was not modified!";
          color = ColorCodes.WARNING;
        } else {
          message = "An error occurred:" + error.message;
          color = ColorCodes.DANGER;
        }
        this.toastService.showToast(message, color);
        this.cancel();
      });
  }

  deleteEvent() {
    this.eventService.delete(this.event.metadata.event_id).subscribe(() => {
      this.router.navigate(['/events'], {state: {deletedEventId: this.event.metadata.event_id}}).catch(console.error);
    });
  }

  fillForm(event: Event) {
    const date = new Date(event.metadata.created);
    this.eventForm.setValue({
      name: event.metadata.name,
      device_identifier: event.metadata.dev_id,
      frame_number: event.metadata.frame_num,
      event_frames: event.metadata.event_frames,
      place_identifier: event.metadata.place_ident,
      longitude: event.metadata.longitude,
      latitude: event.metadata.latitude,
      created_date: {
        year: date.getUTCFullYear(),
        month: date.getUTCMonth() + 1,
        day: date.getUTCDate()
      },
      created_time: {hour: date.getUTCHours(), minute: date.getUTCMinutes()}
    });

    this.eventForm.setValidators([Validators.required, this.validateFrames, this.validateCreatedDate]);
    this.eventForm.get('name')?.setValidators(Validators.required);
    this.eventForm.get('device_identifier')?.setValidators(Validators.required);
    this.eventForm.get('created_date')?.setValidators(Validators.required);
    this.eventForm.get('created_time')?.setValidators(Validators.required);
    this.eventForm.get('frame_number')?.setValidators([Validators.required, this.validateFrameNum]);
    this.eventForm.get('event_frames')?.setValidators([Validators.required, this.validateEventFrames]);
    this.eventForm.get('place_identifier')?.setValidators(Validators.required);
    this.eventForm.get('longitude')?.setValidators([Validators.required, this.validateLongitude]);
    this.eventForm.get('latitude')?.setValidators([Validators.required, this.validateLatitude]);
  }

  getEventFormValues(): Event {
    let editedEvent = {...this.tmpEvent};
    editedEvent.metadata.name = this.eventForm.get('name')?.value;
    editedEvent.metadata.dev_id = this.eventForm.get('device_identifier')?.value;
    editedEvent.metadata.frame_num = this.eventForm.get('frame_number')?.value;
    editedEvent.metadata.event_frames = this.eventForm.get('event_frames')?.value;
    editedEvent.metadata.place_ident = this.eventForm.get('place_identifier')?.value;
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

  goToSlide(id: number) {
    this.carousel.select('ngb-slide-' + id);
  }

  validateFrameNum(frameNum: FormControl): ValidationErrors | null {
    return (
      Number.isInteger(frameNum.value) && Number.parseInt(frameNum.value) >= 1
    ) ?
      null :
      {frameNum: '"Frame number" has to be a number greater or equal to 1'};
  }

  validateEventFrames(eventFrames: FormControl): ValidationErrors | null {
    return (
      Number.isInteger(eventFrames.value) && Number.parseInt(eventFrames.value) >= 1
    ) ?
      null :
      {eventFrames: '"Event frames" has to be a number greater or equal to 1!'};
  }

  validateLongitude(longitude: FormControl): ValidationErrors | null {
    return (
      longitude.value.toString().match('^-?\\d+(?:.\\d+)?$') && longitude.value >= -180 && longitude.value <= 180
    ) ?
      null :
      {longitude: '"Longitude" has to be a number between -180 and 180!'};
  }

  validateLatitude(latitude: FormControl): ValidationErrors | null {
    return (
      latitude.value.toString().match('^-?\\d+(?:.\\d+)?$') && latitude.value >= -90 && latitude.value <= 90
    ) ?
      null :
      {latitude: '"Latitude" has to be a number between -90 and 90!'};
  }

  validateCreatedDate(fg: FormGroup): ValidationErrors | null {
    const createdDateInput = fg?.get('created_date')?.value as NgbDateStruct;
    const createdDateTimeInput = fg?.get('created_time')?.value as NgbTimeStruct;
    const createdDate = new Date(createdDateInput.year, createdDateInput.month - 1, createdDateInput.day, createdDateTimeInput.hour, createdDateTimeInput.minute);
    return (
      createdDate
      &&
      createdDate.valueOf() >= (new Date(0).valueOf())
      &&
      createdDate.valueOf() <= Date.now()
    ) ?
      null :
      {createdDate: '"Created Date" has to be a valid date in the expected format (e.g.: "15.11.2020"), greater or equal to 01.01.1970 and has to be lesser or equal than the current date!'};
  }

  validateFrames(fg: FormGroup): ValidationErrors | null {
    return fg?.get('frame_number')?.value <= fg?.get('event_frames')?.value
      ? null :
      {frames: '"Frame Number" has to be lesser or equal to "Event Frames"!'};
  }
}
