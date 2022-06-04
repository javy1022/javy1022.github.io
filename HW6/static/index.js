$(function(){
	$('button').click(function(){
		
		$.ajax({
			url: '/search',
			type: 'POST',
			success: function(response){
				console.log(response);
			},
			error: function(error){
				console.log(error);
			}
		});
	});
});