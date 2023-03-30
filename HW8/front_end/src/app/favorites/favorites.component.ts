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
}
