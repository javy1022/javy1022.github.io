import { Component, OnInit} from '@angular/core';
import { SharedService } from "../shared.service";

@Component({
  selector: 'app-event-table',
  templateUrl: './event-table.component.html',
  styleUrls: ['./event-table.component.css']
})
export class EventTableComponent implements OnInit{
  constructor(public sharedService: SharedService) { }
  
  ngOnInit() {
    this.sharedService.search_result.subscribe(data => {
      if (data) {        
        console.log(data); // use the data here
      }
    });
  }
}
