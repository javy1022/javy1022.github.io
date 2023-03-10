import { Component } from '@angular/core';
import { YelpAjaxService } from '../yelp-ajax.service';
import * as Constants from "../constants";

@Component({
  selector: 'app-search-form',
  templateUrl: './search-form.component.html',
  styleUrls: ['./search-form.component.css']
})
export class SearchFormComponent {     

  constructor(private yelp_ajax: YelpAjaxService) {
    
   }
 
   // learn to use interface if have time
   keyword_input: string = Constants.EMPTY;
   distance_input: number = 10;
   category_input: string = 'Default';
   location_input: string = Constants.EMPTY;
   checkbox_input: boolean = false;
   
   
  onClear(){
    if (this.keyword_input != Constants.EMPTY) this.keyword_input = Constants.EMPTY;
    if ( this.distance_input != 10) this.distance_input = 10;
    if (this.category_input != "Default") this.category_input = "Default";
    if (this.location_input != Constants.EMPTY) this.location_input = Constants.EMPTY;
    if (this.checkbox_input == true) this.checkbox_input = false;
  }
   
   onSubmit() { 
		let request_url : string = "http://localhost:3000";    // switch to google server when deploying
		//let request_url : string = window.location.origin;     // when using same server such as google (for deploying)
		//console.log(window.location.origin);
		request_url = request_url + "/keyword/" + this.keyword_input + "/distance/" + this.distance_input + "/category/" + this.category_input + "/location/" + this.location_input;
						
		this.yelp_ajax.getConfig(request_url);
	
   }
    
   onCheckboxChange(){
    if (this.checkbox_input == true) {
      this.location_input = Constants.EMPTY;
    }
  }
}
