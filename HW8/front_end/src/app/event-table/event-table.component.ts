import { Component, AfterViewInit, ViewChild, ElementRef , Renderer2} from "@angular/core";
import { SharedService } from "../shared.service";
import * as Constants from "../constants";

@Component({
  selector: "app-event-table",
  templateUrl: "./event-table.component.html",
  styleUrls: ["./event-table.component.css"],
})
export class EventTableComponent implements AfterViewInit{ 
  constructor(public sharedService: SharedService,private elementRef: ElementRef, private renderer: Renderer2) {}
  
 
  @ViewChild('tableWrapper') tableWrapper!: ElementRef;
  list_for_table: any[] = [];
  
  ngAfterViewInit() {
    this.sharedService.search_result.subscribe((resp) => {
      if (resp ) {        
        this.list_for_table = this.generate_table_ref(resp);       
      setTimeout(() => {
        this.tableWrapper.nativeElement.scrollIntoView({
          behavior: 'smooth',
          block: 'start',
        });
      }, 100);
      }
    });
  }

  generate_table_ref(resp: any): any[] {
    let list_for_table = new Array<any>();
    const total_events = resp["_embedded"]?.events?.length;

    for (let i = 0; i < total_events; i++) {
      const resp_obj = resp["_embedded"]["events"][i];
      const resp_item = Object.entries(resp["_embedded"]["events"][i]);
      let buffer_array = new Array<any>();

      if ("dates" in resp_obj) this.buffer_array_append(resp_item, buffer_array, "dates", resp_obj);
      else {
        buffer_array.push(Constants.EMPTY);
        buffer_array.push(Constants.EMPTY);
      }

      if ("images" in resp_obj) this.buffer_array_append(resp_item, buffer_array, "images", resp_obj);
      else buffer_array.push(Constants.EMPTY);

      if ("name" in resp_obj) this.buffer_array_append(resp_item, buffer_array, "name", resp_obj);
      else buffer_array.push(Constants.EMPTY);

      if ("classifications" in resp_obj) this.buffer_array_append(resp_item, buffer_array, "classifications", resp_obj);
      else buffer_array.push(Constants.EMPTY);

      if ("_embedded" in resp_obj) this.buffer_array_append(resp_item, buffer_array, "_embedded", resp_obj);
      else buffer_array.push(Constants.EMPTY);

      if ("id" in resp_obj) this.buffer_array_append(resp_item, buffer_array, "id", resp_obj);
      else buffer_array.push(Constants.EMPTY);

      list_for_table.push(buffer_array);
    }
    console.log(list_for_table);

    return list_for_table;
  }

  buffer_array_append(resp_item: any[], buffer_array: any[], header: string, resp_obj: any) {
    for (let i = 0; i < Object.keys(resp_item).length; i++) {
      if (resp_item[i][0] === header) {
        if (header === "dates") {
          const time_obj = resp_obj["dates"];
          const startLocalDate = time_obj?.start?.localDate?.trim();
          const startLocalTime = time_obj?.start?.localTime?.trim();
          if (startLocalDate !== Constants.UNDEFINED_LOW && startLocalDate !== Constants.UNDEFINED_CAP && startLocalDate !== undefined) {
            buffer_array.push(startLocalDate);
          } else buffer_array.push(Constants.EMPTY);

          if (startLocalTime !== Constants.UNDEFINED_LOW && startLocalTime !== Constants.UNDEFINED_CAP && startLocalTime !== undefined) buffer_array.push(startLocalTime);
          else buffer_array.push(Constants.EMPTY);
        } else if (header === "images") {
          const image_obj = resp_obj["images"];
          const image = image_obj?.[0]?.url?.trim();
          if (image_obj.length !== 0 && image !== Constants.UNDEFINED_LOW && image !== Constants.UNDEFINED_CAP && image !== undefined) {
            buffer_array.push(image);
          } else buffer_array.push(Constants.EMPTY);
        } else if (header === "name") {
          const name = resp_obj["name"].trim();
          if (name !== Constants.UNDEFINED_LOW && name !== Constants.UNDEFINED_CAP && name !== undefined) buffer_array.push(name);
          else buffer_array.push(Constants.EMPTY);
        } else if (header === "classifications") {
          const genre_obj = resp_obj["classifications"];
          const genre = genre_obj?.[0]?.segment?.name.trim();
          if (genre_obj.length !== 0 && genre !== Constants.UNDEFINED_LOW && genre !== Constants.UNDEFINED_CAP && genre !== undefined) buffer_array.push(genre);
          else buffer_array.push(Constants.EMPTY);
        } else if (header === "_embedded") {
          const venues_obj = resp_obj["_embedded"];
          const venue = venues_obj?.venues?.[0]?.name.trim();

          if (venues_obj.length !== 0 && venue !== Constants.UNDEFINED_LOW && venue !== Constants.UNDEFINED_CAP && venue !== undefined) buffer_array.push(venue);
          else buffer_array.push(Constants.EMPTY);
        } else if (header === "id") {
          const id = resp_obj["id"].trim();

          if (id !== Constants.UNDEFINED_LOW && id !== Constants.UNDEFINED_CAP && id !== undefined) buffer_array.push(id);
          else buffer_array.push(Constants.EMPTY);
        }
      }
    }
  }
}
