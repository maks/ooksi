
UI.newPage("callback test");
UI.addCmd({
    cmd: function() {
        alert("got cmd callback!");
    },
	label: "do cb" 
});
UI.addCmd({
    cmd: function() {
        //alert("got test 2 callback!");
		popup();
    },
    label: "test 2" 
});