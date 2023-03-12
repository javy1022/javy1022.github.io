import { Component , Inject, OnInit} from '@angular/core';
import { HttpRequestService } from "../http-request.service";
import { SearchFormComponent } from "../search-form/search-form.component"
import { SharedService } from "../shared.service";

@Component({
  selector: 'app-event-table',
  templateUrl: './event-table.component.html',
  styleUrls: ['./event-table.component.css']
})
export class EventTableComponent implements OnInit{
  constructor(private http_request: HttpRequestService, public sharedService: SharedService) { }
  
  ngOnInit() {
    this.sharedService.search_result.subscribe(data => {
      if (data) {
        
        console.log(data); // use the data here
      }
    });
  }
}
