/* eslint-disable no-unused-vars */

const reg_non_alphanumeric  = /[^a-z0-9+]+/gi;
const reg_remove_all_spaces_after_end_string  = /\s*$/;

const location_form = document.getElementById("locations");
const keyword = document.getElementById('keyword');

const form_data = document.getElementById('form_data');
const submit_button = document.getElementById("submit");


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
   console.log("ok")
   

 }else{
  console.log("ohmygod")

 }
}

