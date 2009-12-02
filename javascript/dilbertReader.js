var strip = new Image("/dilbert1.png");
var currentPage = 0;

UI.newPage("Dilbert Reader");
addCmds();

function showNext(){
    var cell = strip.crop((190 * currentPage), 0, 180, 174, 0);
    write("page.."+currentPage);
	UI.newPage("Dilbert Reader");
	UI.addImage(cell);
	addCmds();
	currentPage++;
}

function addCmds(){
	UI.addCmd({
		cmd: function() {
			//alert("show next"+showNextSlide);
			showNext();
		},
		label: "next"
	});
	UI.addCmd({
		cmd: function(){
			quit();
		},
		label: "exit"
	});
}