import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { SharedService } from "./shared.service";
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { switchMap } from 'rxjs/operators';



@Injectable({
  providedIn: "root",
})
export class HttpRequestService {
  constructor(private http: HttpClient, private sharedService: SharedService) {}
  
  // get autocomplete suggestions
  
  get_autocomplete_suggestions(){

    const params = new HttpParams().set('keyword', this.sharedService.keyword_input);
    return this.http.get("http://localhost:3000/search/auto-complete", { params: params, responseType: "json" }).subscribe((res) => {
      console.log(res);
       //let result_dict = JSON.parse(JSON.stringify(res));
     
    });
  }
  
  // get lat lng using Google Map API
  geoCode_send_request(request_url: string): Observable<any> {
    return this.http.get(request_url, { responseType: "json" })
      .pipe(
        map((res: any) => {
          let result_dict = JSON.parse(JSON.stringify(res));
          return {
            lat: result_dict["results"]["0"]["geometry"]["location"]["lat"],
            lng: result_dict["results"]["0"]["geometry"]["location"]["lng"]
          };
        }),
        switchMap((location) => {
          return this.get_ticketmaster_result(location.lat, location.lng);
        })
      );
  }

  // get lat lng using ipInfo API
  ipInfo_send_request(request_url: string) {
    return this.http.get(request_url, { responseType: "json" }).subscribe((res) => {
      let result_dict = JSON.parse(JSON.stringify(res));
      let buffer = result_dict["loc"];
      let lat_lng_array = buffer.split(",");
      this.get_ticketmaster_result(lat_lng_array[0], lat_lng_array[1]);      
    });
  }

  // api request to Ticketmaster
  get_ticketmaster_result(lat: string, lng: string): Observable<any> {
    const params = new HttpParams({
      fromObject: {
        lat: lat,
        lng: lng,
        keyword: this.sharedService.keyword_input,
        distance: this.sharedService.distance_input,
        category: this.sharedService.category_input,
        location: this.sharedService.location_input,
      },
    });

    return this.http.get("http://localhost:3000/search/event-search", { params: params, responseType: "json" })
    .pipe(
      map((res: any) => {
        let result_dict = JSON.parse(JSON.stringify(res));
        return result_dict;
      })
    );
  }
}
