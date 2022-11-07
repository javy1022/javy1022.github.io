import { Component } from '@angular/core';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'HW8';
  
  selectedNav = "search";
  
  onSelect(): void {
    this.selectedNav = "search";
	
  }
  
  onSelect1(): void {
    this.selectedNav = "bookings";
	
  }
}
