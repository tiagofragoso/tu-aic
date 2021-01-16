import {State} from './state';

export interface EventTableData {
	events: EventTableRow[];
	current_page: number;
	total_items: number;
	total_pages: number;
}

export interface EventTableRow {
  event_id: string;
  state: State;
  name: string;
  created: Date;
  updated: Date;
  place_ident: string;
  longitude: number;
  latitude: number;
  tags: EventTableRowTag[];
}

export interface EventTableRowTag {
	tag_name: string;
}

// "events": [
// 	{
// 			"place_ident": "342Cc013",
// 			"name": "MACON 12",
// 			"event_id": "6f8e7c94-2e32-11e9-a502-dca9047ef277",
// 			"created": 1555607336000,
// 			"updated": 1555607336000,
// 			"longitude": -114.58099285266813,
// 			"latitude": 43.407518038240575,
// 			"state": "CORRECT",
// 			"tags": [
// 					{
// 							"tag_name": "base"
// 					}
// 			]
// 	},
// ],
// "current_page": 0,
// "total_items": 23,
// "total_pages": 2
