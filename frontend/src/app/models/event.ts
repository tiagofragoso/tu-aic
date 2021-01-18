import {Tag} from "./tag";
import {MetaData} from "./metadata";

export class Event {
  image!: string;
  metadata!: MetaData;
  tags!: Tag[];
}
