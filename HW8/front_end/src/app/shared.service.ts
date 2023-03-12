import { Injectable } from "@angular/core";
import { BehaviorSubject } from "rxjs";
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

  private search_result_subject = new BehaviorSubject<any>(null);
  search_result = this.search_result_subject.asObservable();

  setData(result: any) {
    this.search_result_subject.next(result);
  }

}
