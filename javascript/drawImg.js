var img = new Image();
img.src = "http://localhost/Pictures/latest.png";
//write("orig w:"+img.width);
//img = img.crop(0, 0, 190, 174, 0);

//var w = window.innerWidth;
//var h = window.innerHeight;

var canvas = document.getElementById("canvas");
ctx = canvas.getContext("2d");
ctx.drawImage(img, 1, 2);