import {State} from "./state";

export class MetaData {
  event_id!: string;
  state?: State;
  dev_id!: string;
  name!: string;
  created!: number;
  updated!: number;
  place_ident!: string;
  event_frames!: number;
  frame_num!: number;
  longitude!: number;
  latitude!: number;
}
