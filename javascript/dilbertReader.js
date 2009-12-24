var strip = new Image();
strip.src = "dilbert1.png";
var currentPage = 0;

function show(action){
	var CELL_OFFSET = 220;
	var CELL_HEIGHT = 199;
	var CELL_WIDTH = 202;
	var CELL_COUNT = 3;
	
	var OFFSETS = [1, 220, 438];
	
	if ( ((currentPage+action) >= 0) && ((currentPage+action) < CELL_COUNT) )  {
		currentPage = currentPage+action;
	}
    write("page.."+currentPage);
    
    var canvas = document.getElementById("canvas");
    ctx = canvas.getContext("2d");
    
    ctx.drawImage(strip, (OFFSETS[currentPage]), 0, CELL_WIDTH, CELL_HEIGHT,
    	0, 0, CELL_WIDTH, CELL_HEIGHT); 
}

document.onkeydown = function(e) {
      var keycode = e.keyCode;
	  write("keycode:"+keycode);
	  switch(keycode) {
	  case 39:
		  show(1);
		  break;
	  case 37:
		  show(-1);
		  break;
	  }
}

show(0);
