import { NgModule } from "@angular/core";
import { BrowserModule } from "@angular/platform-browser";
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { HttpClientModule } from "@angular/common/http";

import { AppComponent } from "./app.component";
import { SearchFormComponent } from "./search-form/search-form.component";
import { AppRoutingModule } from "./app-routing.module";
import { FavoritesComponent } from "./favorites/favorites.component";
import { NavBarComponent } from "./nav-bar/nav-bar.component";
import { BrowserAnimationsModule } from "@angular/platform-browser/animations";

import { EventTableComponent } from "./event-table/event-table.component";
// Material Modules
import { MatAutocompleteModule } from "@angular/material/autocomplete";
import { MatInputModule } from "@angular/material/input";
import { MatIconModule } from "@angular/material/icon";
import { MatButtonModule } from "@angular/material/button";

@NgModule({
  declarations: [AppComponent, SearchFormComponent, FavoritesComponent, NavBarComponent,
    EventTableComponent],
  imports: [BrowserModule, FormsModule, HttpClientModule, AppRoutingModule, BrowserAnimationsModule, ReactiveFormsModule, MatInputModule, MatIconModule, 
    MatButtonModule, MatAutocompleteModule],
  providers: [],
  bootstrap: [AppComponent],
})
export class AppModule {}
