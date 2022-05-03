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
	httpGetI('http://localhost:8090/api/v1/matchstate/48a90d62-b797-4dbe-9d5c-078ffc222e53', response);
	

}
function setupInterval() {
	setInterval(doLogic, 10)
}

addEventListener('load', setupInterval)