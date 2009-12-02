
var x = new XMLHttpRequest();

x.onReadyStateChange = function a(evt){
    write("x status:"+x.status+" text:"+x.responseText);
	var img = new Image();
	img.fromBytes(x.resBytes);
	UI.newPage("xhr img test");
	UI.addImage(img);
	UI.setTitle("got img!");
};

x.open("GET", "http://localhost:8080/files/Pictures/latest.png");
x.send(null);
