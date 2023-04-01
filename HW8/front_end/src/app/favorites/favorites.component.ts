import { Component } from "@angular/core";
import { SharedService } from "../shared.service";

@Component({
  selector: "app-favorites",
  templateUrl: "./favorites.component.html",
  styleUrls: ["./favorites.component.css"],
})
export class FavoritesComponent {
  constructor(public sharedService: SharedService) {}

  remove_event_touch(event_index: number, fav_icon_key: string, event: TouchEvent) {
    alert("Removed from favorites!");

    let fav_table = this.sharedService.fav_storage_table;
    let local_storage = this.sharedService.window.localStorage;
    fav_table.splice(event_index, 1);
    local_storage.setItem("fav", JSON.stringify(fav_table));

    this.sharedService.fav_toggles_dict[fav_icon_key] = !this.sharedService.fav_toggles_dict[fav_icon_key];
    localStorage.setItem("fav_toggles_dict", JSON.stringify(this.sharedService.fav_toggles_dict));
  }

  remove_event_click(event_index: number, fav_icon_key: string, event: MouseEvent) {
    alert("Removed from favorites!");

    let fav_table = this.sharedService.fav_storage_table;
    let local_storage = this.sharedService.window.localStorage;
    fav_table.splice(event_index, 1);
    local_storage.setItem("fav", JSON.stringify(fav_table));
    this.sharedService.fav_toggles_dict[fav_icon_key] = !this.sharedService.fav_toggles_dict[fav_icon_key];
    localStorage.setItem("fav_toggles_dict", JSON.stringify(this.sharedService.fav_toggles_dict));
  }
}
