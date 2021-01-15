import {Component, HostBinding, TemplateRef} from '@angular/core';
import {ToastService} from './toast.service';


@Component({
  selector: 'bc-app-toasts',
  template: `
    <ngb-toast
      *ngFor="let toast of toastService.toasts"
      [class]="toast.classname"
      [autohide]="!toast.disableTimeOut"
      [delay]="toast.delay || 5000"
      (hidden)="toastService.remove(toast)"
    >
      <ng-template [ngIf]="isTemplate(toast)" [ngIfElse]="text">
        <ng-template [ngTemplateOutlet]="toast.textOrTpl"></ng-template>
      </ng-template>

      <ng-template #text>{{toast.textOrTpl}}</ng-template>
    </ngb-toast>
  `
})
export class ToastsContainerComponent {
  @HostBinding('class.ngb-toasts') role = 'button';

  constructor(public toastService: ToastService) {
  }

  isTemplate(toast: any) {
    return toast.textOrTpl instanceof TemplateRef;
  }
}
