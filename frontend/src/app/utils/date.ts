// TODO: Use this in the service class
import * as moment from "moment";

export const convertUnixDateToString = (date: Date) => {
  return moment(date).format('DD.MM.YYYY - HH:mm');
};

export const convertUnixDateToDate = (d: number) => {
  return new Date(d);
};

export const convertStringDateToDate = (d: string) => {
  // example string: '13-Apr-2019 (04:35:23.000000)'
  return moment(d, 'DD-MMM-YYYY (HH:mm:ss.SSSSSS)').toDate();
};
