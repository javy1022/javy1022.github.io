import { Component } from "@angular/core";
import { HttpRequestService } from "../http-request.service";
import * as Constants from "../constants";
import * as Config from "../config";
import { SharedService } from "../shared.service";

@Component({
  selector: "app-search-form",
  templateUrl: "./search-form.component.html",
  styleUrls: ["./search-form.component.css"],
})
export class SearchFormComponent {
  constructor(private http_request: HttpRequestService, public sharedService: SharedService) {}

  onClear() {
    if (this.sharedService.keyword_input != Constants.EMPTY) this.sharedService.keyword_input = Constants.EMPTY;
    if (this.sharedService.distance_input != 10) this.sharedService.distance_input = 10;
    if (this.sharedService.category_input != "Default") this.sharedService.category_input = "Default";
    if (this.sharedService.location_input != Constants.EMPTY) this.sharedService.location_input = Constants.EMPTY;
    if (this.sharedService.checkbox_input == true) this.sharedService.checkbox_input = false;
  }

  onSubmit() {
    const reg_geo_loc = /\s*$/;
    const reg_non_alphanumeric = /[^a-z0-9+]+/gi;
    const GOOGLE_API_HOST = "https://maps.googleapis.com";
    const GEOCODING_SEARCH_PATH = "/maps/api/geocode/json";

    if (!this.sharedService.checkbox_input) {
      let buffer = this.sharedService.location_input.replace(reg_geo_loc, Constants.EMPTY);
      let api_address_param = buffer.replace(reg_non_alphanumeric, "+");
      let url = GOOGLE_API_HOST + GEOCODING_SEARCH_PATH + "?address=" + api_address_param + "&key=" + Config.GOOGLE_API_KEY;
      this.http_request.geoCode_send_request(url);
    } else {
      const IPINFO_API_HOST = "https://ipinfo.io/";
      this.http_request.ipInfo_send_request(IPINFO_API_HOST + "?token=" + Config.IPINFO_API_KEY);
    }
  }

  onCheckboxChange() {
    if (this.sharedService.checkbox_input == true) {
      this.sharedService.location_input = Constants.EMPTY;
    }
  }

  onKeywordChange(){
     //this.http_request.get_autocomplete_suggestions();
  }

}

// move to another function
/*
		let request_url : string = "http://localhost:3000";    // switch to google server when deploying
		//let request_url : string = window.location.origin;     // when using same server such as google (for deploying)
		//console.log(window.location.origin);
		//request_url = request_url + "/keyword/" + this.keyword_input + "/distance/" + this.distance_input + "/category/" + this.category_input + "/location/" + this.location_input;
    request_url = request_url + "/search"
   	this.yelp_ajax.getConfig(request_url);
	*/
