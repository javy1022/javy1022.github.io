import { Component, OnInit, ElementRef, ViewChild, OnDestroy } from "@angular/core";
import { HttpRequestService } from "../http-request.service";
import { SharedService } from "../shared.service";
import { Subscription } from "rxjs";
import * as Constants from "../constants";
import { faSquareFacebook, faTwitter } from "@fortawesome/free-brands-svg-icons";
import { filter } from "rxjs/operators";

@Component({
  selector: "app-event-details",
  templateUrl: "./event-details.component.html",
  styleUrls: ["./event-details.component.css"],
})
export class EventDetailsComponent implements OnInit, OnDestroy {
  constructor(public http_request: HttpRequestService, public sharedService: SharedService) {}

  @ViewChild("eventDetails") eventDetails!: ElementRef;
  ARTISTS_SEPARATOR = Constants.ARTISTS_SEPARATOR;
  event_detail_subs: Subscription = new Subscription();
  clearEventDetailsSubscription: Subscription = new Subscription();
  spotifyArtistsSubscription: Subscription = new Subscription();
  /* Events tab */
  event_title!: string;
  local_date!: string;
  local_time!: string;
  artist_or_team: any[] = []; 
  venue!: string;
  genre: string = Constants.EMPTY;
  price_range!: any;
  status!: string;
  ticket_url!: string;
  seatmap_url!: string;
  fb_icon = faSquareFacebook;
  twitter_icon = faTwitter;

  /* Artist tab */
  artist_name_spotify: any[] = [];

  ngOnInit() {
    this.subs_event_details();
    this.subscribeToClearEventDetails();

    this.spotifyArtistsSubscription = this.sharedService.spotifyArtistsResult$.pipe(filter((resp) => resp !== null)).subscribe({
      next: (resp) => {
        if (resp) {
          this.extract_artists_spotify(resp);
          // You can now use the result in your component to render the view
        }
      },
      error: (error) => {
        console.error(error);
      },
    });
  }

  extract_artists_spotify(resp: any) {
    console.log(resp);
    if (resp?.artists?.items?.length !== 0) {
      let artist_obj = resp.artists.items[0];
      if (artist_obj?.name) {
        this.artist_name_spotify.push(artist_obj.name);
      }

      const artist_id = artist_obj?.id?.trim();
      this.get_artist_albumn(artist_id);
    }
   // console.log(this.artist_name_spotify);
  }

  get_artist_albumn(artist_id: string) {
    this.http_request.get_artist_with_id(artist_id).subscribe({
      next: (resp) => {
        // Handle the API response data here
        console.log(resp);
      },
      error: (error) => {
        console.error(error);
      },
    });
  }

  ngOnDestroy() {
    this.event_detail_subs.unsubscribe();
    this.clearEventDetailsSubscription?.unsubscribe();
    this.spotifyArtistsSubscription.unsubscribe();
  }

  subscribeToClearEventDetails(): void {
    this.clearEventDetailsSubscription = this.sharedService.clearEventDetails$.subscribe(() => {
      this.clear_btn();
    });
  }

  subs_event_details() {
    this.event_detail_subs = this.sharedService.event_detail$.subscribe({
      next: (resp) => {
        if (resp) {
          console.log(resp);
          this.extract_event_details(resp);
          //this.scroll_to_eventDetails();
        }
      },
      error: (error) => {
        console.error("Error occurred:", error);
      },
    });
  }

  clear_btn(): void {
    this.artist_or_team = [];   
  }

  scroll_to_eventDetails() {
    setTimeout(() => {
      this.eventDetails.nativeElement.scrollIntoView({
        behavior: "instant",
        block: "start",
      });
    }, 100);
  }

  back_to_table() {
    this.sharedService.current_info = "table";
    this.artist_or_team = [];  
    this.genre = Constants.EMPTY;
  }
  extract_event_details(resp: any) {
    this.event_title = resp?.name?.trim();
    if (this.event_title === undefined || this.event_title === Constants.UNDEFINED_CAP || this.event_title === Constants.UNDEFINED_LOW) this.event_title = Constants.EMPTY;

    this.local_date = resp?.dates?.start?.localDate?.trim();
    if (this.local_date === undefined || this.local_date === Constants.UNDEFINED_CAP || this.local_date === Constants.UNDEFINED_LOW) this.local_date = Constants.EMPTY;

    this.local_time = resp?.dates?.start?.localTime?.trim();
    if (this.local_time === undefined || this.local_time === Constants.UNDEFINED_CAP || this.local_time === Constants.UNDEFINED_LOW) this.local_time = Constants.EMPTY;

    let artists = resp?._embedded?.attractions;
    if (artists !== undefined && artists.length !== 0) {
      for (let i = 0; i < artists.length; i++) {
        let artist_name = artists[i]?.name?.trim();
        if (artist_name === undefined || artist_name === Constants.UNDEFINED_CAP || artist_name === Constants.UNDEFINED_LOW) this.artist_or_team.push(Constants.EMPTY);
        else {
          this.artist_or_team.push(artist_name);          
        }
      }
    }

    this.venue = resp?._embedded?.venues?.[0]?.name?.trim();
    if (this.venue === undefined || this.venue === Constants.UNDEFINED_CAP || this.venue === Constants.UNDEFINED_LOW) this.venue = Constants.EMPTY;

    if (resp?.classifications !== undefined) {
      let class_obj = resp.classifications?.[0];

      let segment = class_obj?.segment?.name?.trim();
      if (segment !== undefined && segment !== Constants.UNDEFINED_CAP && segment !== Constants.UNDEFINED_LOW) this.genre += segment + Constants.GENRE_SEPARATOR;

      let genre_name = class_obj?.genre?.name?.trim();
      if (genre_name !== undefined && genre_name !== Constants.UNDEFINED_CAP && genre_name !== Constants.UNDEFINED_LOW) this.genre += genre_name + Constants.GENRE_SEPARATOR;

      let sub_genre = class_obj?.subGenre?.name?.trim();
      if (sub_genre !== undefined && sub_genre !== Constants.UNDEFINED_CAP && sub_genre !== Constants.UNDEFINED_LOW) this.genre += sub_genre + Constants.GENRE_SEPARATOR;

      let type = class_obj?.name?.trim();
      if (type !== undefined && type !== Constants.UNDEFINED_CAP && type !== Constants.UNDEFINED_LOW) this.genre += type + Constants.GENRE_SEPARATOR;

      let subtype = class_obj?.subType?.name?.trim();
      if (subtype !== undefined && subtype !== Constants.UNDEFINED_CAP && subtype !== Constants.UNDEFINED_LOW) this.genre += subtype + Constants.GENRE_SEPARATOR;
      if (this.genre.length !== 0) this.genre = this.genre.slice(0, -2).trim();
    }

    this.price_range = resp?.priceRanges;
    if (this.price_range !== undefined && (this.price_range[0]?.max !== undefined || this.price_range[0]?.min !== undefined)) {
      let price_range_obj = this.price_range[0];
      let min = -1,
        max = -1;
      let currency = Constants.EMPTY;

      if (price_range_obj?.max !== undefined) max = price_range_obj.max;
      if (price_range_obj?.min !== undefined) min = price_range_obj.min;

      let currency_type = price_range_obj?.currency?.trim();
      if (currency_type !== undefined && currency_type !== Constants.UNDEFINED_CAP && currency_type !== Constants.UNDEFINED_LOW) currency = currency_type;

      if (min === -1) this.price_range = max + Constants.PRICE_RANGES_OPERATOR + max + " " + currency;
      else if (max === -1) this.price_range = min + Constants.PRICE_RANGES_OPERATOR + min + " " + currency;
      else this.price_range = min + Constants.PRICE_RANGES_OPERATOR + max + " " + currency;
    } else this.price_range = Constants.EMPTY;

    this.status = resp?.dates?.status?.code?.trim();
    if (this.status === undefined || this.status === Constants.UNDEFINED_CAP || this.status === Constants.UNDEFINED_LOW) this.status = Constants.EMPTY;

    if (this.status === "onsale") this.status = "On Sale";
    else if (this.status === "offsale") this.status = "Off Sale";
    else if (this.status === "cancelled" || this.status === "canceled") this.status = "Canceled";
    else if (this.status === "rescheduled") this.status = "Rescheduled";
    else if (this.status === "postponed") this.status = "Postponed";

    this.ticket_url = resp?.url?.trim();
    if (this.ticket_url === undefined || this.ticket_url === Constants.UNDEFINED_CAP || this.ticket_url === Constants.UNDEFINED_LOW) this.ticket_url = Constants.EMPTY;

    this.seatmap_url = resp?.seatmap?.staticUrl?.trim();
    if (this.seatmap_url === undefined || this.seatmap_url === Constants.UNDEFINED_CAP || this.seatmap_url === Constants.UNDEFINED_LOW) this.seatmap_url = Constants.EMPTY;
  }
}