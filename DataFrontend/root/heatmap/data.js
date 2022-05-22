let json;

function httpGetI(theUrl) {
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
	httpGetI('http://localhost:8090/api/v1/matchstate/map/mp_forwardbase_kodai');
}


addEventListener('load', doLogic)