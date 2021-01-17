import {Injectable, TemplateRef} from "@angular/core";
import {ColorCodes} from "../Color/color-codes";

@Injectable({providedIn: 'root'})
export class ToastService {
  toasts: { classname?: any; delay?: any; disableTimeOut?: boolean, textOrTpl?: any; title?: string }[] = [];

  remove(toast: any) {
    this.toasts = this.toasts.filter(t => t !== toast);
  }

  showToast(message: string, color: ColorCodes): void {
    this.show(message, {
      classname: 'bg-' + color + ' text-light',
      delay: 6000,
    });
  }

  private show(textOrTpl: string | TemplateRef<any>, options: any = {}) {
    this.toasts.push({textOrTpl, ...options});
  }
}
