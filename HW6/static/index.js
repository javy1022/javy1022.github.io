/* eslint-disable no-mixed-spaces-and-tabs */
/* eslint-disable no-unused-vars */

const GOOGLE_API_HOST = "https://maps.googleapis.com";
const GEOCODING_SEARCH_PATH = "/maps/api/geocode/json";
const GOOGLE_API_KEY = "AIzaSyD4LQR4r_MWnIrZ0FDMipKXA80t_YvajGQ";
const IPINFO_API_HOST = "https://ipinfo.io/";
const IPINFO_API_KEY = "69aeb460f27a79";
const GOOGLE_MAP_SEARCH_PATH = "https://www.google.com/maps/search/?api=1&query=";

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
const check_box = document.getElementById("check_box");
const submit_button = document.getElementById("submit");
const event_details = document.getElementById("event_details_holder");
const item_table = document.getElementById("table");
const venue_details = document.getElementById("venue_details_holder");
const venue_card_holder = document.getElementById("venue_card_holder");
const no_record_container = document.getElementById("no_record_container");

/* Helper functions */
function reset() {
  if (item_table.innerHTML != EMPTY) item_table.innerHTML = EMPTY;
  if (list_for_table.length != 0) list_for_table = [];
  if (event_details.innerHTML != EMPTY) event_details.innerHTML = EMPTY;
  if (venue_details.innerHTML != EMPTY) venue_details.innerHTML = EMPTY;
  if (venue_card_holder.innerHTML != EMPTY) venue_card_holder.innerHTML = EMPTY;
}

function hovered() {
  this.style.color = "#6600ff";
}

function hovered_artists_url() {
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

function custom_urls_css(class_name) {
  let temp = document.getElementsByClassName(class_name);
  for (let i = 0; i < temp.length; i++) {
    if (class_name == "artists_url" || class_name == "ticket_url") {
      temp[i].style.color = "#00a0cc";
      temp[i].style.textDecoration = "none";
      temp[i].addEventListener("mouseover", hovered_artists_url, false);
      temp[i].addEventListener("mouseout", not_hovered_artists_url, false);
    } else if (class_name == "event_title") {
      temp[i].style.color = "black";
      temp[i].style.textDecoration = "none";
      temp[i].addEventListener("mouseover", hovered, false);
      temp[i].addEventListener("mouseout", not_hovered, false);
      temp[i].addEventListener("click", preventDefault, false);
    }
  }
}

function buffer_array_append(result_dict_item, buffer_array, header, result_dict_obj) {
  for (let i = 0; i < Object.keys(result_dict_item).length; i++) {
    if (result_dict_item[i][0] == header) {
      if (header == "dates") {
        let time_obj = result_dict_obj["dates"];
        if ("start" in time_obj && "localDate" in time_obj["start"] && time_obj["start"]["localDate"].trim() != UNDEFINED_LOW && time_obj["start"]["localDate"].trim() != UNDEFINED_CAP)
          buffer_array.push(time_obj["start"]["localDate"].trim());
        else buffer_array.push(EMPTY);

        if ("start" in time_obj && "localTime" in time_obj["start"] && time_obj["start"]["localTime"].trim() != UNDEFINED_LOW && time_obj["start"]["localTime"].trim() != UNDEFINED_CAP)
          buffer_array.push(time_obj["start"]["localTime"].trim());
        else buffer_array.push(EMPTY);
      } else if (header == "images") {
        let image_obj = result_dict_obj["images"];
        if (image_obj.length != 0 && image_obj["0"]["url"].trim() != UNDEFINED_LOW && image_obj["0"]["url"].trim() != UNDEFINED_CAP) buffer_array.push(image_obj["0"]["url"].trim());
        else buffer_array.push(EMPTY);
      } else if (header == "name") {
        let name_obj = result_dict_obj["name"];
        if (name_obj.trim() != UNDEFINED_LOW && name_obj.trim() != UNDEFINED_CAP) buffer_array.push(name_obj.trim());
        else buffer_array.push(EMPTY);
      } else if (header == "classifications") {
        let genre_obj = result_dict_obj["classifications"];
        if (
          genre_obj.length != 0 &&
          "segment" in genre_obj["0"] &&
          "name" in genre_obj["0"]["segment"] &&
          genre_obj["0"]["segment"]["name"].trim() != UNDEFINED_LOW &&
          genre_obj["0"]["segment"]["name"].trim() != UNDEFINED_CAP
        )
          buffer_array.push(genre_obj[0]["segment"]["name"].trim());
        else buffer_array.push(EMPTY);
      } else if (header == "_embedded") {
        let venues_obj = result_dict_obj["_embedded"];

        if (
          "venues" in venues_obj &&
          venues_obj["venues"].length != 0 &&
          "name" in venues_obj["venues"]["0"] &&
          venues_obj["venues"]["0"]["name"].trim() != UNDEFINED_LOW &&
          venues_obj["venues"]["0"]["name"].trim() != UNDEFINED_CAP
        )
          buffer_array.push(venues_obj["venues"]["0"]["name"].trim());
        else buffer_array.push(EMPTY);
      } else if (header == "id") {
        let id_obj = result_dict_obj["id"];
        if (id_obj.trim() != UNDEFINED_LOW && id_obj.trim() != UNDEFINED_CAP) buffer_array.push(id_obj.trim());
        else buffer_array.push(EMPTY);
      }
    }
  }
}

function table_header_constructor(item_table) {
  item_table.insertAdjacentHTML(
    "beforeend",
    '<tr id ="first_row_height"><th id ="first_columns_width">Date</th> <th id ="second_columns_width">Icon</th> <th id ="third_columns_width" onClick="sort_table(this.id,toggle_sorting_event)" >Event</th> <th id ="fourth_columns_width" onClick="sort_table(this.id,toggle_sorting_genre)">Genre</th> <th id ="fifth_columns_width" onClick="sort_table(this.id,toggle_sorting_venue)">Venue</th>  </tr>'
  );

  document.getElementById("third_columns_width").style.cursor = "pointer";
  document.getElementById("fourth_columns_width").style.cursor = "pointer";
  document.getElementById("fifth_columns_width").style.cursor = "pointer";
}

function table_append_row(item_table, list_for_table, i) {
  let test;
  if (i == 0) test = '<tr class="rows_height" id= "test"><td class="table_text">';
  else test = '<tr class="rows_height"><td class="table_text">';
  item_table.insertAdjacentHTML(
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

function clear_fields() {
  if (keyword.value != EMPTY) keyword.value = EMPTY;
  if (distance.value != EMPTY) distance.value = EMPTY;
  if (category.value != "Default") category.value = "Default";
  if (check_box.checked == true) check_box.checked = false;

  if (location_form.style.display == "none") {
    location_form.style.display = "initial";
    location_form.required = true;
  } else {
    location_form.value = EMPTY;
  }
  if (item_table.innerHTML != EMPTY) item_table.innerHTML = EMPTY;
  if (event_details.innerHTML != EMPTY) event_details.innerHTML = EMPTY;
  if (list_for_table.length != 0) list_for_table = [];
  if (venue_details.innerHTML != EMPTY) venue_details.innerHTML = EMPTY;
  if (venue_card_holder.innerHTML != EMPTY) venue_card_holder.innerHTML = EMPTY;
  if (no_record_container.innerHTML != EMPTY) no_record_container.innerHTML = EMPTY;
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

function no_record_constructor() {
  if (no_record_container.innerHTML != EMPTY) no_record_container.innerHTML = EMPTY;

  no_record_container.insertAdjacentHTML("beforeend", '<div id= "no_record_div">');
  const no_record = document.getElementById("no_record_div");
  no_record.insertAdjacentHTML("beforeend", '<p id= "no_record">No Records found</p></div>');

  no_record_container.insertAdjacentHTML("beforeend", "</div>");
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
  } else if (keyword.checkValidity() != false && location_form.style.display == "none" && check_box.checked == true) {
    event.preventDefault();
    ipInfo_send_request(IPINFO_API_HOST + "?token=" + IPINFO_API_KEY);
  }
}

function geoCode_send_request(url) {
  let xhttp;
  xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
      let resp = this.responseText;
      let result_dict = JSON.parse(resp);

      if (result_dict["status"] != "ZERO_RESULTS") {
        get_ticketmaster_result(result_dict["results"]["0"]["geometry"]["location"]["lat"], result_dict["results"]["0"]["geometry"]["location"]["lng"]);
      } else {
        reset();
        no_record_constructor();
      }
    }
  };
  xhttp.open("GET", url, true);
  xhttp.send();
}

function ipInfo_send_request(url) {
  var xhttp;
  xhttp = new XMLHttpRequest();
  xhttp.onreadystatechange = function () {
    if (this.readyState == 4 && this.status == 200) {
      var resp = this.responseText;
      let result_dict = JSON.parse(resp);

      var buffer = result_dict["loc"];
      var lat_lng_array = buffer.split(",");

      get_ticketmaster_result(lat_lng_array[0], lat_lng_array[1]);
    }
  };
  xhttp.open("GET", url, true);
  xhttp.send();
}

function get_ticketmaster_result(lat, lng) {
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
      reset();
      if (no_record_container.innerHTML != EMPTY) no_record_container.innerHTML = EMPTY;

      let resp = this.responseText;
      let result_dict = JSON.parse(resp);

      if ("_embedded" in result_dict && "events" in result_dict["_embedded"] && result_dict["_embedded"]["events"].length != 0) {
        if (no_record_container.innerHTML != EMPTY) no_record_container.innerHTML = EMPTY;

        let total_events = Object.keys(result_dict["_embedded"]["events"]).length;

        for (let i = 0; i < total_events; i++) {
          if (result_dict["_embedded"]["events"][i].length != 0) {
            let result_dict_obj = result_dict["_embedded"]["events"][i];
            let result_dict_item = Object.entries(result_dict["_embedded"]["events"][i]);
            let buffer_array = new Array();

            if ("dates" in result_dict_obj) buffer_array_append(result_dict_item, buffer_array, "dates", result_dict_obj);
            else {
              buffer_array.push(EMPTY);
              buffer_array.push(EMPTY);
            }

            if ("images" in result_dict_obj) buffer_array_append(result_dict_item, buffer_array, "images", result_dict_obj);
            else buffer_array.push(EMPTY);

            if ("name" in result_dict_obj) buffer_array_append(result_dict_item, buffer_array, "name", result_dict_obj);
            else buffer_array.push(EMPTY);

            if ("classifications" in result_dict_obj) buffer_array_append(result_dict_item, buffer_array, "classifications", result_dict_obj);
            else buffer_array.push(EMPTY);

            if ("_embedded" in result_dict_obj) buffer_array_append(result_dict_item, buffer_array, "_embedded", result_dict_obj);
            else buffer_array.push(EMPTY);

            if ("id" in result_dict_obj) buffer_array_append(result_dict_item, buffer_array, "id", result_dict_obj);
            else buffer_array.push(EMPTY);

            list_for_table.push(buffer_array);
          }
        }

        table_header_constructor(item_table);

        for (let i = 0; i < total_events; i++) {
          table_append_row(item_table, list_for_table, i);
        }

        custom_urls_css("event_title");
      } else {
        reset();
        no_record_constructor();
      }
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

      if (event_details.innerHTML != EMPTY) event_details.innerHTML = EMPTY;
      if (venue_details.innerHTML != EMPTY) venue_details.innerHTML = EMPTY;
      if (venue_card_holder.innerHTML != EMPTY) venue_card_holder.innerHTML = EMPTY;

      let event_title, local_date, local_time, venue, price_range, status, ticket_url, seatmap_url;
      let artist_or_team = [];
      let genre = EMPTY;

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

          if ("name" in artists_obj[i] && artists_obj[i]["name"].trim() != UNDEFINED_LOW && artists_obj[i]["name"].trim() != UNDEFINED_CAP) temp.push(artists_obj[i]["name"].trim());
          else temp.push(EMPTY);

          if ("url" in artists_obj[i] && artists_obj[i]["url"].trim() != UNDEFINED_LOW && artists_obj[i]["url"].trim() != UNDEFINED_CAP) temp.push(artists_obj[i]["url"].trim());
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

      generate_event_details_card(event_title, local_date, local_time, artist_or_team, venue, genre, price_range, status, ticket_url, seatmap_url);
    }
  };
  xhttp.open("GET", request_url, true);
  xhttp.send();
}

function generate_event_details_card(event_title, local_date, local_time, artist_or_team, venue, genre, price_range, status, ticket_url, seatmap_url) {
  event_details.insertAdjacentHTML("beforeend", '<div id= "card">');
  const card = document.getElementById("card");
  if (event_title != EMPTY) card.insertAdjacentHTML("beforeend", '<p id= "event_title" class= "card_font">' + event_title + "</p>");
  if (seatmap_url != EMPTY) card.insertAdjacentHTML("beforeend", '<img id= "seatmap" src= ' + seatmap_url + ' alt="seatmap" >' + "</img>");

  card.insertAdjacentHTML("beforeend", '<div id= "event_info">');
  const event_info = document.getElementById("event_info");
  if (local_date != EMPTY || local_time != EMPTY) {
    if (local_time == EMPTY) {
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_title">Date</p>');
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text">' + local_date + "</p>");
    } else if (local_date == EMPTY) {
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_title">Date</p>');
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text">' + local_time + "</p>");
    } else {
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_title">Date</p>');
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text">' + local_date + " " + local_time + "</p>");
    }
  }

  if (artist_or_team.length != 0) {
    event_info.insertAdjacentHTML("beforeend", '<p class= "detail_title">Artist/Team</p>');

    let artists_info = '<p class= "detail_text">';
    for (let i = 0; i < artist_or_team.length; i++) {
      if (artist_or_team[i][0] != EMPTY) {
        if (artist_or_team[i][1] != EMPTY) {
          if (i != artist_or_team.length - 1) artists_info += "<a href=" + artist_or_team[i][1] + ' class= "artists_url" target="_blank"' + ">" + artist_or_team[i][0] + "</a>" + ARTISTS_SEPARATOR;
          else artists_info += "<a href=" + artist_or_team[i][1] + ' class= "artists_url"  target="_blank"' + ">" + artist_or_team[i][0] + "</a>";
        } else {
          if (i != artist_or_team.length - 1) artists_info += artist_or_team[i][0] + ARTISTS_SEPARATOR;
          else artists_info += artist_or_team[i][0] + "</a>";
        }
      }
    }
    artists_info += "</p>";
    event_info.insertAdjacentHTML("beforeend", artists_info);
    custom_urls_css("artists_url");
  }

  if (venue != EMPTY) {
    event_info.insertAdjacentHTML("beforeend", '<p class= "detail_title">Venue</p>');
    event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text">' + venue + "</p>");
  }

  if (genre != EMPTY) {
    event_info.insertAdjacentHTML("beforeend", '<p class= "detail_title">Genres</p>');
    event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text">' + genre + "</p>");
  }

  if (price_range != EMPTY) {
    event_info.insertAdjacentHTML("beforeend", '<p class= "detail_title">Price Ranges</p>');
    event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text">' + price_range + "</p>");
  }

  if (status != EMPTY) {
    event_info.insertAdjacentHTML("beforeend", '<p class= "detail_title" >Ticket Status</p>');

    if (status == "onsale") {
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text" id="onsale">On Sale</p>');
    } else if (status == "offsale") {
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text" id="offsale">Off Sale</p>');
    } else if (status == "cancelled" || status == "canceled") {
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text" id="canceled">Canceled</p>');
    } else if (status == "postponed") {
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text" id="postponed">Postponed</p>');
    } else if (status == "rescheduled") {
      event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text" id="rescheduled">Rescheduled</p>');
    }
  }

  if (ticket_url != EMPTY) {
    event_info.insertAdjacentHTML("beforeend", '<p class= "detail_title" >Buy Ticket At:</p>');
    event_info.insertAdjacentHTML("beforeend", '<p class= "detail_text"><a href=' + ticket_url + ' class= "ticket_url"  target="_blank"' + ">" + "Ticketmaster" + "</a></p>");
    custom_urls_css("ticket_url");
  }
  card.insertAdjacentHTML("beforeend", "</div>");

  card.insertAdjacentHTML("beforeend", "</div>");
  card.scrollIntoView({ behavior: "smooth" });

  venue_details.insertAdjacentHTML("beforeend", '<div id= "venue_details">');
  const venue_content = document.getElementById("venue_details");

  venue_content.insertAdjacentHTML("beforeend", '<p id= "show_venue">Show Venue Details</p>');

  venue_content.insertAdjacentHTML("beforeend", '<div id= "arrow"></div>');
  document.getElementById("arrow").style.cursor = "pointer";
  document.getElementById("arrow").addEventListener("click", function () {
    get_request_venue_details(venue);
  });

  venue_details.insertAdjacentHTML("beforeend", "</div>");
}

function get_request_venue_details(venue) {
  if (venue != EMPTY) {
    let request_url = "/venue-detail/" + venue;
    var xhttp;
    xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function () {
      if (this.readyState == 4 && this.status == 200) {
        let venue_name, address, city_temp, state_code, city, postalCode, upcoming_url, image;
        var resp = this.responseText;
        let result_dict = JSON.parse(resp);

        if (
          "_embedded" in result_dict &&
          "venues" in result_dict["_embedded"] &&
          result_dict["_embedded"]["venues"].length != 0 &&
          "name" in result_dict["_embedded"]["venues"]["0"] &&
          result_dict["_embedded"]["venues"]["0"]["name"].trim() != UNDEFINED_LOW &&
          result_dict["_embedded"]["venues"]["0"]["name"].trim() != UNDEFINED_CAP
        )
          venue_name = result_dict["_embedded"]["venues"]["0"]["name"].trim();
        else venue_name = EMPTY;

        if (
          "_embedded" in result_dict &&
          "venues" in result_dict["_embedded"] &&
          result_dict["_embedded"]["venues"].length != 0 &&
          "address" in result_dict["_embedded"]["venues"]["0"] &&
          "line1" in result_dict["_embedded"]["venues"]["0"]["address"] &&
          result_dict["_embedded"]["venues"]["0"]["address"]["line1"].trim() != UNDEFINED_LOW &&
          result_dict["_embedded"]["venues"]["0"]["address"]["line1"].trim() != UNDEFINED_CAP
        )
          address = result_dict["_embedded"]["venues"]["0"]["address"]["line1"].trim();
        else address = EMPTY;

        if (
          "_embedded" in result_dict &&
          "venues" in result_dict["_embedded"] &&
          result_dict["_embedded"]["venues"].length != 0 &&
          "city" in result_dict["_embedded"]["venues"]["0"] &&
          "name" in result_dict["_embedded"]["venues"]["0"]["city"] &&
          result_dict["_embedded"]["venues"]["0"]["city"]["name"].trim() != UNDEFINED_LOW &&
          result_dict["_embedded"]["venues"]["0"]["city"]["name"].trim() != UNDEFINED_CAP
        )
          city_temp = result_dict["_embedded"]["venues"]["0"]["city"]["name"].trim();
        else city_temp = EMPTY;

        if (
          "_embedded" in result_dict &&
          "venues" in result_dict["_embedded"] &&
          result_dict["_embedded"]["venues"].length != 0 &&
          "state" in result_dict["_embedded"]["venues"]["0"] &&
          "stateCode" in result_dict["_embedded"]["venues"]["0"]["state"] &&
          result_dict["_embedded"]["venues"]["0"]["state"]["stateCode"].trim() != UNDEFINED_LOW &&
          result_dict["_embedded"]["venues"]["0"]["state"]["stateCode"].trim() != UNDEFINED_CAP
        )
          state_code = result_dict["_embedded"]["venues"]["0"]["state"]["stateCode"].trim();
        else state_code = EMPTY;

        if (
          "_embedded" in result_dict &&
          "venues" in result_dict["_embedded"] &&
          result_dict["_embedded"]["venues"].length != 0 &&
          "postalCode" in result_dict["_embedded"]["venues"]["0"] &&
          result_dict["_embedded"]["venues"]["0"]["postalCode"].trim() != UNDEFINED_LOW &&
          result_dict["_embedded"]["venues"]["0"]["postalCode"].trim() != UNDEFINED_CAP
        )
          postalCode = result_dict["_embedded"]["venues"]["0"]["postalCode"].trim();
        else postalCode = EMPTY;

        if (
          "_embedded" in result_dict &&
          "venues" in result_dict["_embedded"] &&
          result_dict["_embedded"]["venues"].length != 0 &&
          "url" in result_dict["_embedded"]["venues"]["0"] &&
          result_dict["_embedded"]["venues"]["0"]["url"].trim() != UNDEFINED_LOW &&
          result_dict["_embedded"]["venues"]["0"]["url"].trim() != UNDEFINED_CAP
        )
          upcoming_url = result_dict["_embedded"]["venues"]["0"]["url"].trim();
        else upcoming_url = EMPTY;

        if (
          "_embedded" in result_dict &&
          "venues" in result_dict["_embedded"] &&
          result_dict["_embedded"]["venues"].length != 0 &&
          "images" in result_dict["_embedded"]["venues"]["0"] &&
          result_dict["_embedded"]["venues"]["0"]["images"].length != 0 &&
          "url" in result_dict["_embedded"]["venues"]["0"]["images"]["0"] &&
          result_dict["_embedded"]["venues"]["0"]["images"]["0"]["url"].trim() != UNDEFINED_LOW &&
          result_dict["_embedded"]["venues"]["0"]["images"]["0"]["url"].trim() != UNDEFINED_CAP
        )
          image = result_dict["_embedded"]["venues"]["0"]["images"]["0"]["url"].trim();
        else image = EMPTY;

        city = city_temp + ", " + state_code;
        generate_venue_details_card(venue_name, address, state_code, city, city_temp, postalCode, upcoming_url, image);
      }
    };
    xhttp.open("GET", request_url, true);
    xhttp.send();
  }
}

function generate_venue_details_card(venue_name, address, state_code, city, city_temp, postalCode, upcoming_url, image) {
  if (venue_details.innerHTML != EMPTY) venue_details.innerHTML = EMPTY;

  venue_card_holder.insertAdjacentHTML("beforeend", '<div id= "venue_card">');
  const venue_card = document.getElementById("venue_card");

  if (venue_name != EMPTY) venue_card.insertAdjacentHTML("beforeend", '<p id= "venue_card_title">' + venue_name + "</p>");
  if (image != EMPTY) venue_card.insertAdjacentHTML("beforeend", ' <div id= "wrap"><img id= "venue_image"  src= ' + image + ' alt="seatmap" >' + "</img></div>");

  venue_card.insertAdjacentHTML("beforeend", '<div id="vl"></div>');
  venue_card.insertAdjacentHTML("beforeend", '<div id="address_box"></div>');

  venue_card.insertAdjacentHTML("beforeend", '<span id= "address_T">Address:</span>');
  venue_card.insertAdjacentHTML("beforeend", '<span id= "address_1">' + address + "</span>");
  venue_card.insertAdjacentHTML("beforeend", '<span id= "address_2">' + city + "</span>");
  venue_card.insertAdjacentHTML("beforeend", '<span id= "address_3">' + postalCode + "</span>");

  let venue_temp = venue_name.replace(reg_non_alphanumeric, "+");
  let address_temp = address.replace(reg_non_alphanumeric, "+");
  let city_name = city_temp.replace(reg_non_alphanumeric, "+");
  let query = venue_temp + "%2C+" + address_temp + "%2C+" + city_name + "%2C+" + state_code + "%2C+" + postalCode;
  let google_map_link = GOOGLE_MAP_SEARCH_PATH + query;
  venue_card.insertAdjacentHTML("beforeend", "<a href=" + google_map_link + ' class= "google_map"  target="_blank"' + ">" + '<span id= "map_text">Open in Google Maps</span>' + "</a>");

  venue_card.insertAdjacentHTML("beforeend", "<a href=" + upcoming_url + ' class= "google_map"  target="_blank"' + ">" + '<span id= "upcoming_url">More events at this venue</span>' + "</a>");

  venue_card.insertAdjacentHTML("beforeend", '<div id= "venue_card_outline"></div>');
  venue_card_holder.insertAdjacentHTML("beforeend", "</div>");
  venue_card.scrollIntoView({ behavior: "smooth" });
}

let toggle_sorting_event = false;
let toggle_sorting_genre = false;
let toggle_sorting_venue = false;
function sort_table(id, toggle_sorting) {
  document.getElementById("table").innerHTML = EMPTY;

  if (toggle_sorting == true) {
    bubbleSort(id, true);
  } else {
    bubbleSort(id, false);
  }

  if (id == "third_columns_width") {
    if (toggle_sorting_event == true) toggle_sorting_event = false;
    else toggle_sorting_event = true;
  } else if (id == "fourth_columns_width") {
    if (toggle_sorting_genre == true) toggle_sorting_genre = false;
    else toggle_sorting_genre = true;
  } else if (id == "fifth_columns_width") {
    if (toggle_sorting_venue == true) toggle_sorting_venue = false;
    else toggle_sorting_venue = true;
  }

  table_header_constructor(item_table);

  for (let i = 0; i < list_for_table.length; i++) {
    table_append_row(item_table, list_for_table, i);
  }

  custom_urls_css("event_title");
}

function swap(list_for_table, x, y) {
  let tmp = list_for_table[x];
  list_for_table[x] = list_for_table[y];
  list_for_table[y] = tmp;
}

function bubbleSort(id, toggle_bool) {
  let index;
  if (id == "third_columns_width") index = 3;
  else if (id == "fourth_columns_width") index = 4;
  else if (id == "fifth_columns_width") index = 5;

  if (toggle_bool == true) {
    var i, j;
    for (i = 0; i < list_for_table.length - 1; i++) {
      for (j = 0; j < list_for_table.length - i - 1; j++) {
        if (list_for_table[j][index].charCodeAt(0) < list_for_table[j + 1][index].charCodeAt(0)) {
          swap(list_for_table, j, j + 1);
        }
      }
    }
  } else {
    for (i = 0; i < list_for_table.length - 1; i++) {
      for (j = 0; j < list_for_table.length - i - 1; j++) {
        if (list_for_table[j][index].charCodeAt(0) > list_for_table[j + 1][index].charCodeAt(0)) {
          swap(list_for_table, j, j + 1);
        }
      }
    }
  }
}
