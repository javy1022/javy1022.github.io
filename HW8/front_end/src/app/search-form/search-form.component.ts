import { Component, ViewChild} from "@angular/core";
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
  ac_list = [];
  isLoading = false;

  onClear() {
    // Search form
    if (this.sharedService.keyword_input !== Constants.EMPTY) this.sharedService.keyword_input = Constants.EMPTY;
    if (this.sharedService.distance_input !== 10) this.sharedService.distance_input = 10;
    if (this.sharedService.category_input !== "Default") this.sharedService.category_input = "Default";
    if (this.sharedService.location_input !== Constants.EMPTY) this.sharedService.location_input = Constants.EMPTY;
    if (this.sharedService.checkbox_input === true) this.sharedService.checkbox_input = false;
    this.ac_list = [];

    // Event table
    this.sharedService.search_result_source.next(null);
    this.sharedService.list_for_table = [];
    this.sharedService.current_info = "";

    this.sharedService.clearEventDetails$.next();
 
  }

  searchResult$ = this.sharedService.search_result_source.asObservable();
  onSubmit() {
    const reg_geo_loc = /\s*$/;
    const reg_non_alphanumeric = /[^a-z0-9+]+/gi;
    const GOOGLE_API_HOST = "https://maps.googleapis.com";
    const GEOCODING_SEARCH_PATH = "/maps/api/geocode/json";
    // this.sharedService.searchResultSource.next(null);  maybe? idk
    this.sharedService.list_for_table = [];
    this.sharedService.current_info = "table";
    this.sharedService.resetTabs();
    this.sharedService.clearEventDetails$.next();

    if (!this.sharedService.checkbox_input) {
      let buffer = this.sharedService.location_input.replace(reg_geo_loc, Constants.EMPTY);
      let api_address_param = buffer.replace(reg_non_alphanumeric, "+");
      let url = GOOGLE_API_HOST + GEOCODING_SEARCH_PATH + "?address=" + api_address_param + "&key=" + Config.GOOGLE_API_KEY;
      this.http_request.geoCode_send_request(url).subscribe({
        next: (result) => {
          if (result) {
            this.sharedService.search_result_source.next(result);
          }
        }
      });
    } else {
      const IPINFO_API_HOST = "https://ipinfo.io/";
      this.http_request.ipInfo_send_request(IPINFO_API_HOST + "?token=" + Config.IPINFO_API_KEY).subscribe({
        next: (result) => {
          this.sharedService.search_result_source.next(result);
        }
      });
    }
  }

  onCheckboxChange() {
    if (this.sharedService.checkbox_input === true) {
      this.sharedService.location_input = Constants.EMPTY;
    }
  }

  onKeywordChange() {
    this.isLoading = true;
    setTimeout(() => {
      this.http_request.get_autocomplete_suggestions().subscribe((res) => {
        const attractions = res["attractions"];
        const names = attractions.map((attraction: { name: string }) => attraction.name);
        this.ac_list = names;
        if (this.sharedService.keyword_input === Constants.EMPTY) this.ac_list = [];
        this.isLoading = false;
      });
    }, 500);
  }
}

// move to another function
/*
		let request_url : string = "http://localhost:3000";    // switch to google server when deploying
		//let request_url : string = window.location.origin;     // when using same server such as google (for deploying)
		//console.log(window.location.origin);
		
	*/
