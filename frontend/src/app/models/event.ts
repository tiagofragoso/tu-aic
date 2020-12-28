import {Tag} from "./tag";
import {MetaData} from "./metadata";

export class Event {
  image?: string;
  metadata?: MetaData;
  tags?: Tag[];
}

/*
{
“image”: ImageAsB64,
“metadata”: json: MetaDataEntity,
“tags” : [
    {“name”:string,
“created”: long_unix_epoch
}, ...
]
}
*/
