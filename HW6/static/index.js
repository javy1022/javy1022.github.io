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

const IPINFO_API_HOST = "https://ipinfo.io/";
const IPINFO_API_KEY = "69aeb460f27a79";

document.getElementById("check_box").checked = false;


function table_header_constructor(item_table){
	item_table.innerHTML += "<tr id =\"first_row_height\"><th id =\"first_columns_width\">No.</th> <th id =\"second_columns_width\">Image</th> <th id =\"third_columns_width\">Business Name</th> <th id =\"fourth_columns_width\">Rating</th> <th id =\"fifth_columns_width\">Distance (miles)</th>  </tr>";
}

function table_append_row(item_table, list_for_table, i){
		
	item_table.innerHTML += "<tr class=\"rows_height\"><td class=\"table_text\">" + (i+1) + "</td><td><img src=" + list_for_table[i][0] + " class=\"yelp_image\"></img></td> <td class=\"table_text\"> <a href = \"#\" class=\"business_name\"  onclick='business_detail_request(\""+ list_for_table[i][4] +"\");' >" +  list_for_table[i][1]  + "</a></td> <td class=\"table_text\">" + list_for_table[i][2] + "</td> <td class=\"table_text\">" + list_for_table[i][3] +"</td> </tr>";
	
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
	 document.getElementById("locations").disabled = false;
	 document.getElementById("locations").style.backgroundColor = 'white';
	 document.getElementById("table").innerHTML = "";
	 
	 list_for_table = [];
}

function business_detail_request(id) {
		
	
	send_request_business_detail("/" + id);
		
	
	 
	// document.getElementById('card').scrollIntoView();           for debug convinience
    
}

function send_request_business_detail(url) {
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	 
      var resp = this.responseText;
	  result_dict = JSON.parse(resp) ;
	  console.log(result_dict);
	 
	  var name = result_dict["name"];
	  var status_bool = result_dict.hours[0]["is_open_now"];
	  var phone_number = result_dict["display_phone"];
	  var price = result_dict["price"];
	  var more_info_url = result_dict["url"];
	  var photos_array = new Array();
	  
	  var transactions_support_buffer = ""; 
	  var transactions_support;
	  
	  var address_buffer = "";
	  var address;
	  
	  var categories_buffer = "";
	  var category;
	  
	   for(let i = 0; i < result_dict["photos"].length; i ++){
		  	photos_array.push(result_dict["photos"][i]);  
	  }
	  
	  
	  
	  
	  for(let i = 0; i < result_dict["transactions"].length; i ++){
		  
		   if(i != result_dict["transactions"].length - 1) transactions_support_buffer += result_dict["transactions"][i].charAt(0).toUpperCase() + result_dict["transactions"][i].slice(1) + " | ";
		   else transactions_support_buffer += result_dict["transactions"][i].charAt(0).toUpperCase() + result_dict["transactions"][i].slice(1) ;
	  }
	  	  
	  
	  for(let i = 0; i < result_dict["categories"].length; i ++){
		   
		   if(i != result_dict["categories"].length - 1) categories_buffer += result_dict["categories"][i]["title"] + " | ";
		   else categories_buffer += result_dict["categories"][i]["title"] ;
	  }
	  
	  
	  for(let i = 0; i < result_dict["location"]["display_address"].length; i ++){
		  
		  if(i != result_dict["categories"].length - 1) address_buffer += result_dict["location"]["display_address"][i] + " ";
		  else  address_buffer += result_dict["location"]["display_address"][i] ;
		  
	  }
	  
	  transactions_support = transactions_support_buffer;
	  address = address_buffer;
	  category = categories_buffer;
	   
	  card.innerHTML += "<p id= \"business_name\">" + name + "</p>"
	  
	  
    }
 };
  xhttp.open("GET", url, true);
  xhttp.send();
}

/*function card_detail(result_dict, header){
	for(let i = 0; i < Object.keys(result_dict).length; i++){
		
			if(result_dict_item[i][0] == header){
				 buffer_array.push(result_dict_item[i][1]);
			
		}	
	
}*/


 
function preventDefault(event) {
	event.preventDefault();
    
}
 

 var result_dict;
 var item_table =  document.getElementById("table");
 var card =  document.getElementById("card");
 var items_for_table_list = new Array();
 var list_for_table = new Array();
  
function send_request(url) {
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	  
	  if(document.getElementById("table").innerHTML != ""){
		  document.getElementById("table").innerHTML = "";
		  list_for_table = [];
	  }
	  
      var resp = this.responseText;
	  result_dict = JSON.parse(resp) ;
	  console.log(result_dict);
	  table_header_constructor(item_table);
	
	  
	  var total_businesses = Object.keys(result_dict["businesses"]).length;
	 	  
	  for (let i = 0; i < total_businesses  ; i++) {
		var result_dict_item = Object.entries(result_dict["businesses"][i]);
		var buffer_array = new Array();
		buffer_array_append(result_dict_item, buffer_array, "image_url");
		buffer_array_append(result_dict_item, buffer_array, "name");
		buffer_array_append(result_dict_item, buffer_array, "rating");
		buffer_array_append(result_dict_item, buffer_array, "distance")	;			
		buffer_array_append(result_dict_item, buffer_array, "id");	
	
		list_for_table.push(buffer_array);
	  } 
	  	  
  
	  
	  for (let i = 0; i < total_businesses  ; i++) {
		table_append_row(item_table, list_for_table, i);
	  } 
	  document.getElementById('table').scrollIntoView();
	  
	  var business_urls_array = document.getElementsByClassName("business_name");
	  
	  for (let i = 0; i < business_urls_array.length; i++) {
		 business_urls_array[i].addEventListener('click', preventDefault, false);
      }
	  
    }
 };
  xhttp.open("GET", url, true);
  xhttp.send();
}


function get_yelp_result(lat, lng){
	
	var form_keyword = document.getElementById('keyword').value;
	var form_category= document.getElementById('category_bar').value;
	var form_distance_in_meter = Math.round(parseInt(document.getElementById('distance').value) * oneMile_in_meter) ;
	
	if((typeof lat) == "number" && (typeof lng) == "number"){
		send_request("/" + form_keyword +  "/" + lat.toString() + "/" + lng.toString() + "/" +   form_category + "/" + form_distance_in_meter);
	
	}
	else if ((typeof lat) == "string" && (typeof lng) == "string") {
		send_request("/" + form_keyword +  "/" + lat + "/" + lng + "/" +  form_category + "/" + form_distance_in_meter);
		
	}
		
}


const submit_button = document.getElementById("submit");
const keyword = document.getElementById('keyword');
const locations = document.getElementById('locations');
const form_data = document.getElementById('form_data');
submit_button.addEventListener("click", submitForm, false);

function submitForm(event) {
	
 if(keyword.checkValidity() != false && locations.checkValidity() != false && location_form.disabled == false){
	event.preventDefault();
	
	var form_location= document.getElementById('locations').value;
	var buffer = form_location.replace(reg_remove_all_spaces_after_end_string, "");
	var api_address_param = buffer.replace(reg_non_alphanumeric, '+');
	
	var url = GOOGLE_API_HOST + GEOCODING_SEARCH_PATH + "?address=" + api_address_param + "&key=" + GOOGLE_API_KEY;
	
	geoCode_send_request(url);
	
	
}else if (keyword.checkValidity() != false && location_form.disabled == true){
	event.preventDefault();
	IpInfo_send_request(IPINFO_API_HOST + "?token=" + IPINFO_API_KEY);
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

