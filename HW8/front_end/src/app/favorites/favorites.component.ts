import { Component, ViewChild, ElementRef , OnInit} from '@angular/core';

@Component({
  selector: 'app-favorites',
  templateUrl: './favorites.component.html',
  styleUrls: ['./favorites.component.css']
})
export class FavoritesComponent implements OnInit {
   window: Window = window;

  ngOnInit() {
    this.window.localStorage.clear();

    
    alert(this.window.localStorage.length)
  }
}
