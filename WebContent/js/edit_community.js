$(document).ready(function(){
	// Add a new website functionality
	$("#addWebsite").click(function(){
		var name = document.getElementById('website_name').value;
		var url = document.getElementById('website_url').value;
		
		if(name.trim().length == 0) {
			showMessage('error', 'please enter a website name', 6000);
			return;
		} else if (url.indexOf("http://") != 0) {			
			showMessage('error', 'url must start with http://', 6000);
			return;
		} else if (url.trim().length <= 12) {
			showMessage('error', 'the given url is not long enough', 6000);
			return;
		}			
		
		var data = {name: name, url: url};
		$.post(
				"/starexec/services/website/add/space/" + $('#comId').val(),
				data,
				function(returnCode) {
			    	if(returnCode == '0') {
			    		showMessage('success', "website successfully added", 5000);
			    		$('#websites').append('<li><a href="' + url + '">' + name + '</a></li>');
			    		$('#websites li').removeClass('shade');
			    		$('#websites li:even').addClass('shade');
			    	} else {
			    		showMessage('error', "error: website not added. please try again", 5000);
			    	}
				},
				"json"
		);
	});
	
	// Make forms editable
	editable("name");
	editable("desc");	
	typeEditable();
	
	// Style the table with alternate rows
	$('#details tr:even').addClass('shade');
	$('#websites li:even').addClass('shade');
	$('#benchTypes tr:even').addClass('shade');	
	
	// Add toggles for the "add new" buttons and hide them by default
	$('#toggleWebsite').click(function() {
		$('#new_website').slideToggle('fast');	
	});	
	$('#toggleType').click(function() {
		$('#newType').slideToggle('fast');
	});	
	$('#new_website').hide();
	$('#newType').hide();
	
	// Adds 'regex' function to validator
	$.validator.addMethod(
			"regex", 
			function(value, element, regexp) {
				var re = new RegExp(regexp);
				return this.optional(element) || re.test(value);
	});
	
	// Form validation
	$("#typeForm").validate({
		rules : {
			typeName : {
				required : true,
				regex : "^[a-zA-Z0-9\\-\\s_]+$",
				minlength : 2,
				maxlength: 32
			},
			typeDesc : {
				required : false,	
				regex : "^[a-zA-Z0-9\\-\\s_.!?/,\\\\+=\"'#$%&*()\\[{}\\]]+$",
				maxlength: 300
			},
			typeFile : {
				required : true				
			}			
		},
		messages : {
			typeName : {
				required : "enter a type name",
				minlength : "needs to be at least 2 characters",
				maxlength : "32 characters max",
				regex : "invalid characters"
			},
			typeDesc : {				
				maxlength : "no more than 300 characters",	
				regex : "invalid characters"
			},
			typeFile : {
				required : "choose a file"				
			}
		}		
	});
});

function editable(attribute) {
	$('#edit' + attribute).click(function(){
		var old = $(this).html();
		
		if(attribute == "desc") {
			$(this).after('<td><textarea>' + old + '</textarea>&nbsp;<button id="save' + attribute + '">save</button>&nbsp;<button id="cancel' + attribute + '">cancel</button>&nbsp;</td>').remove();
		} else {
			$(this).after('<td><input type="text" value="' + old + '" />&nbsp;<button id="save' + attribute + '">save</button>&nbsp;<button id="cancel' + attribute + '">cancel</button>&nbsp;</td>').remove();	
		}		
		
		$('#save' + attribute).click(function(){saveChanges(this, true, attribute);});
		$('#cancel' + attribute).click(function(){saveChanges(this, old, attribute);});
	});
	
	$('#edit' + attribute).css('cursor', 'pointer');
} 

var x= 0;

function typeEditable() {			
	$('#benchTypes').delegate('tr', 'click', function(){
		$('#benchTypes').undelegate('tr');
		$('#benchTypes tr').css('cursor', 'default');		
		
		$('#benchTypes').find('tr:first').append('<th>action</th>');
		var old = $(this).html();
		$(this).addClass('selected');
		
		var tName = $(this).children(':first');
		var tDesc = $(this).children(':nth-child(2)');
		var tFile = $(this).children(':nth-child(3)');
		
		$(tName).html('<input type="text" name="typeName" value="' +  $(tName).text() + '" />');
		$(tDesc).html('<textarea name="typeDesc">' + $(tDesc).text() + '</textarea>');
		$(tFile).html('<input type="file" name="typeFile" />');
		$(this).append('<input type="hidden" name="typeId" value="' + $(this).attr('id').split('_')[1] + '" />');
				
		var saveBtn = $('<button type="submit">save</button><br/>');
		var cancelBtn = $('<button type="button">cancel</button>');
		var saveCol = $('<td></td>').append(saveBtn).append(cancelBtn);			
		$(this).append(saveCol);
		
		$(saveBtn).click(function(){saveChanges(this, true, attribute);});
		$(cancelBtn).click(function(){ restoreBenchTypeRow(old); });			
	});
	
	$('#benchTypes tr').css('cursor', 'pointer');
}

function restoreBenchTypeRow(old) {	
	$('#benchTypes').find('tr:first').find('th:last').remove();	
	$('#benchTypes tr.selected').html(old);
	$('#benchTypes tr.selected').removeClass('selected');	
	typeEditable();
}

function saveChanges(obj, save, attr) {
	var t = save;
	if (true == save) {
		t = $(obj).siblings('input:first').val();
		if(t == null) {
			t = $(obj).siblings('textarea:first').val();			
		}		
		
		$.post(  
			    "/starexec/services/edit/space/" + attr + "/" + $('#comId').val(),
			    {val: t},
			    function(returnCode){  			        
			    	if(returnCode == '0') {
			    		showMessage('success', "information successfully updated", 5000);
			    	} else if(returnCode == '2') {
			    		showMessage('error', "insufficient permissions. only space leaders can update their space", 5000);
			    	} else {
			    		showMessage('error', "information not changed. please try again", 5000);
			    	}
			     },  
			     "json"  
		).error(function(){
			alert('Session expired');
			window.location.reload(true);
		});
	}
	
	// Hide the input box and replace it with the table cell
	$(obj).parent().after('<td id="edit' + attr + '">' + t + '</td>').remove();
	
	// Make the value editable again
	editable(attr);
}