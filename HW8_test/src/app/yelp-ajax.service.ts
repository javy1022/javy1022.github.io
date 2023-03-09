import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse, HttpResponse } from '@angular/common/http';

import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class YelpAjaxService {

  constructor(private http : HttpClient) { }
  
  getConfig(request_url: string) {
		

    return this.http.get(request_url, {responseType: 'json'}).subscribe(res => {
		
		// sample javascript to extract data from json response
        console.log(res);
		var search_result = JSON.parse(JSON.stringify(res));
		
		const business_id = search_result.businesses[0].id;
		const business_name = search_result.businesses[0].name;
		const image_url = search_result.businesses[0].image_url;
		
		console.log(business_id);
		console.log(business_name);
		console.log(image_url);
		
		//possible steps to do after,
		// 1. pass result variables to the Table Ui component <app-table> using getter functions
		// 2. using the variables to draw the table UI in app_table.html
		// 3. include <app-table> into search_form.html, and maybe use ngIf* to toggle it	
		 
    });
}
  
}
