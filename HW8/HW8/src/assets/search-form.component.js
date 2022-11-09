function clear_fields() {
	 document.getElementById("keyword_input").value = "";
	 document.getElementById("location_input").value = "";
	 document.getElementById("distance_input").value = "10";
	 document.getElementById("category_input").value = "Default";
	 document.getElementById("flexCheckDefault").checked = false;
	 document.getElementById("location_input").disabled = false;
}

function check(event) {

if(location_input.disabled == false){
	 location_input.value = "";
	 location_input.disabled = true;
	 
	 
 }else if (location_input.disabled == true){
	
	location_input.disabled = false;
	 
  }
}