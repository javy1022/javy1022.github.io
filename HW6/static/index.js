

function clear_fields() {
	 document.getElementById("search_bar").value = "";
}

$(document).ready(function() {

	$('form').on('submit', function(event) {
		document.getElementById("loader").style.display = 'block';
		let k = document.getElementById("list");
		list.innerHTML = "";
		
		$.ajax({
			data : {
				input : $('#search_bar').val(),
				
			},
			type : 'POST',
			url : '/search_input',
		
		success: function(response){
			
		    for (let i = 0; i < response.length; i++) {
			
			var img = document.createElement("img");
			
			img.src = response[i];
			
			if(response[i] == "/assets/shared/missing_image.png"){
				img.src = "/icons/artsy_logo.svg"
				img.style.borderRadius = "50%";
				img.style.border = "5px solid white";
				img.width = "230";
			}
						
			img.style.borderRadius = "50%";
			img.style.border = "5px solid white";
			let list = document.getElementById("list"); 
			list.append(img);
						
			}
						
			document.getElementById("loader").style.display = 'none';
			
			
		},
        error: function(error){
            alert("ERROR");
        }
		})
		
		$.ajax({
			data : {
				input : $('#search_bar').val(),
				
			},
			type : 'POST',
			url : '/search_title',
		
		success: function(response){
		   	for (let i = 0; i < response.length; i++) {
			const newDiv = document.createElement("p");
			const newContent = document.createTextNode(response[i]);
			let list = document.getElementById("title"); 
			newDiv.appendChild(newContent);
			newDiv.id = "name"
			list.appendChild(newDiv);
						
		}
		},
        error: function(error){
            alert("ERROR2");
        }
		})
		
		event.preventDefault();

	});
	

});
/*
function load_images(event) {
	 event.preventDefault();
	 var nameValue = document.getElementById("search_bar").value;
	 
	 let xhr = new XMLHttpRequest();
     xhr.open("POST", '/debug', true);
     xhr.setRequestHeader('Content-Type', 'application/json');
     xhr.send(JSON.stringify({
        "values": nameValue 
    }));
	 
	$.ajax({
			url: '/debug',
			type: 'POST',
			
        success: function(response){
		    for (let i = 0; i < response.length; i++) {
			
			var img = document.createElement("img");
			img.src = response[i];
			let list = document.getElementById("image"); 
			list.append(img);
			
			}
			$("#test").html(response);
			alert("success");
			
		},
        error: function(error){
            alert("ERROR");
        }
		});
		

		
     
}*/
