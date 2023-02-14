/* eslint-disable no-mixed-spaces-and-tabs */
/* eslint-disable no-unused-vars */

const GOOGLE_API_HOST = "https://maps.googleapis.com";
const GEOCODING_SEARCH_PATH = "/maps/api/geocode/json";
const GOOGLE_API_KEY = "AIzaSyDIGpZqNauMP2qqXnnanXzP6ba0AIIkT58";
const reg_non_alphanumeric  = /[^a-z0-9+]+/gi;
const reg_remove_all_spaces_after_end_string  = /\s*$/;
const DEFAULT_DISTANCE = "10"


const keyword = document.getElementById("keyword");
const distance = document.getElementById("distance")
const category= document.getElementById("category_bar");
const location_form = document.getElementById("locations");

const form_data = document.getElementById('form_data');
const submit_button = document.getElementById('submit');


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
	
  if(keyword.checkValidity() != false && location_form.checkValidity() != false && location_form.style.display != "none"){
   event.preventDefault();
   
   var form_location= location_form.value;
   var buffer = form_location.replace(reg_remove_all_spaces_after_end_string, "");
   var api_address_param = buffer.replace(reg_non_alphanumeric, '+');
   var url = GOOGLE_API_HOST + GEOCODING_SEARCH_PATH + "?address=" + api_address_param + "&key=" + GOOGLE_API_KEY;

   geoCode_send_request(url);

 }else{
  console.log("ohmygod")

 }
}


function geoCode_send_request(url) {
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
      console.log("bingo")
	    var resp = this.responseText;
	    var result_dict = JSON.parse(resp) ;
	    
      if(result_dict["status"] != "ZERO_RESULTS"){
        get_yelp_result(result_dict["results"]["0"]["geometry"]["location"]["lat"], result_dict["results"]["0"]["geometry"]["location"]["lng"]);
      }
	 }
  };
  xhttp.open("GET", url, true);
  xhttp.send();
}


function get_yelp_result(lat, lng){
	var form_keyword = keyword.value;
	var form_category= category.value;
  var form_distance;
	
  if(distance.value == "") form_distance = DEFAULT_DISTANCE;
  else form_distance = distance.value;

	if((typeof lat) == "number" && (typeof lng) == "number"){
		send_request("/" + form_keyword +  "/" + lat.toString() + "/" + lng.toString() + "/" +   form_category + "/" + form_distance);
	
	}
	else if ((typeof lat) == "string" && (typeof lng) == "string") {
		send_request("/" + form_keyword +  "/" + lat + "/" + lng + "/" +  form_category + "/" + form_distance);
		
	}
	
}

function send_request(url) {
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	    var resp = this.responseText;
	    var result_dict = JSON.parse(resp) ;
      console.log(result_dict)
	    console.log(result_dict["_embedded"]["events"][0].name);
	  
    }
	
 };
  xhttp.open("GET", url, true);
  xhttp.send();
}
