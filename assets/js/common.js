
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

function img_replace_by_url(url) {
	var objs = document.getElementsByTagName("img"); 

	for(var i=0;i<objs.length;i++) {
		var imgSrc = objs[i].getAttribute("src_link");     
		var imgOriSrc = objs[i].getAttribute("ori_link");  
		if(imgOriSrc == url) {  
   			objs[i].setAttribute("src",imgSrc);
		}	
	}
}

function img_replace_all() {
	
	var result = '';
	
	var objs = document.getElementsByTagName("img"); 
	for(var i=0;i<objs.length;i++) {    
		var imgSrc = objs[i].getAttribute("src_link");     
		objs[i].setAttribute("src", imgSrc );
		
		result = result + imgSrc;
		result = result + ","; 
	}
	
	return result;
}