import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { SharedService } from "./shared.service";

@Injectable({
  providedIn: "root",
})
export class YelpAjaxService {
  constructor(private http: HttpClient, private sharedService: SharedService) {}

  // get lat lng and call ticketmaster
  geoCode_send_request(request_url: string) {
    return this.http.get(request_url, { responseType: "json" }).subscribe((res) => {
      var result_dict = JSON.parse(JSON.stringify(res));
      this.get_ticketmaster_result(result_dict["results"]["0"]["geometry"]["location"]["lat"], result_dict["results"]["0"]["geometry"]["location"]["lng"]);
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

    return this.http.get("http://localhost:3000/search", { params: params, responseType: "json" }).subscribe((res) => {
      console.log(res);
      let result_dict = JSON.parse(JSON.stringify(res));

      //possible steps to do after,
      // 1. pass result variables to the Table Ui component <app-table> using getter functions
      // 2. using the variables to draw the table UI in app_table.html
      // 3. include <app-table> into search_form.html, and maybe use ngIf* to toggle it
    });
  }
}
