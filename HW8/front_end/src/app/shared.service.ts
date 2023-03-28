import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import * as Constants from "./constants";
import { Subject } from "rxjs";


@Injectable({
  providedIn: "root",
})
export class SharedService {
  constructor() {}

  //public keyword_input: string = Constants.EMPTY;
  public keyword_input: string = "P!NK";
  public distance_input: number = 10;
  public category_input: string = "Default";
  //public location_input: string = Constants.EMPTY;
  public location_input: string = "New York";
  public checkbox_input: boolean = false;

  public show_table: boolean = false;
  public current_info: string = "";

  public list_for_table: any[] = [];

  public search_result_source = new BehaviorSubject<any>(null);
  search_result$ = this.search_result_source.asObservable();

  public eventDetailSource = new BehaviorSubject<any>(null);
  event_detail$ = this.eventDetailSource.asObservable();

  public spotifyArtistsResultSource = new BehaviorSubject<any>(null);
  spotifyArtistsResult$ = this.spotifyArtistsResultSource.asObservable();
 
  public spotifyArtistDataSource = new BehaviorSubject<any>(null);
  spotifyArtistData$= this.spotifyArtistsResultSource.asObservable();

  venueResponseSource = new BehaviorSubject<any>(null);
  venueResponse$ = this.venueResponseSource.asObservable();


  private resetTabsSource = new Subject<void>();
  resetTabs$ = this.resetTabsSource.asObservable();

  clearEventDetails$ = new Subject<void>();

  resetTabs() {
    this.resetTabsSource.next();
  }
}
