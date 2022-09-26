/*
 Client ID: stkdKi8rz4r41QtUROS-6g
 API Key: P5t7BttvGrXWg8bb79sSf_HpQ_B-S2HShUbc8nwlp4I3DFaPKVpuXMuo3sIhELcn-xxnPpLuilHqnfaQLXwU_DZ4tALO0UZ0Wd9j3YLSEp89rb6SEUxwDFwckOgvY3Yx
*/


function clear_fields() {
	 document.getElementById("keyword").value = "";
	 document.getElementById("location").value = "";
	 document.getElementById("distance").value = "10";
	 document.getElementById("category_bar").value = "Default";
	 document.getElementById("check_box").checked = false;
	
}

const submit_button = document.getElementById("submit");
const keyword = document.getElementById('keyword');
const locations = document.getElementById('locations');
const form_data = document.getElementById('form_data');
submit_button.addEventListener("click", checkboxClick, false);

let form_obj = new FormData(form_data);

function checkboxClick(event) {
 if(keyword.checkValidity() != false && locations.checkValidity() != false ){
	 event.preventDefault();
	 alert(form_obj.get('keywords'));
	
	  
 } 
}

