import { Component, AfterViewInit, ElementRef, ViewChild } from "@angular/core";
import { HttpRequestService } from "../http-request.service";

@Component({
  selector: "app-event-details",
  templateUrl: "./event-details.component.html",
  styleUrls: ["./event-details.component.css"],
})
export class EventDetailsComponent implements AfterViewInit {
  constructor(public http_request: HttpRequestService) {}

  @ViewChild("eventDetails") eventDetails!: ElementRef;

  ngAfterViewInit() {
    this.subs_event_details();
    this.scroll_to_eventDetails();
  }

  subs_event_details() {
    this.http_request.get_autocomplete_suggestions().subscribe(
      (result) => {
        console.log("Ni Hao:", result);
        // Process the result here
      },
      (error) => {
        console.error("Error occurred:", error);
      }
    );
  }

  scroll_to_eventDetails() {
     setTimeout(() => {
      this.eventDetails.nativeElement.scrollIntoView({
        behavior: "auto",
        block: "start",
      });
    }, 100);
  }
}
