let json;

function httpGetI(theUrl, reponse) {
    fetch(theUrl, {
        method: "GET",
    }).then(function(response) {
        return response.json();
    }).then(function(data) {
        //response = data;
        //console.log(data);
        if(data.status == 200) {
        json = data.data;
        }
    }).catch(function(err) {
        console.log(err);
});
}

function doLogic() {
	let response;
	httpGetI('http://localhost:8090/api/v1/matchstate/80c7ff93-0700-4e93-8888-9f01f0f95cf8', response);
	

}
function setupInterval() {
	setInterval(doLogic, 10)
}

addEventListener('load', setupInterval)