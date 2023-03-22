import { Component, AfterViewInit, ElementRef, ViewChild, OnDestroy } from "@angular/core";
import { HttpRequestService } from "../http-request.service";
import { SharedService } from "../shared.service";
import { Subscription } from "rxjs";
import * as Constants from "../constants";

@Component({
  selector: "app-event-details",
  templateUrl: "./event-details.component.html",
  styleUrls: ["./event-details.component.css"],
})
export class EventDetailsComponent implements AfterViewInit, OnDestroy {
  constructor(public http_request: HttpRequestService, public sharedService: SharedService) {}

  @ViewChild("eventDetails") eventDetails!: ElementRef;
  event_detail_subs: Subscription = new Subscription();

  event_title = ""

  ngAfterViewInit() {
    this.subs_event_details();
  }
  ngOnDestroy() {
    this.event_detail_subs.unsubscribe();
  }

  subs_event_details() {
    this.event_detail_subs = this.sharedService.eventDetail$.subscribe({
      next: (resp) => {
        if (resp) {
          console.log(resp);
          this.extract_event_details(resp);
          this.scroll_to_eventDetails();
          // Process the result here
        }
      },
      error: (error) => {
        console.error("Error occurred:", error);
      },
    });
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
  }
  extract_event_details(resp: any) {
    let local_date, local_time, venue, price_range, status, ticket_url, seatmap_url;
    let artist_or_team = [];
    let genres = Constants.EMPTY;

    this.event_title = resp?.name?.trim();
    if (this.event_title === undefined || this.event_title === Constants.UNDEFINED_CAP || this.event_title  === Constants.UNDEFINED_LOW) this.event_title = Constants.EMPTY;

    local_date = resp?.dates?.start?.localDate?.trim();
    if (local_date === undefined || local_date === Constants.UNDEFINED_CAP || local_date === Constants.UNDEFINED_LOW) local_date = Constants.EMPTY;

    local_time = resp?.dates?.start?.localTime?.trim();
    if (local_time === undefined || local_time === Constants.UNDEFINED_CAP || local_time === Constants.UNDEFINED_LOW) local_time = Constants.EMPTY;

    let artists = resp?._embedded?.attractions;
    if (artists !== undefined && artists.length !== 0) {
      for (let i = 0; i < artists.length; i++) {
        let buffer = [];

        let artist_name = artists[i]?.name?.trim();
        if (artist_name === undefined || artist_name === Constants.UNDEFINED_CAP || artist_name === Constants.UNDEFINED_LOW) buffer.push(Constants.EMPTY);
        else buffer.push(artist_name);

        let colab_artists = artists[i]?.url?.trim();
        if (colab_artists === undefined || colab_artists === Constants.UNDEFINED_CAP || colab_artists === Constants.UNDEFINED_LOW) buffer.push(Constants.EMPTY);
        else buffer.push(colab_artists);

        artist_or_team.push(buffer);
      }
    }

    venue = resp?._embedded?.venues?.[0]?.name?.trim();
    if (venue === undefined || venue === Constants.UNDEFINED_CAP || venue === Constants.UNDEFINED_LOW) venue = Constants.EMPTY;

    if (resp?.classifications !== undefined) {
      let class_obj = resp.classifications?.[0];

      let segment = class_obj?.segment?.name?.trim();
      if (segment !== undefined && segment !== Constants.UNDEFINED_CAP && segment !== Constants.UNDEFINED_LOW) genres += segment + Constants.GENRE_SEPARATOR;

      let genre_name = class_obj?.genre?.name?.trim();
      if (genre_name !== undefined && genre_name !== Constants.UNDEFINED_CAP && genre_name !== Constants.UNDEFINED_LOW) genres += genre_name + Constants.GENRE_SEPARATOR;

      let sub_genre = class_obj?.subGenre?.name?.trim();
      if (sub_genre !== undefined && sub_genre !== Constants.UNDEFINED_CAP && sub_genre !== Constants.UNDEFINED_LOW) genres += sub_genre + Constants.GENRE_SEPARATOR;

      let type = class_obj?.name?.trim();
      if (type !== undefined && type !== Constants.UNDEFINED_CAP && type !== Constants.UNDEFINED_LOW) genres += type + Constants.GENRE_SEPARATOR;

      let subtype = class_obj?.subType?.name?.trim();
      if (subtype !== undefined && subtype !== Constants.UNDEFINED_CAP && subtype !== Constants.UNDEFINED_LOW) genres += subtype + Constants.GENRE_SEPARATOR;
      if (genres.length !== 0) genres = genres.slice(0, -2).trim();
    }

    price_range = resp?.priceRanges;
    if (price_range !== undefined && (price_range[0]?.max !== undefined || price_range[0]?.min !== undefined)) {
      let price_range_obj = price_range[0];
      let min = -1,
        max = -1;
      let currency = Constants.EMPTY;

      if (price_range_obj?.max !== undefined) max = price_range_obj.max;
      if (price_range_obj?.min !== undefined) min = price_range_obj.min;

      let currency_type = price_range_obj?.currency?.trim();
      if (currency_type !== undefined && currency_type !== Constants.UNDEFINED_CAP && currency_type !== Constants.UNDEFINED_LOW) currency = currency_type;

      if (min === -1) price_range = max + Constants.PRICE_RANGES_OPERATOR + max + " " + currency;
      else if (max === -1) price_range = min + Constants.PRICE_RANGES_OPERATOR + min + " " + currency;
      else price_range = min + Constants.PRICE_RANGES_OPERATOR + max + " " + currency;
    } else price_range = Constants.EMPTY;

    status = resp?.dates?.status?.code?.trim();
    if (status === undefined || status === Constants.UNDEFINED_CAP || status === Constants.UNDEFINED_LOW) status = Constants.EMPTY;

    ticket_url = resp?.url?.trim();
    if (ticket_url === undefined || ticket_url === Constants.UNDEFINED_CAP || ticket_url === Constants.UNDEFINED_LOW) ticket_url = Constants.EMPTY;

    seatmap_url = resp?.seatmap?.staticUrl?.trim();
    if (seatmap_url === undefined || seatmap_url === Constants.UNDEFINED_CAP || seatmap_url === Constants.UNDEFINED_LOW) seatmap_url = Constants.EMPTY;
    console.log(this.event_title)
  }
}
