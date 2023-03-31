import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import * as Constants from "./constants";
import { Subject } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class SharedService {
  constructor() { this.load_fav_states_from_storage();this.load_fav_table_from_storage();}

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

  public search_result_source = new Subject<any>();
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

  public window: Window = window;
  public fav_storage_table: any[][] = [];
  public fav_toggles_dict: { [key: string]: boolean } = {};

  public table_no_result: boolean = false;

  
  load_fav_states_from_storage() {
    const fav_states = this.window.localStorage.getItem('fav_toggles_dict');
    if (fav_states) {
      this.fav_toggles_dict = JSON.parse(fav_states);
    }
  }

  load_fav_table_from_storage() {
    const fav_table = this.window.localStorage.getItem('fav');
    if (fav_table) {
      this.fav_storage_table = JSON.parse(fav_table);
    }
  }

  resetTabs() {
    this.resetTabsSource.next();
  }
}
