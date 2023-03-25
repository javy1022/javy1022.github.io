import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { SharedService } from "./shared.service";
import { Observable } from "rxjs";
import { filter, map, concatMap, tap } from "rxjs/operators";
import * as Constants from "./constants";
import { forkJoin } from "rxjs";

@Injectable({
  providedIn: "root",
})
export class HttpRequestService {
  constructor(private http: HttpClient, private sharedService: SharedService) {}

  // Get autocomplete suggestions
  get_autocomplete_suggestions(): Observable<any> {
    const params = new HttpParams().set("keyword", this.sharedService.keyword_input);
    return this.http.get("http://localhost:3000/search/auto-complete", { params: params, responseType: "json" }).pipe(
      map((res: any) => {
        const attractions = res?._embedded?.attractions;
        if (attractions) {
          return { attractions };
        } else {
          return { attractions: [] };
        }
      })
    );
  }
  // Get event details with id
  get_request_event_detail(id: string, event: MouseEvent): void {
    event.preventDefault();
    const url = `http://localhost:3000/search/event-details/${id}`;
    this.sharedService.current_info = "event_details";
    this.http
      .get(url)
      .pipe(
        tap((result: any) => {
          let spotify_search = [];
          let artists = result?._embedded?.attractions;
          if (artists !== undefined && artists.length !== 0) {
            for (let i = 0; i < artists.length; i++) {
              let artist_name = artists[i]?.name?.trim();
              if (artist_name !== undefined && artist_name !== Constants.UNDEFINED_CAP && artist_name !== Constants.UNDEFINED_LOW) {
                const artist_category = artists?.[0]?.classifications?.[0]?.segment?.name?.trim();
                if (artist_category === "Music" && spotify_search.length < 3) spotify_search.push(artist_name);
              }
            }
          }

          // Use forkJoin to make parallel requests and maintain the order of responses
          const observables = spotify_search.map((artist_name) => this.spotify_searchArtists(artist_name));
          forkJoin([...observables]).subscribe((results) => {
            results.forEach((result) => {
              this.sharedService.spotifyArtistsResultSource.next(result);
            });
          });
        })
      )
      .subscribe((result) => {
        this.sharedService.eventDetailSource.next(result);
      });
  }

  // Get artist from Spotify using artist's name
  spotify_searchArtists(q: string): Observable<any> {
    const url = "http://localhost:3000/search/artists";
    const params = new HttpParams().set("q", q);
    return this.http.get(url, { params: params, responseType: "json" });
  }

  //
  get_artist_with_id(artist_id: string): Observable<any> {
    return this.http.get(`http://localhost:3000/search/artists-id/${artist_id}`);
  }

  // Get lat lng using Google geolocation API
  geoCode_send_request(request_url: string): Observable<any> {
    return this.http.get(request_url, { responseType: "json" }).pipe(
      map((res: any) => {
        if (res.status != "ZERO_RESULTS") {
          const lat_lng_obj = res?.results?.[0]?.geometry?.location;
          return {
            lat: lat_lng_obj?.lat,
            lng: lat_lng_obj?.lng,
          };
        } else {
          return;
        }
      }),
      filter((location): location is { lat: string; lng: string } => location !== undefined),
      concatMap((location) => {
        return this.get_ticketmaster_result(location.lat, location.lng);
      })
    );
  }

  // Get lat lng using ipInfo API
  ipInfo_send_request(request_url: string): Observable<any> {
    return this.http.get(request_url, { responseType: "json" }).pipe(
      map((res: any) => {
        const buffer = res.loc;
        const lat_lng_array = buffer.split(",");
        return { lat: lat_lng_array[0], lng: lat_lng_array[1] };
      }),
      concatMap((location) => {
        return this.get_ticketmaster_result(location.lat, location.lng);
      })
    );
  }

  // Get events from Ticketmaster API
  get_ticketmaster_result(lat: string, lng: string): Observable<any> {
    const params = new HttpParams({
      fromObject: {
        lat: lat,
        lng: lng,
        keyword: this.sharedService.keyword_input,
        distance: this.sharedService.distance_input,
        category: this.sharedService.category_input,
      },
    });

    return this.http.get("http://localhost:3000/search/event-search", { params: params, responseType: "json" }).pipe(
      map((res: any) => {
        const result_dict = JSON.parse(JSON.stringify(res));
        const data = res?._embedded?.events;
        if (data && data.length !== 0) {
          this.sharedService.show_table = true;
          return result_dict;
        } else {
          console.log("No Keyword");
          return;
        }
      }),
      filter((result_dict): result_dict is {} => result_dict !== undefined)
    );
  }
}
