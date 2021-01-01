import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';

import {environment} from "../../environments/environment";

import {QueryOptions} from "../components/event-table/event-table.component";
import {Event} from "../models/event";
import {EventTableData, EventTableRow} from "../models/event-table-data";

@Injectable({
  providedIn: 'root'
})
export class EventService {

  url = `${environment.apiUrl}/events`;

  constructor(private http: HttpClient) { }

  getAll(query: QueryOptions) {
    let params = new HttpParams()
      .set('page', (query.page - 1).toString())
      .set('size', query.pageSize.toString());
    if (query.sortColumn && query.sortDirection) {
      params = params.set('sort', `${query.sortColumn},${query.sortDirection}`);
    }
    if (query.searchTerm.value.trim() !== '') {
      params = params.set('search', query.searchTerm.value.trim());
    }
    console.log(params);
    return this.http.get<EventTableData>(this.url, {params});
  }

  getById(id: string) {
    return this.http.get<Event>(`${this.url}/${id}`);
  }

  findInRadius(radius: number, latitude: number, longitude: number) {
    const params = new HttpParams()
      .set('size', radius.toString())
      .set('lon', longitude.toString())
      .set('lat', latitude.toString());
    return this.http.get<EventTableRow[]>(`${this.url}/radius`, {params});
  }

  update(id: string, event: Event) {
    return this.http.put(`${this.url}/${id}`, event);
  }

  delete(id: string) {
    return this.http.delete(`${this.url}/${id}`);
  }
}
