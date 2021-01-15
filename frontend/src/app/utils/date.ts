import * as moment from "moment";

export const convertUnixDateToString = (date: Date) => {
  return moment.utc(date).format('DD.MM.YYYY - HH:mm');
};

export const convertUnixDateToDate = (d: number) => {
  const date = new Date(d);
  const utcDate = Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(),
    date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds());
  return new Date(utcDate);
};

export const convertStringDateToDate = (d: string) => {
  // example string: '13-Apr-2019 (04:35:23.000000)'
  return new Date(moment.utc(d, 'DD-MMM-YYYY (HH:mm:ss.SSSSSS)').toDate());
};
