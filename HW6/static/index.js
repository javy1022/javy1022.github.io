var x = document.getElementById("nocontent");
x.style.display = "none";

function clear_fields() {
	 document.getElementById("search_bar").value = "";
}



/*$(document).ready(function() {
	
	$('form').on('submit', function(event) {
		event.preventDefault();
		$.ajax({
			data : {
				input : $('#search_bar').val(),
				
			},
			type : 'POST',
			url : '/search_input',
		
		success: function(response){
					
			alert("hi");
			alert(response);
			
			
		},
        error: function(error){
            alert("ERROR");
        }
		})
		
		
		
		

	});
	

});
*/


$(function(){
    $('#f').bind('submit', function(evt){
        event.preventDefault();
		alert("adawwadw")
		$.ajax({
			data : {
				input : $('#search_bar').val(),
				
			},
			type : 'POST',
			url : '/search_input',
		
		success: function(response){
					
			alert("hi");
			alert(response);
			
			
		},
        error: function(error){
            alert("ERROR");
        }
		})       
    });
});    
