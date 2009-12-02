function a () {
	var x = 3;
	
	function b() {
		//write("x++:"+ (x++));
		console.debug("x++:"+ (x++));
	};
	return b;
}

var c = a();
c();
c();
		