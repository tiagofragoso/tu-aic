import {States} from "./states";

export class MetaData {
  event_id!: string;
  state?: States;
  dev_id?: string;
  dev_name?: string;
  timestamp?: Date;
  created?: Date;
  updated?: Date;
  place_ident?: string;
  event_frames?: number;
  frame_num?: number;
  longitude?: number;
  latitude?: number;
}
