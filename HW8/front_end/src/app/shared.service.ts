import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
import * as Constants from "./constants";

@Injectable({
  providedIn: "root",
})
export class SharedService {
  constructor() {}

  //public keyword_input: string = Constants.EMPTY;
  public keyword_input: string = "Taylor Swift"
  public distance_input: number = 10;
  public category_input: string = "Default";
  //public location_input: string = Constants.EMPTY;
  public location_input: string = "New York";
  public checkbox_input: boolean = false;

  public show_table: boolean = false;
 
  private search_result_subject = new BehaviorSubject<any>(null);
  search_result = this.search_result_subject.asObservable();

  set_search_result(result: any) {
    this.search_result_subject.next(result);
  }

}
