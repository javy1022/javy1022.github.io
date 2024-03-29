import { Component, OnInit, ElementRef, ViewChild, OnDestroy } from "@angular/core";
import { HttpRequestService } from "../http-request.service";
import { SharedService } from "../shared.service";
import { Subscription } from "rxjs";
import * as Constants from "../constants";
import { faSquareFacebook, faTwitter, faSpotify } from "@fortawesome/free-brands-svg-icons";
import { filter, concatMap } from "rxjs/operators";
import { MatTabGroup } from "@angular/material/tabs";

@Component({
  selector: "app-event-details",
  templateUrl: "./event-details.component.html",
  styleUrls: ["./event-details.component.css"],
})
export class EventDetailsComponent implements OnInit, OnDestroy {
  constructor(public http_request: HttpRequestService, public sharedService: SharedService) {}
  @ViewChild(MatTabGroup) tabGroup!: MatTabGroup;
  @ViewChild("eventDetails") eventDetails!: ElementRef;

  ARTISTS_SEPARATOR = Constants.ARTISTS_SEPARATOR;
  event_detail_subs: Subscription = new Subscription();
  clear_eventDetails_sub: Subscription = new Subscription();
  spotify_artists_sub: Subscription = new Subscription();
  /* Events tab */
  event_title!: string;
  event_id!: string;
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
  artist_name_spotify: string[] = [];
  artist_img_spotify: string[] = [];
  artist_popularity_spotify: number[] = [];
  artist_followersNum_spotify: number[] = [];
  artist_spotify_link: string[] = [];
  artists_spotify_albumns: any[][] = [];
  spotify_icon = faSpotify;

  /* Venue tab */
  venue_name = Constants.EMPTY;
  venue_address: string = Constants.EMPTY;
  venue_phone: string = Constants.EMPTY;
  venue_hours: string = Constants.EMPTY;
  venue_general_rule: string = Constants.EMPTY;
  venue_child_rule: string = Constants.EMPTY;
  venue_lat!: any;
  venue_lng!: any;

  hours_toggle: boolean = false;
  general_rule_toggle: boolean = false;
  child_rule_toggle: boolean = false;
  favorite_toggle: boolean = false;

  marker?: any;
  mapOptions?: any;

  ngOnInit() {
    this.subs_event_details();
    this.clear_EventDetails_sub();

    this.spotify_artists_sub = this.sharedService.spotify_artists_result$
      .pipe(
        filter((resp) => resp !== null),
        concatMap((resp) => this.extract_artists_spotify(resp))
      )
      .subscribe({
        next: (resp) => {
          if (resp?.items) {
            let artist_album: string[] = [];
            for (let album of resp.items) {
              if (album?.images) artist_album.push(album.images[0].url);
            }
            this.artists_spotify_albumns.push(artist_album);
          }
        },
      });

    this.sharedService.venue_result$.subscribe((resp) => {
      if (resp) {
        const venue_obj = resp?._embedded?.venues?.[0];

        if (venue_obj) {
          if (venue_obj?.name) this.venue_name = venue_obj.name.trim();

          const address_obj = venue_obj?.address?.line1;
          const city_obj = venue_obj?.city?.name;
          const state_obj = venue_obj?.state?.name;
          if (address_obj || city_obj || state_obj) {
            if (address_obj) this.venue_address += address_obj.trim() + ", ";
            if (city_obj) this.venue_address += city_obj.trim() + ", ";
            if (state_obj) this.venue_address += state_obj.trim() + ", ";
            if (this.venue_address !== "") this.venue_address = this.venue_address.slice(0, -2);
          }

          const phone_obj = venue_obj?.boxOfficeInfo?.phoneNumberDetail;
          if (phone_obj) this.venue_phone = phone_obj.trim();

          const hours_obj = venue_obj?.boxOfficeInfo?.openHoursDetail;
          if (hours_obj) this.venue_hours = hours_obj.trim();

          const general_rule_obj = venue_obj?.generalInfo?.generalRule;
          if (general_rule_obj) this.venue_general_rule = general_rule_obj.trim();

          const child_rule_obj = venue_obj?.generalInfo?.childRule;
          if (child_rule_obj) this.venue_child_rule = child_rule_obj.trim();

          const locale_obj = venue_obj?.location;
          if (locale_obj.latitude && locale_obj.longitude) {
            this.venue_lat = parseFloat(locale_obj.latitude);
            this.venue_lng = parseFloat(locale_obj.longitude);
            this.mapOptions = {
              center: { lat: this.venue_lat, lng: this.venue_lng },
              zoom: 14,
            };
            this.marker = {
              position: { lat: this.venue_lat, lng: this.venue_lng },
            };
          }
        }
      }
    });

    this.sharedService.resetTabs$.subscribe(() => this.setActiveTab());
  }

  hours_info_toggle() {
    this.hours_toggle = !this.hours_toggle;
  }

  general_rule_info_toggle() {
    this.general_rule_toggle = !this.general_rule_toggle;
  }

  child_rule_info_toggle() {
    this.child_rule_toggle = !this.child_rule_toggle;
  }

  favorite_btn_toggle(key: string) {
    this.sharedService.fav_toggles_dict[key] = !this.sharedService.fav_toggles_dict[key];
    this.sharedService.window.localStorage.setItem("fav_toggles_dict", JSON.stringify(this.sharedService.fav_toggles_dict));

    if (this.sharedService.fav_toggles_dict[key] === true) {
      alert("Event Added to Favorites!");
      this.fav_storage_and_table_push(this.local_date, this.event_title, this.genre, this.venue, this.event_id);
    } else {
      alert("Removed from Favorites!");
      this.fav_storage_and_table_remove(this.event_id);
    }
  }

  setActiveTab() {
    this.tabGroup.selectedIndex = 0;
  }

  async extract_artists_spotify(resp: any) {
    if (resp?.artists?.items?.length !== 0) {
      let artist_obj = resp.artists.items[0];
      if (artist_obj?.name) this.artist_name_spotify.push(artist_obj.name.trim());
      if (artist_obj?.images?.[2]?.url) this.artist_img_spotify.push(artist_obj.images[2].url.trim());
      if (artist_obj?.popularity) this.artist_popularity_spotify.push(artist_obj.popularity);
      if (artist_obj?.followers?.total) this.artist_followersNum_spotify.push(artist_obj.followers.total.toLocaleString());
      if (artist_obj?.external_urls?.spotify) this.artist_spotify_link.push(artist_obj.external_urls.spotify.trim());

      const artist_id = artist_obj?.id?.trim();
      return this.get_artist_albumn(artist_id).toPromise();
    }
  }

  get_artist_albumn(artist_id: string) {
    return this.http_request.get_artist_with_id(artist_id);
  }

  ngOnDestroy() {
    this.event_detail_subs.unsubscribe();
    this.clear_eventDetails_sub?.unsubscribe();
    this.spotify_artists_sub.unsubscribe();
  }

  clear_EventDetails_sub(): void {
    this.clear_eventDetails_sub = this.sharedService.clear_event_details$.subscribe(() => {
      this.clear_btn();
    });
  }

  subs_event_details() {
    this.event_detail_subs = this.sharedService.event_detail$.subscribe({
      next: (resp) => {
        if (resp) {
          this.extract_event_details(resp);
        }
      },
    });
  }

  clear_btn(): void {
    this.back_to_table();
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
    this.artist_name_spotify = [];
    this.artist_img_spotify = [];
    this.artist_popularity_spotify = [];
    this.artist_followersNum_spotify = [];
    this.artists_spotify_albumns = [];
    this.artist_spotify_link = [];
    this.venue_name = Constants.EMPTY;
    this.venue_address = Constants.EMPTY;
    this.venue_phone = Constants.EMPTY;
    this.venue_hours = Constants.EMPTY;
    this.venue_general_rule = Constants.EMPTY;
    this.venue_child_rule = Constants.EMPTY;
    this.hours_toggle = false;
    this.general_rule_toggle = false;
    this.child_rule_toggle = false;
  }
  extract_event_details(resp: any) {
    this.event_title = resp?.name?.trim();
    if (this.event_title === undefined || this.event_title === Constants.UNDEFINED_CAP || this.event_title === Constants.UNDEFINED_LOW) this.event_title = Constants.EMPTY;

    this.event_id = resp?.id?.trim();
    if (this.event_id === undefined || this.event_id === Constants.UNDEFINED_CAP || this.event_id === Constants.UNDEFINED_LOW) this.event_id = Constants.EMPTY;

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

  fav_storage_and_table_push(local_date: string, event_title: string, genre: string, venue: string, event_id: string) {
    let table_row_buffer: any = [];
    let local_storage = this.sharedService.window.localStorage;
    let fav_table = this.sharedService.fav_storage_table;

    table_row_buffer.push(local_date);
    table_row_buffer.push(event_title);
    table_row_buffer.push(genre);
    table_row_buffer.push(venue);
    table_row_buffer.push(event_id);

    fav_table.push(table_row_buffer);

    local_storage.setItem("fav", JSON.stringify(fav_table));
  }

  fav_storage_and_table_remove(event_id: string) {
    let fav_table = this.sharedService.fav_storage_table;
    let local_storage = this.sharedService.window.localStorage;
    const target_index = fav_table.findIndex((event) => event[4] === event_id);
    fav_table.splice(target_index, 1);
    local_storage.setItem("fav", JSON.stringify(fav_table));
  }
}
