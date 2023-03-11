import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { SharedService } from "./shared.service";

@Injectable({
  providedIn: "root",
})
export class HttpRequestService {
  constructor(private http: HttpClient, private sharedService: SharedService) {}

  // get autocomplete suggestions
  /*
  get_autocomplete_suggestions(){
    return this.http.get(request_url, { responseType: "json" }).subscribe((res) => {
      let result_dict = JSON.parse(JSON.stringify(res));
      this.get_ticketmaster_result(result_dict["results"]["0"]["geometry"]["location"]["lat"], result_dict["results"]["0"]["geometry"]["location"]["lng"]);
    });
  }*/
  
  // get lat lng using Google Map API
  geoCode_send_request(request_url: string) {
    return this.http.get(request_url, { responseType: "json" }).subscribe((res) => {
      let result_dict = JSON.parse(JSON.stringify(res));
      this.get_ticketmaster_result(result_dict["results"]["0"]["geometry"]["location"]["lat"], result_dict["results"]["0"]["geometry"]["location"]["lng"]);
    });
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
  get_ticketmaster_result(lat: string, lng: string) {
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

    return this.http.get("http://localhost:3000/search/event-search", { params: params, responseType: "json" }).subscribe((res) => {
      console.log(res);
      let result_dict = JSON.parse(JSON.stringify(res));

      //possible steps to do after,
      // 1. pass result variables to the Table Ui component <app-table> using getter functions
      // 2. using the variables to draw the table UI in app_table.html
      // 3. include <app-table> into search_form.html, and maybe use ngIf* to toggle it
    });
  }
}
