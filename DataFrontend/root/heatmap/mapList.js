let table
let thead
let tbody
function createTables(data) {
    if (data.status !== 200) {
        document.getElementById('body').innerHTML = 'Error connecting to backend'
        return
    }

    for (let i = 0; i < data.data.hitCount;i++) {
        let element = data.data.hits[i];
        let row = document.createElement('tr');

        let rowData_1 = document.createElement('td');
        rowData_1.innerHTML = '<a href="map.html?map=' + element.name + '">' + element.name + '</a>';

        row.appendChild(rowData_1);
        tbody.appendChild(row);
    }

}

function doLogic() {
    table = document.createElement('table');
    thead = document.createElement('thead');
    tbody = document.createElement('tbody');

    table.appendChild(thead);
    table.appendChild(tbody);

    // Adding the entire table to the body tag
    document.getElementById('body').appendChild(table);

    // Creating and adding data to first row of the table
    let tableHeadder = document.createElement('tr');
    let heading_1 = document.createElement('th');
    heading_1.innerHTML = "Map";

    tableHeadder.appendChild(heading_1);
    thead.appendChild(tableHeadder);


    fetch('http://localhost:8090/api/v1/map').then(response => response.json()).then(data => {
        console.log(data);
        createTables(data);
    }).catch(err => {
        document.getElementById('body').innerHTML = 'Error connecting to backend please try again later'
    });
}

addEventListener('load', doLogic)