/* eslint-disable no-mixed-spaces-and-tabs */
/* eslint-disable no-unused-vars */

const GOOGLE_API_HOST = "https://maps.googleapis.com";
const GEOCODING_SEARCH_PATH = "/maps/api/geocode/json";
const GOOGLE_API_KEY = "AIzaSyDIGpZqNauMP2qqXnnanXzP6ba0AIIkT58";
const reg_non_alphanumeric = /[^a-z0-9+]+/gi;
const reg_remove_all_spaces_after_end_string = /\s*$/;
const DEFAULT_DISTANCE = "10";
const UNDEFINED_LOW = "undefined";
const UNDEFINED_CAP = "Undefined";
const EMPTY = "";
const ARTISTS_SEPARATOR = " | ";
const GENRE_SEPARATOR = " | ";
const PRICE_RANGES_OPERATOR = " - ";

const keyword = document.getElementById("keyword");
const distance = document.getElementById("distance");
const category = document.getElementById("category_bar");
const location_form = document.getElementById("locations");
const form_data = document.getElementById("form_data");
const submit_button = document.getElementById("submit");
const event_details =  document.getElementById("event_details_holder");

const item_table = document.getElementById("table");

/* Helper functions */
function sort_table(id) {
  console.log("sort placeholder");
}

function hovered() {
  this.style.color = "#6600ff";
}

function hovered_artists_url(){
  this.style.color = "#b380ff";
}
function not_hovered() {
  this.style.color = "black";
}

function not_hovered_artists_url() {
  this.style.color = "#00a0cc";
}

function preventDefault(event) {
  event.preventDefault();
}

function buffer_array_append(result_dict_item, buffer_array, header) {
  /* have to handle cases like get_request_event_detai*/
  for (let i = 0; i < Object.keys(result_dict_item).length; i++) {
    if (result_dict_item[i][0] == header) {
      if (header == "dates") {
        let time_obj = result_dict_item[i][1]["start"];
        if ("localDate" in time_obj) buffer_array.push(time_obj["localDate"]);
        else buffer_array.push(EMPTY);

        if ("localTime" in time_obj) buffer_array.push(time_obj["localTime"]);
        else buffer_array.push(EMPTY);
      } else if (header == "images") {
        let image_obj = result_dict_item[i][1];
        if (image_obj.length != 0) buffer_array.push(image_obj[0]["url"]);
        else buffer_array.push(EMPTY);
      } else if (header == "name") {
        let name_obj = result_dict_item[i][1];
        if (name_obj.length != 0) buffer_array.push(name_obj);
        else buffer_array.push(EMPTY);
      } else if (header == "classifications") {
        let genre_obj = result_dict_item[i][1];

        if ("name" in genre_obj[0]["segment"]) buffer_array.push(genre_obj[0]["segment"]["name"]);
        else buffer_array.push(EMPTY);
      } else if (header == "_embedded") {
        let venues_obj = result_dict_item[i][1]["venues"][0];

        if (venues_obj["name"].length != 0) buffer_array.push(venues_obj["name"]);
        else buffer_array.push(EMPTY);
      } else if (header == "id") {
        let id_obj = result_dict_item[i][1];
        if (id_obj.length != 0) buffer_array.push(id_obj);
        else buffer_array.push(EMPTY);
      }
    }
  }
}


function table_header_constructor(item_table) {
  item_table.innerHTML +=
    '<tr id ="first_row_height"><th id ="first_columns_width">Date</th> <th id ="second_columns_width">Icon</th> <th id ="third_columns_width" onClick="sort_table(this.id)" >Event</th> <th id ="fourth_columns_width" onClick="sort_table(this.id)">Genre</th> <th id ="fifth_columns_width" onClick="sort_table(this.id)">Venue</th>  </tr>';
  document.getElementById("third_columns_width").style.cursor = "pointer";
  document.getElementById("fourth_columns_width").style.cursor = "pointer";
  document.getElementById("fifth_columns_width").style.cursor = "pointer";
}

function table_append_row(item_table, list_for_table, i) {
  item_table.innerHTML +=
    '<tr class="rows_height"><td class="table_text">' +
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
    "</td> </tr>";
}

function clear_fields() {
  document.getElementById("keyword").value = EMPTY;
  document.getElementById("distance").value = EMPTY;
  document.getElementById("category_bar").value = "Default";
  document.getElementById("check_box").checked = false;

  if (location_form.style.display == "none") {
    location_form.style.display = "initial";
    location_form.required = true;
  } else {
    document.getElementById("locations").value = EMPTY;
  }

  document.getElementById("table").innerHTML = EMPTY;
  //document.getElementById("card_holder").innerHTML = "";
  //document.getElementById("no_record_container").innerHTML = "";

  list_for_table = [];
}

function check(event) {
  if (location_form.style.display == "none") {
    location_form.style.display = "initial";
    location_form.required = true;
  } else {
    location_form.value = EMPTY;
    location_form.required = false;
    location_form.style.display = "none";
  }
}

submit_button.addEventListener("click", submitForm, false);
function submitForm(event) {
  if (keyword.checkValidity() != false && location_form.checkValidity() != false && location_form.style.display != "none") {
    event.preventDefault();

    let form_location = location_form.value;
    let buffer = form_location.replace(reg_remove_all_spaces_after_end_string, EMPTY);
    let api_address_param = buffer.replace(reg_non_alphanumeric, "+");
    let url = GOOGLE_API_HOST + GEOCODING_SEARCH_PATH + "?address=" + api_address_param + "&key=" + GOOGLE_API_KEY;

    geoCode_send_request(url);
  } else {
    console.log("ohmygod");
  }
}

function geoCode_send_request(url) {
  let xhttp;
  xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
      console.log("bingo");
      let resp = this.responseText;
      let result_dict = JSON.parse(resp);

      if (result_dict["status"] != "ZERO_RESULTS") {
        get_yelp_result(result_dict["results"]["0"]["geometry"]["location"]["lat"], result_dict["results"]["0"]["geometry"]["location"]["lng"]);
      }
    }
  };
  xhttp.open("GET", url, true);
  xhttp.send();
}

function get_yelp_result(lat, lng) {
  let form_keyword = keyword.value;
  let form_category = category.value;
  let form_distance;

  if (distance.value == EMPTY) form_distance = DEFAULT_DISTANCE;
  else form_distance = distance.value;

  if (typeof lat == "number" && typeof lng == "number") {
    send_request("/" + form_keyword + "/" + lat.toString() + "/" + lng.toString() + "/" + form_category + "/" + form_distance);
  } else if (typeof lat == "string" && typeof lng == "string") {
    send_request("/" + form_keyword + "/" + lat + "/" + lng + "/" + form_category + "/" + form_distance);
  }
}

let list_for_table = new Array();

function send_request(url) {
  let xhttp;
  xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
      if (document.getElementById("table").innerHTML != EMPTY) {
        document.getElementById("table").innerHTML = EMPTY;
        list_for_table = [];
      }

      let resp = this.responseText;
      let result_dict = JSON.parse(resp);

      let total_events = Object.keys(result_dict["_embedded"]["events"]).length;

      for (let i = 0; i < total_events; i++) {
        let result_dict_item = Object.entries(result_dict["_embedded"]["events"][i]);
        let buffer_array = new Array();

        buffer_array_append(result_dict_item, buffer_array, "dates");
        buffer_array_append(result_dict_item, buffer_array, "images");
        buffer_array_append(result_dict_item, buffer_array, "name");
        buffer_array_append(result_dict_item, buffer_array, "classifications");
        buffer_array_append(result_dict_item, buffer_array, "_embedded");
        buffer_array_append(result_dict_item, buffer_array, "id");

        list_for_table.push(buffer_array);
      }

      if (list_for_table.length > 0) {
        table_header_constructor(item_table);
      } else {
        console.log("no record");
      }

      for (let i = 0; i < total_events; i++) {
        table_append_row(item_table, list_for_table, i);
      }

      let event_title_list = document.getElementsByClassName("event_title");

      for (let i = 0; i < event_title_list.length; i++) {
        event_title_list[i].style.color = "black";
        event_title_list[i].style.textDecoration = "none";
        event_title_list[i].addEventListener("mouseover", hovered, false);
        event_title_list[i].addEventListener("mouseout", not_hovered, false);
        event_title_list[i].addEventListener("click", preventDefault, false);
      }

     
      //console.log(list_for_table)
    }
  };
  xhttp.open("GET", url, true);
  xhttp.send();
}

function get_request_event_detail(id) {
  let request_url = "/event-detail/" + id;
  var xhttp;
  xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
      let resp = this.responseText;
      let result_dict = JSON.parse(resp);
      console.log(result_dict);

      let event_title, local_date, local_time, venue, price_range, status, ticket_url, seatmap_url;
      let artist_or_team = [];
      let genre = EMPTY;
      let time_obj = result_dict["dates"]["start"];

      if ("name" in result_dict && result_dict["name"].trim() != UNDEFINED_LOW && result_dict["name"].trim() != UNDEFINED_CAP) event_title = result_dict["name"].trim();
      else event_title = EMPTY;

      if (
        "dates" in result_dict &&
        "start" in result_dict["dates"] &&
        "localDate" in result_dict["dates"]["start"] &&
        result_dict["dates"]["start"]["localDate"].trim() != UNDEFINED_LOW &&
        result_dict["dates"]["start"]["localDate"].trim() != UNDEFINED_CAP
      )
        local_date = result_dict["dates"]["start"]["localDate"].trim();
      else local_date = EMPTY;

      if (
        "dates" in result_dict &&
        "start" in result_dict["dates"] &&
        "localTime" in result_dict["dates"]["start"] &&
        result_dict["dates"]["start"]["localTime"].trim() != UNDEFINED_LOW &&
        result_dict["dates"]["start"]["localTime"].trim() != UNDEFINED_CAP
      )
        local_time = result_dict["dates"]["start"]["localTime"].trim();
      else local_time = EMPTY;

      if ("_embedded" in result_dict && "attractions" in result_dict["_embedded"] && result_dict["_embedded"]["attractions"].length != 0) {
          let artists_obj = result_dict["_embedded"]["attractions"];
          
          for (let i = 0; i < artists_obj.length; i++) {
              let temp = [];

              if ("name" in artists_obj[i] && artists_obj[i]["name"].trim() != UNDEFINED_LOW && artists_obj[i]["name"].trim() != UNDEFINED_CAP)temp.push(artists_obj[i]["name"].trim())
              else temp.push(EMPTY);

              if("url" in artists_obj[i] && artists_obj[i]["url"].trim() != UNDEFINED_LOW && artists_obj[i]["url"].trim() != UNDEFINED_CAP ) temp.push(artists_obj[i]["url"].trim())
              else temp.push(EMPTY);

              artist_or_team.push(temp);
          }
         


      } 

      if (
        "_embedded" in result_dict &&
        "venues" in result_dict["_embedded"] &&
        "name" in result_dict["_embedded"]["venues"][0] &&
        result_dict["_embedded"]["venues"][0]["name"].trim() != UNDEFINED_LOW &&
        result_dict["_embedded"]["venues"][0]["name"].trim() != UNDEFINED_CAP
      )
        venue = result_dict["_embedded"]["venues"][0]["name"].trim();
      else venue = EMPTY;

      if ("classifications" in result_dict) {
        let class_obj = result_dict["classifications"][0];

        if ("segment" in class_obj && "name" in class_obj["segment"] && class_obj["segment"]["name"].trim() != UNDEFINED_LOW && class_obj["segment"]["name"].trim() != UNDEFINED_CAP)
          genre += class_obj["segment"]["name"].trim() + GENRE_SEPARATOR;

        if ("genre" in class_obj && "name" in class_obj["genre"] && class_obj["genre"]["name"].trim() != UNDEFINED_LOW && class_obj["genre"]["name"].trim() != UNDEFINED_CAP)
          genre += class_obj["genre"]["name"].trim() + GENRE_SEPARATOR;

        if ("subGenre" in class_obj && "name" in class_obj["subGenre"] && class_obj["subGenre"]["name"].trim() != UNDEFINED_LOW && class_obj["subGenre"]["name"].trim() != UNDEFINED_CAP)
          genre += class_obj["subGenre"]["name"].trim() + GENRE_SEPARATOR;

        if ("type" in class_obj && "name" in class_obj["type"] && class_obj["type"]["name"].trim() != UNDEFINED_LOW && class_obj["type"]["name"].trim() != UNDEFINED_CAP)
          genre += class_obj["type"]["name"].trim() + GENRE_SEPARATOR;

        if ("subType" in class_obj && "name" in class_obj["subType"] && class_obj["subType"]["name"].trim() != UNDEFINED_LOW && class_obj["subType"]["name"].trim() != UNDEFINED_CAP)
          genre += class_obj["subType"]["name"].trim() + GENRE_SEPARATOR;

        if (genre.length != 0) genre = genre.slice(0, -2).trim();
      }

      if ("priceRanges" in result_dict && ("max" in result_dict["priceRanges"][0] || "min" in result_dict["priceRanges"][0])) {
        let price_range_obj = result_dict["priceRanges"][0];
        let min = -1,
          max = -1;
        let currency = EMPTY;

        if ("max" in price_range_obj) max = price_range_obj["max"];
        if ("min" in price_range_obj) min = price_range_obj["min"];
        if ("currency" in price_range_obj && price_range_obj["currency"].trim() != UNDEFINED_LOW && price_range_obj["currency"].trim() != UNDEFINED_CAP) currency = price_range_obj["currency"].trim();

        if (min == -1) price_range = max + PRICE_RANGES_OPERATOR + max + " " + currency;
        else if (max == -1) price_range = min + PRICE_RANGES_OPERATOR + min + " " + currency;
        else price_range = min + PRICE_RANGES_OPERATOR + max + " " + currency;
      } else price_range = EMPTY;

      if (
        "dates" in result_dict &&
        "status" in result_dict["dates"] &&
        "code" in result_dict["dates"]["status"] &&
        result_dict["dates"]["status"]["code"].trim() != UNDEFINED_LOW &&
        result_dict["dates"]["status"]["code"].trim() != UNDEFINED_CAP
      )
        status = result_dict["dates"]["status"]["code"].trim();
      else status = EMPTY;

      if ("url" in result_dict && result_dict["url"].trim() != UNDEFINED_LOW && result_dict["url"].trim() != UNDEFINED_CAP) ticket_url = result_dict["url"].trim();
      else ticket_url = EMPTY;

      if (
        "seatmap" in result_dict &&
        "staticUrl" in result_dict["seatmap"] &&
        result_dict["seatmap"]["staticUrl"].trim() != UNDEFINED_LOW &&
        result_dict["seatmap"]["staticUrl"].trim() != UNDEFINED_CAP
      )
        seatmap_url = result_dict["seatmap"]["staticUrl"].trim();
      else seatmap_url = EMPTY;

      console.log(artist_or_team)
      generate_event_details_card(event_title,local_date,local_time,artist_or_team,venue,genre,price_range,status,ticket_url,seatmap_url);
    }
  };
  xhttp.open("GET", request_url, true);
  xhttp.send();
}

function generate_event_details_card(event_title,local_date,local_time,artist_or_team,venue,genre,price_range,status,ticket_url,seatmap_url){
    //card.innerHTML += "<div id= \"card\">";
   // card.innerHTML += "<p id= \"event_title\" class= \"card_font\">" + event_title + "</p>";
    
    event_details.insertAdjacentHTML("beforeend","<div id= \"card\">");
    const card =  document.getElementById("card");
    if(event_title !=EMPTY) card.insertAdjacentHTML("beforeend","<p id= \"event_title\" class= \"card_font\">" + event_title + "</p>");
    if(seatmap_url != EMPTY) card.insertAdjacentHTML("beforeend","<img id= \"seatmap\" src= " + seatmap_url + " alt=\"seatmap\" >" + "</img>");

    card.insertAdjacentHTML("beforeend","<div id= \"event_info\">");
    const event_info =  document.getElementById("event_info");
    if(local_date != EMPTY || local_time !=EMPTY) {
        if(local_time ==EMPTY) { 
            event_info.insertAdjacentHTML("beforeend", "<p class= \"detail_title\">Date</p>"); 
            event_info.insertAdjacentHTML("beforeend", "<p class= \"detail_text\">" + local_date + "</p>"); 
        }
        else if (local_date ==EMPTY) {
            event_info.insertAdjacentHTML("beforeend", "<p class= \"detail_title\">Date</p>"); 
            event_info.insertAdjacentHTML("beforeend", "<p class= \"detail_text\">" + local_time + "</p>"); 
        }
        else{
            event_info.insertAdjacentHTML("beforeend", "<p class= \"detail_title\">Date</p>"); 
            event_info.insertAdjacentHTML("beforeend", "<p class= \"detail_text\">" + local_date + " " + local_time + "</p>"); 
        }
    }

    if(artist_or_team.length != 0){
        event_info.insertAdjacentHTML("beforeend", "<p class= \"detail_title\">Artist/Team</p>");   

        let artists_info = "<p class= \"detail_text\">"
        for(let i= 0; i < artist_or_team.length; i++){
            if(artist_or_team[i][0] != EMPTY){
                if(artist_or_team[i][1] != EMPTY){
                    if(i != artist_or_team.length - 1) artists_info +=  "<a href=" + artist_or_team[i][1] + " class= \"artists_url\" target=\"_blank\"" + ">" +  artist_or_team[i][0]  + "</a>" + ARTISTS_SEPARATOR;
                    else artists_info +=  "<a href=" + artist_or_team[i][1] + " class= \"artists_url\"  target=\"_blank\"" + ">" +  artist_or_team[i][0]  + "</a>";
                }else{
                    if(i != artist_or_team.length - 1) artists_info +=  artist_or_team[i][0]  + ARTISTS_SEPARATOR;
                    else artists_info += artist_or_team[i][0]  + "</a>";
                }
            }    
         
        }
        artists_info += "</p>"
        event_info.insertAdjacentHTML("beforeend", artists_info ); 
    }

    var temp = document.getElementsByClassName("artists_url");
	  
	  for(let i = 0; i < temp.length; i ++){
		 temp[i].style.color = "#00a0cc";
		 temp[i].style.textDecoration = "none";
		 temp[i].addEventListener('mouseover', hovered_artists_url, false);
		 temp[i].addEventListener('mouseout', not_hovered_artists_url, false);
	  }



    card.insertAdjacentHTML("beforeend","</div>");
    card.insertAdjacentHTML("beforeend","</div>");

  
    card.scrollIntoView({behavior: "smooth"});

}