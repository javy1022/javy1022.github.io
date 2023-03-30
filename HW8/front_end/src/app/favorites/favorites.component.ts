import { Component, OnInit } from "@angular/core";
import { SharedService } from "../shared.service";


@Component({
  selector: "app-favorites",
  templateUrl: "./favorites.component.html",
  styleUrls: ["./favorites.component.css"],
})
export class FavoritesComponent implements OnInit {
  constructor(public sharedService: SharedService) {}
  ngOnInit() {
    //this.sharedService.window.localStorage.clear();
    this.getTableData();
    //console.log(this.sharedService.window.localStorage.length)
 }
  getTableData(): any[][] {
    const tableData = this.sharedService.window.localStorage.getItem('fav');
    //console.log( tableData ? JSON.parse(tableData) : [])
    return tableData ? JSON.parse(tableData) : [];
  }
  

}
