import { Component, AfterViewInit, ViewChild, ElementRef } from "@angular/core";
import { SharedService } from "../shared.service";
import * as Constants from "../constants";


@Component({
  selector: "app-event-table",
  templateUrl: "./event-table.component.html",
  styleUrls: ["./event-table.component.css"],
})
export class EventTableComponent implements AfterViewInit {
  constructor(public sharedService: SharedService) {}
  @ViewChild('table') table!: ElementRef;

  ngAfterViewInit() {
    let list_for_table = new Array<any>();

    this.sharedService.search_result.subscribe((resp) => {
      if (resp) {
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
        this.table_header_constructor(this.table)
          
        for (let i = 0; i < total_events; i++) {
          this.table_append_row(this.table, list_for_table, i);
        }
      }
    });
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
          if (image_obj.length !== 0 && image !== Constants.UNDEFINED_LOW && image !== Constants.UNDEFINED_CAP && image !== undefined)  {
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

  table_header_constructor(table:any) {
    table.nativeElement.insertAdjacentHTML(
      "beforeend",
      '<tr id ="first_row_height"><th id ="first_columns_width">Date</th> <th id ="second_columns_width">Icon</th> <th id ="third_columns_width" onClick="sort_table(this.id,toggle_sorting_event)" >Event</th> <th id ="fourth_columns_width" onClick="sort_table(this.id,toggle_sorting_genre)">Genre</th> <th id ="fifth_columns_width" onClick="sort_table(this.id,toggle_sorting_venue)">Venue</th>  </tr>'
    );

  }

  table_append_row(table:ElementRef, list_for_table:any[], i:any) {
    let test;
    if (i == 0) test = '<tr class="rows_height" id= "test"><td class="table_text">';
    else test = '<tr class="rows_height"><td class="table_text">';
    table.nativeElement.insertAdjacentHTML(
      "beforeend",
      test +
        list_for_table[i][0] +
        "<br>" +
        list_for_table[i][1] +
        "</td><td><img src=" +
        list_for_table[i][2] +
        ' class="yelp_image"></img></td> <td class="table_text"> <a href = "#" class="event_title"  onclick=\'get_request_event_detail("' +
        list_for_table[i][6] +
        "\");' >" +
        list_for_table[i][3] +
        '</a></td> <td class="table_text">' +
        list_for_table[i][4] +
        '</td> <td class="table_text">' +
        list_for_table[i][5] +
        "</td> </tr>"
    );
  }
}
