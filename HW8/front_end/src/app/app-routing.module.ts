import { NgModule } from "@angular/core";
import { CommonModule } from "@angular/common";
import { Routes, RouterModule } from "@angular/router";
import { SearchFormComponent } from "./search-form/search-form.component";
import { FavoritesComponent } from "./favorites/favorites.component";

const routes: Routes = [
  { path: "search", component: SearchFormComponent },
  { path: "favorites", component: FavoritesComponent },
  { path: "", redirectTo: "/search", pathMatch: "full" },
];

@NgModule({
  declarations: [],
  imports: [CommonModule, RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
