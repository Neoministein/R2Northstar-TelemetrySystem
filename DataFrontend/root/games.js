function createTables(data) {
    if (data.status !== 200) {
        document.getElementById('body').innerHTML = 'Error connecting to backend'
        return
    }
    let table = document.createElement('table');
    let thead = document.createElement('thead');
    let tbody = document.createElement('tbody');

    table.appendChild(thead);
    table.appendChild(tbody);

    // Adding the entire table to the body tag
    document.getElementById('body').appendChild(table);

    // Creating and adding data to first row of the table
    let tableHeadder = document.createElement('tr');
    let heading_1 = document.createElement('th');
    heading_1.innerHTML = "Ns Server Name";
    let heading_2 = document.createElement('th');
    heading_2.innerHTML = "Map";
    let heading_3 = document.createElement('th');
    heading_3.innerHTML = "Gamemode";
    let heading_4 = document.createElement('th');
    heading_4.innerHTML = "Start date";

    tableHeadder.appendChild(heading_1);
    tableHeadder.appendChild(heading_2);
    tableHeadder.appendChild(heading_3);
    tableHeadder.appendChild(heading_4);
    thead.appendChild(tableHeadder);

    for (let i = 0; i < data.data.hitCount;i++) {
        let element = data.data.hits[i];
        let row = document.createElement('tr');

        let rowData_1 = document.createElement('td');
        rowData_1.innerHTML = '<a href="/game.html?id=' + element.id +'&map=' + element.map + '">' + element.nsServerName + '</a>';
        let rowData_2 = document.createElement('td');
        rowData_2.innerHTML = element.map;
        let rowData_3 = document.createElement('td');
        rowData_3.innerHTML = element.gamemode;
        let rowData_4 = document.createElement('td');
        rowData_4.innerHTML = new Date(element.startDate).toLocaleDateString('de-DE', { year:"numeric", month:"numeric", day:"numeric", hour:"numeric", minute:"numeric"})

        row.appendChild(rowData_1);
        row.appendChild(rowData_2);
        row.appendChild(rowData_3);
        row.appendChild(rowData_4);
        tbody.appendChild(row);
    }

}

function doLogic() {
    fetch('http://localhost:8090/api/v1/match/playing').then(response => response.json()).then(data => {
        console.log(data);
        createTables(data);
    });
}

addEventListener('load', doLogic)