const params = new Proxy(new URLSearchParams(window.location.search), {
    get: (searchParams, prop) => searchParams.get(prop),
});


let json;

function httpGetI(theUrl) {
    fetch(theUrl, {
        method: "GET",
    }).then(function(response) {
        return response.json();
    }).then(function(response) {
        if(response.status === 200) {
            if (response.data.hitCount > 0) {
                json = response.data.hits[0];
                return
            } else {
                document.getElementById('body').innerHTML = 'No heatmap found'
            }
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