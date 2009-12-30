var img = new Image();
img.src = "orca.png";
var canvas = document.getElementById("canvas");
ctx = canvas.getContext("2d");

ctx.drawImage(img, 0 , 0);