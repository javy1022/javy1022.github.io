/*
 Client ID: stkdKi8rz4r41QtUROS-6g
 API Key: P5t7BttvGrXWg8bb79sSf_HpQ_B-S2HShUbc8nwlp4I3DFaPKVpuXMuo3sIhELcn-xxnPpLuilHqnfaQLXwU_DZ4tALO0UZ0Wd9j3YLSEp89rb6SEUxwDFwckOgvY3Yx
*/

function table_header_constructor(item_table){
	item_table.innerHTML += "<tr><th id =\"first_columns_width\">No.</th> <th id =\"second_columns_width\">Image</th> <th id =\"third_columns_width\">Business Name</th> <th id =\"fourth_columns_width\">Rating</th> <th id =\"fifth_columns_width\">Distance (miles)</th>  </tr>";
}

function clear_fields() {
	 document.getElementById("keyword").value = "";
	 document.getElementById("locations").value = "";
	 document.getElementById("distance").value = "10";
	 document.getElementById("category_bar").value = "Default";
	 document.getElementById("check_box").checked = false;
	
}

 var result_dict;
 var item_table =  document.getElementById("table");
 
function send_request(url) {
  var xhttp;
  xhttp=new XMLHttpRequest();
  xhttp.onreadystatechange = function() {
    if (this.readyState == 4 && this.status == 200) {
	  
      var resp = this.responseText;
	 // alert(resp);
	  result_dict = JSON.parse(resp) ;
	  console.log(result_dict);
	  var test_arr = Object.entries(result_dict["businesses"]["0"]);
	  alert(test_arr[6][1]);
	  
	  table_header_constructor(item_table);
	  item_table.innerHTML += "<tr><td>Jill</td><td><img src=" + test_arr[6][1] + "></img></td></tr>";
	  
	  
	  /*
	  <tr>
    <td>Jill</td>
    <td>Smith</td>
    <td>50</td>
  </tr>
	  */
    }
 };
  xhttp.open("GET", url, true);
  xhttp.send();
}

//https://api.yelp.com/v3/businesses/search?term=Sushi&latitude=33.8491816&longitude=-118.3884078&categories=Food&radius=5
function get_yelp_result(){
	var form_keyword = document.getElementById('keyword').value;
	var form_location= document.getElementById('locations').value;
	var form_category= document.getElementById('category_bar').value;
	send_request("/" + form_keyword + "/" + form_location + "/" +  form_category + "/" + "8047");
	//alert("/?keywords=" + form_keyword + "&location=" + form_location  + "&category=" + form_category + "&distance=16093")
	//alert("/" + form_keyword + "/" + form_location + "/" +  form_category + "/" + "16093");
}


const submit_button = document.getElementById("submit");
const keyword = document.getElementById('keyword');
const locations = document.getElementById('locations');
const form_data = document.getElementById('form_data');
submit_button.addEventListener("click", submitForm, false);

//let form_obj = new FormData(form_data);

function submitForm(event) {
	
 if(keyword.checkValidity() != false && locations.checkValidity() != false ){
	event.preventDefault();
	get_yelp_result();
	
	
	
	/*var xhttp = new XMLHttpRequest();
    xhttp.onreadystatechange = function() {
		if (this.readyState == 4 && this.status == 200) {
      document.getElementById("demo").innerHTML =
      this.responseText;
		}
  };
    xhttp.open("GET", "/", true);
    xhttp.send();
 }*/
}
}