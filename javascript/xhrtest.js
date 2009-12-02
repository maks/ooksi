
var x = new XMLHttpRequest();

x.onReadyStateChange = function a(evt){
    write("x status:"+x.status+" text:"+x.responseText);
};

x.open("GET", "http://localhost:8080/sandbox/midp");
x.send(null);
