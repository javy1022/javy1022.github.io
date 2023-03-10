import { Component } from '@angular/core';
import { NgForm }   from '@angular/forms';

import { YelpAjaxService } from '../yelp-ajax.service';


@Component({
  selector: 'app-search-form',
  templateUrl: './search-form.component.html',
  styleUrls: ['./search-form.component.css']
})
export class SearchFormComponent {     

  constructor(private yelp_ajax: YelpAjaxService) {
    
   }
 
   // learn to use interface if have time
   keyword_input: string = '';
   distance_input: number = 10;
   category_input: string = '';
   location_input: string = '';
   
  onClear(){
	var search_form = <HTMLFormElement>document.getElementById("search_form");
	//can't use this if hw ask to use javascript to reset form (is reset javascript?)
    search_form.reset();
  }
   
   onSubmit() { 
		let request_url : string = "http://localhost:3000";    // switch to google server when deploying
		//let request_url : string = window.location.origin;     // when using same server such as google (for deploying)
		//console.log(window.location.origin);
		request_url = request_url + "/keyword/" + this.keyword_input + "/distance/" + this.distance_input + "/category/" + this.category_input + "/location/" + this.location_input;
						
		this.yelp_ajax.getConfig(request_url);
	
   }
    
}
