var icon = new Image();
icon.src = "orca.png"

icon.onload = function(){	
	var canvas = document.getElementById("canvas");
	ctx = canvas.getContext("2d");
	
	ctx.drawImage(icon, 0, 0);
	
	ctx.strokeStyle = "rgb(0,0,200)";
	ctx.strokeRect (0, 0, 240, 32);

	//ctx.fillStyle = "rgba(0, 0, 200, 0.5)";
	//ctx.fillRect (30, 30, 55, 50);

	ctx.textBaseline = "bottom";
	ctx.font = "30px sans-serif";
	ctx.fillText("test", 40, 32);

}