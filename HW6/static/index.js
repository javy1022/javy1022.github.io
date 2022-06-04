$(function(){
	$('button').click(function(){
		
		$.ajax({
			url: '/search',
			type: 'GET',
			
        success: function(response){
		    $("#test").html(response);
			
			
        },
        error: function(error){
            alert("ERROR");
        }
		});
	});
});


function clear_fields() {
	 document.getElementById("search_bar").value = "";
    
}