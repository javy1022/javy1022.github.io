/* eslint-disable no-mixed-spaces-and-tabs */
/* eslint-disable no-unused-vars */

const GOOGLE_API_HOST = "https://maps.googleapis.com";
const GEOCODING_SEARCH_PATH = "/maps/api/geocode/json";
const GOOGLE_API_KEY = "AIzaSyDIGpZqNauMP2qqXnnanXzP6ba0AIIkT58";
const reg_non_alphanumeric = /[^a-z0-9+]+/gi;
const reg_remove_all_spaces_after_end_string = /\s*$/;
const DEFAULT_DISTANCE = "10";

const keyword = document.getElementById("keyword");
const distance = document.getElementById("distance");
const category = document.getElementById("category_bar");
const location_form = document.getElementById("locations");
const form_data = document.getElementById("form_data");
const submit_button = document.getElementById("submit");

const item_table =  document.getElementById("table");


function clear_fields() {
  document.getElementById("keyword").value = "";
  document.getElementById("distance").value = "";
  document.getElementById("category_bar").value = "Default";
  document.getElementById("check_box").checked = false;

  if (location_form.style.display == "none") {
    location_form.style.display = "initial";
    location_form.required = true;
  } else {
    document.getElementById("locations").value = "";
  }

  //document.getElementById("table").innerHTML = "";
  //document.getElementById("card_holder").innerHTML = "";
  //document.getElementById("no_record_container").innerHTML = "";

  //list_for_table = [];
}

function check(event) {
  if (location_form.style.display == "none") {
    location_form.style.display = "initial";
    location_form.required = true;
  } else {
    location_form.value = "";
    location_form.required = false;
    location_form.style.display = "none";
  }
}

submit_button.addEventListener("click", submitForm, false);
function submitForm(event) {
  if (keyword.checkValidity() != false && location_form.checkValidity() != false && location_form.style.display != "none") {
    event.preventDefault();

    let form_location = location_form.value;
    let buffer = form_location.replace(reg_remove_all_spaces_after_end_string, "");
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

  if (distance.value == "") form_distance = DEFAULT_DISTANCE;
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

        list_for_table.push(buffer_array);
       
      }
      console.log(list_for_table)


      if(list_for_table.length > 0){
        table_header_constructor(item_table);
      }else{
         
       console.log("no record")
       
      } 
      
      for (let i = 0; i < total_events  ; i++) {
        table_append_row(item_table, list_for_table, i);
        } 






    }
  };
  xhttp.open("GET", url, true);
  xhttp.send();
}

function buffer_array_append(result_dict_item, buffer_array, header) {
  for (let i = 0; i < Object.keys(result_dict_item).length; i++) {
    if (result_dict_item[i][0] == header) {
      if (header == "dates") {
        let time_obj = result_dict_item[i][1]["start"];
        if ("localDate" in time_obj) buffer_array.push(time_obj["localDate"]);
        else buffer_array.push(" ");

        if ("localTime" in time_obj) buffer_array.push(time_obj["localTime"]);
        else buffer_array.push(" ");
      } else if (header == "images") {
        let image_obj = result_dict_item[i][1];
        if (image_obj.length != 0) buffer_array.push(image_obj[0]["url"]);
        else buffer_array.push(" ");
      } else if (header == "name") {
        let name_obj = result_dict_item[i][1];
        if (name_obj.length != 0) buffer_array.push(name_obj);
        else buffer_array.push(" ");
      } else if (header == "classifications") {
        let genre_obj = result_dict_item[i][1];

        if ("name" in genre_obj[0]["segment"]) buffer_array.push(genre_obj[0]["segment"]["name"]);
        else buffer_array.push(" ");
      } else if (header == "_embedded") {
        let venues_obj = result_dict_item[i][1]["venues"][0];

        if (venues_obj["name"].length != 0) buffer_array.push(venues_obj["name"]);
        else buffer_array.push(" ");
      }
    }
  }
}

function table_header_constructor(item_table){
	item_table.innerHTML += "<tr id =\"first_row_height\"><th id =\"first_columns_width\">Date</th> <th id =\"second_columns_width\">Icon</th> <th id =\"third_columns_width\" onClick=\"sort_table(this.id)\" >Event</th> <th id =\"fourth_columns_width\" onClick=\"sort_table(this.id)\">Genre</th> <th id =\"fifth_columns_width\" onClick=\"sort_table(this.id)\">Venue</th>  </tr>";
	document.getElementById("third_columns_width").style.cursor = "pointer";
	document.getElementById("fourth_columns_width").style.cursor = "pointer";
	document.getElementById("fifth_columns_width").style.cursor = "pointer";
}

function table_append_row(item_table, list_for_table, i){
		
	item_table.innerHTML += "<tr class=\"rows_height\"><td class=\"table_text\">" +  list_for_table[i][0] + "</td><td><img src=" + list_for_table[i][2] + " class=\"yelp_image\"></img></td> <td class=\"table_text\"> <a href = \"#\" class=\"name_title\"  onclick='business_detail_request(\""+ list_for_table[i][4] +"\");' >" +  list_for_table[i][3]  + "</a></td> <td class=\"table_text\">" + list_for_table[i][4] + "</td> <td class=\"table_text\">" + list_for_table[i][5] +"</td> </tr>";
	
}

function sort_table(id){
	console.log("sort placeholder")
}