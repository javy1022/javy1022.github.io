/*
 Client ID: stkdKi8rz4r41QtUROS-6g
 Yelp API Key: P5t7BttvGrXWg8bb79sSf_HpQ_B-S2HShUbc8nwlp4I3DFaPKVpuXMuo3sIhELcn-xxnPpLuilHqnfaQLXwU_DZ4tALO0UZ0Wd9j3YLSEp89rb6SEUxwDFwckOgvY3Yx
 Goole Map API Key: AIzaSyBJa7H7NebIkHQVvifN-TKvBlsJnQNwMLE
 IpInfo API KEY: 69aeb460f27a79
 
*/

const reg_non_alphanumeric  = /[^a-z0-9+]+/gi;
const reg_remove_all_spaces_after_end_string  = /\s*$/;
const oneMile_in_meter = 1609.344;
const GOOGLE_API_HOST = "https://maps.googleapis.com";
const GEOCODING_SEARCH_PATH = "/maps/api/geocode/json";
const GOOGLE_API_KEY = "AIzaSyBJa7H7NebIkHQVvifN-TKvBlsJnQNwMLE";
document.getElementById("check_box").checked = false;


function table_header_constructor(item_table){
	item_table.innerHTML += "<tr id =\"first_row_height\"><th id =\"first_columns_width\">No.</th> <th id =\"second_columns_width\">Image</th> <th id =\"third_columns_width\">Business Name</th> <th id =\"fourth_columns_width\">Rating</th> <th id =\"fifth_columns_width\">Distance (miles)</th>  </tr>";
}

function table_append_row(item_table, list_for_table, i){
		
	item_table.innerHTML += "<tr class=\"rows_height\"><td class=\"table_text\">" + (i+1) + "</td><td><img src=" + list_for_table[i][0] + " class=\"yelp_image\"></img></td> <td class=\"table_text\">" +  list_for_table[i][1]  + "</td> <td class=\"table_text\">" + list_for_table[i][2] + "</td> <td class=\"table_text\">" + list_for_table[i][3] +"</td> </tr>";
	
}

function buffer_array_append(result_dict_item, buffer_array, header){
	for(let i = 0; i < Object.keys(result_dict_item).length; i++){
		
			if(result_dict_item[i][0] == header){
				if(header != "distance") buffer_array.push(result_dict_item[i][1]);
				else{
					var distanceMeters_to_miles = (result_dict_item[i][1] / oneMile_in_meter).toFixed(2);
					buffer_array.push(distanceMeters_to_miles);
				}
			}
		
		}	
	
}

function clear_fields() {
	 document.getElementById("keyword").value = "";
	 document.getElementById("locations").value = "";
	 document.getElementById("distance").value = "10";
	 document.getElementById("category_bar").value = "Default";
	 document.getElementById("check_box").checked = false;
	 document.getElementById("table").innerHTML = "";
}

 var result_dict;
 var item_table =  document.getElementById("table");
 var items_for_table_list = new Array();

 
function send_request(url) {
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	  
	  if(document.getElementById("table").innerHTML != ""){
		  document.getElementById("table").innerHTML = "";
	  }
	  
      var resp = this.responseText;
	  result_dict = JSON.parse(resp) ;
	  console.log(result_dict);
	  table_header_constructor(item_table);
	
	  
	  var total_businesses = Object.keys(result_dict["businesses"]).length;
	  var list_for_table = new Array();
	  
	  for (let i = 0; i < total_businesses  ; i++) {
		var result_dict_item = Object.entries(result_dict["businesses"][i]);
		var buffer_array = new Array();
		buffer_array_append(result_dict_item, buffer_array, "image_url")
		buffer_array_append(result_dict_item, buffer_array, "name")
		buffer_array_append(result_dict_item, buffer_array, "rating")
		buffer_array_append(result_dict_item, buffer_array, "distance")				
				
		list_for_table.push(buffer_array);
	  } 
	  	   
	  
	  for (let i = 0; i < total_businesses  ; i++) {
		table_append_row(item_table, list_for_table, i);
	  } 
	  document.getElementById('table').scrollIntoView();
    }
 };
  xhttp.open("GET", url, true);
  xhttp.send();
}


function get_yelp_result(lat, lng){
	
	var form_keyword = document.getElementById('keyword').value;
	var form_location= document.getElementById('locations').value; // to replace
	var form_category= document.getElementById('category_bar').value;
	var form_distance_in_meter = Math.round(parseInt(document.getElementById('distance').value) * oneMile_in_meter) ;
	
	if((typeof lat) == "number" && (typeof lng) == "number"){
		send_request("/" + form_keyword +  "/" + lat.toString() + "/" + lng.toString() + "/" + form_location + "/" +  form_category + "/" + form_distance_in_meter);
	}
	else if (typeof lat == "string" && typeof lng == "string") {
		send_request("/" + form_keyword +  "/" + lat + "/" + lng + "/" + form_location + "/" +  form_category + "/" + form_distance_in_meter);
		alert("hi");
	}
		
    //https://api.yelp.com/v3/businesses/search?term=Sushi&latitude=33.8491816&longitude=-118.3884078&categories=Food&radius=5	
	//send_request("/" + form_keyword +  "/" + lat.toString() + "/" + lng.toString() + "/" + form_location + "/" +  form_category + "/" + form_distance_in_meter);
	
}


const submit_button = document.getElementById("submit");
const keyword = document.getElementById('keyword');
const locations = document.getElementById('locations');
const form_data = document.getElementById('form_data');
submit_button.addEventListener("click", submitForm, false);

function submitForm(event) {
	
 if(keyword.checkValidity() != false && locations.checkValidity() != false ){
	event.preventDefault();
	
	var form_location= document.getElementById('locations').value;
	var buffer = form_location.replace(reg_remove_all_spaces_after_end_string, "");
	var api_address_param = buffer.replace(reg_non_alphanumeric, '+');
	
	var url = GOOGLE_API_HOST + GEOCODING_SEARCH_PATH + "?address=" + api_address_param + "&key=" + GOOGLE_API_KEY;
	
	//geoCode_send_request(url);
	IpInfo_send_request("https://ipinfo.io/?token=69aeb460f27a79");
	
}
}

const check_box = document.getElementById("check_box");
const location_form = document.getElementById("locations");


function check(event) {

if(location_form.disabled == false){
	 location_form.value = "";
	 location_form.disabled = true;
	 location_form.style.backgroundColor = '#e6e6e6';
	 
 }else if (location_form.disabled == true){
	 location_form.style.backgroundColor = 'white';
	 location_form.disabled = false;
	 
  }
}


function geoCode_send_request(url) {
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	  
	  	  
      var resp = this.responseText;
	  result_dict = JSON.parse(resp) ;
	  console.log(result_dict);
	 	
	  get_yelp_result(result_dict["results"]["0"]["geometry"]["location"]["lat"], result_dict["results"]["0"]["geometry"]["location"]["lng"])		
	}
 };
  xhttp.open("GET", url, true);
  xhttp.send();
}


function IpInfo_send_request(url) {
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	   	  
      var resp = this.responseText;
	  result_dict = JSON.parse(resp) ;
	  
	  var buffer = result_dict["loc"];
	  var lat_lng_array = buffer.split(",")
	  
	  console.log(result_dict);
	  get_yelp_result(lat_lng_array[0], lat_lng_array[1]);
	}
 };
  xhttp.open("GET", url, true);
  xhttp.send();
}
