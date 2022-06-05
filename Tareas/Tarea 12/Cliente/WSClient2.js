// Copyright(C) 2015-2022  Carlos Pineda Guerrero
function WSClient2(url)
{
	this.url = url;
	this.post = function(operacion,args,callback)
	{
		var request = new XMLHttpRequest();
		var body = "";
		var pairs = [];
		var name;
		try
		{
			var body = JSON.stringify(args);
			var req = new XMLHttpRequest();
			req.open("POST",url + "/" + operacion);
			req.setRequestHeader("Accept","application/json");
			req.setRequestHeader("Content-Type","application/json");
			req.onload = function()
			{
				if (callback != null) callback(req.status,req.response);
			};
			req.send(body);
		}
		catch (e)
		{
			alert("Error: " + e.message);
		}
	}
}