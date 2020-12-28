export class Tag {
  name!: string;
  created?: Date;
  image?: string;
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
