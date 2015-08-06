
window.onload=function(){
	if(window.injectedObject){
		injectedObject.pageLoadFinished();
	}
}

function openImage(url) {
	if(window.injectedObject){
		injectedObject.openImage(url);
	}
}
