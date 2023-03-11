import { Injectable } from "@angular/core";
import * as Constants from "./constants";

@Injectable({
  providedIn: "root",
})
export class SharedService {
  constructor() {}

  public keyword_input: string = Constants.EMPTY;
  public distance_input: number = 10;
  public category_input: string = "Default";
  public location_input: string = Constants.EMPTY;
  public checkbox_input: boolean = false;
}
