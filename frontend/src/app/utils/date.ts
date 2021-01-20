import * as moment from "moment";

export const convertUnixDateToString = (date: Date) => {
  return moment(date.valueOf()).format('DD.MM.YYYY - HH:mm');
};

export const convertUnixDateToDate = (d: number) => {
  // const date = new Date(d);
  //const utcDate = Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(),
  // date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds());
  return new Date(d);
};
