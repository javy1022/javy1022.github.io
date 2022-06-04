/*$(function(){
	$('button').click(function(){
		
		$.ajax({
			url: '/image',
			type: 'GET',
			
        success: function(response){
		    $("#test").html(response[0]);
			
			
        },
        error: function(error){
            alert("ERROR");
        }
		});
	});
});*/


function clear_fields() {
	 document.getElementById("search_bar").value = "";
    
}

function load_images() {
	 
	 $.ajax({
			url: '/image',
			type: 'GET',
			
        success: function(response){
		    for (let i = 0; i < 4; i++) {
			/*$("#test").html(response[0]);*/
			var img = document.createElement("img");
			img.src = response[i];
			let list = document.getElementById("image"); 
			list.append(img);
			}
		},
        error: function(error){
            alert("ERROR");
        }
		});
     
}