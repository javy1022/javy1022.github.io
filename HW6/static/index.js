/* eslint-disable no-unused-vars */

const location_form = document.getElementById("locations");

function clear_fields() {
  document.getElementById("keyword").value = "";
  document.getElementById("distance").value = "";
  document.getElementById("category_bar").value = "Default";
  document.getElementById("check_box").checked = false;

  if (location_form.style.display == "none") {
    location_form.style.display = "initial";
    location_form.required = truee;
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

