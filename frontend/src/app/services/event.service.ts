import {Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';

import {environment} from "../../environments/environment";

import {QueryOptions} from "../components/event-table/event-table.component";
import {Event} from "../models/event";

@Injectable({
  providedIn: 'root'
})
export class EventService {

  url = `${environment.apiUrl}/events`;

  constructor(private http: HttpClient) { }

  getAll(query: QueryOptions) {
    console.log(query);
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
    return this.http.get(this.url, {params});
  }

  getById(id: string) {
    return "";
  }

  findInRadius(radius: number, latitude: number, longitude: number) {
    return "";
  }

  update(id: string, event: Event) {
    return "";
  }

  delete(id: string) {
    return "";
  }
}
