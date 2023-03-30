import { Component, OnInit } from "@angular/core";
import { SharedService } from "../shared.service";


@Component({
  selector: "app-favorites",
  templateUrl: "./favorites.component.html",
  styleUrls: ["./favorites.component.css"],
})
export class FavoritesComponent implements OnInit {
  constructor(public sharedService: SharedService) {}   

  ngOnInit(): void {
    //this.sharedService.window.localStorage.clear();
    console.log("temp");
  }

  remove_event_touch(event_index:number ,event:  TouchEvent){      
    console.log("mumi")
    console.log(event_index)
  }

  remove_event_click(event_index:number ,event:  MouseEvent){      
    console.log("mumi")
    console.log(event_index)
  }
  
}
