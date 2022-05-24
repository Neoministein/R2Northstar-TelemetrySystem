const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});


let json;

function httpGetI(theUrl) {
    fetch(theUrl, {
        method: "GET",
    }).then(function(response) {
        return response.json();
    }).then(function(data) {
        if(data.status == 200) {
        json = data.data;
        } else {
            document.getElementById('body').innerHTML = 'Invalid map'
        }
    }).catch(function(err) {
        console.log(err);
});
}

function doLogic() {
	httpGetI('http://localhost:8090/api/v1/map/heatmap/' + params.map);
}


addEventListener('load', doLogic)