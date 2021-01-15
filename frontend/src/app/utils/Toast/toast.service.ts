import {Injectable, TemplateRef} from "@angular/core";

@Injectable({providedIn: 'root'})
export class ToastService {
  toasts: { classname?: any; delay?: any; disableTimeOut?: boolean, textOrTpl?: any; title?: string }[] = [];

  remove(toast: any) {
    this.toasts = this.toasts.filter(t => t !== toast);
  }

  showToastError(errMess: string): void {
    this.show(errMess, {
      classname: 'bg-danger text-light',
      delay: 6000,
    });
  }

  showToastSuccess(message: string): void {
    this.show(message, {
      classname: 'bg-success text-light',
      delay: 4000,

      autohide: false
    });
  }

  private show(textOrTpl: string | TemplateRef<any>, options: any = {}) {
    this.toasts.push({textOrTpl, ...options});
  }
}
