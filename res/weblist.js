//TODO:
var appList = ["first app", "second app"];

UI.newPage("App List");
UI.addCmd({
    cmd: function() {
        quit();
    },
	label: "Exit" 
});

//UI.addItem("Test item no label");

var img = new Image();
img.src = "file:///orca.png";
for (var i in appList) {
	UI.addItem(appList[i], null, img);
}