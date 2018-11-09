function addBusesToStop(stopid, buslist){ 

    let row = document.getElementById('stop' + stopid);

    for (var i = 0; i < buslist.length; i++) {
        let bus = buslist[i];
        cell = row.insertCell();
        cell.id = "bus" + bus.id;
        cell.className = 'min';
        cell.innerHTML = "<img src='images/bus-48.png' alt='bus'/>";
    }
    
    row = document.getElementById('stop' + stopid + '-desc');

    for (var i = 0; i < buslist.length; i++) {
        let bus = buslist[i];
        cell = row.insertCell();
        cell.id = "bus" + bus.id + '-desc';
        cell.className = 'min';
        cell.innerHTML = bus.status;
    }
}

var initdata = {
    stops: [
        {id:1, name:"Stop A"},
        {id:5, name:"Stop B"},
        {id:8, name:"Stop C"}

    ],
    buses: [
        {id:1, fromStop:1, route: "Route A", capacity: 20 },
        {id:2, fromStop:5, route: "Route B", capacity: 40 },
        {id:3, fromStop:5, route: "Route C", capacity: 70 },
    ]
}

// Load all data passed from the simulation service to UI objects
var engine = new Engine();

engine.kspeed = initdata.kspeed;
engine.kcapacity = initdata.kcapacity;
engine.kwaiting = initdata.kwaiting;
engine.kcombined = initdata.kcombined;

engine.routes = initdata.routes;

for (var j = 0; j < initdata.stops.length; j++) {
    let obj = initdata.stops[j];
    let stop =   new Stop(obj.id, obj.name, obj.waiting_count);
    engine.stoplist.push(stop);
}

for (var j = 0; j < initdata.buses.length; j++) {

    let obj = initdata.buses[j];

    let bus =  new Bus(obj.id, obj.currrent_stop_id, obj.arrival_time, obj.status, obj.rider_count, obj.route_id, obj.capacity, obj.speed);
    engine.buslist.push(bus);

    var current_stop = engine.stoplist.find(s => s.id === bus.currrent_stop_id)
    current_stop.buslist.push(bus)
}


var table = document.getElementById("maintable");


for (var j = 0; j < engine.stoplist.length; j++) {
    
    var stop = stoplist[j];

    var row = table.insertRow();
    row.id = "stop" + stop.id;
    row.stop = stop;

    var cell = row.insertCell();
    cell.className = 'min';
    cell.innerHTML="<img src='images/stop-48.png' alt='stop'/>";

    row = table.insertRow();
    row.id = "stop" + stop.id + "-desc";
    row.className = "desc-row"; // To leave space before next row

    cell = row.insertCell();
    cell.className = 'min';
    cell.innerHTML = "id: " + stop.id + "<br>name : " + stop.name + "<br>passengers : " + stop.waiting_count;

    addBusesToStop(stop.id, stop.buslist)

}

function movebus(busid, stop_id){
    
    let bus = engine.buslist.find(b => b.id === busid);
    let from_stop = engine.stoplist.find(s => s.id === bus.currrent_stop_id);
    let from_stop_index = fromStop.buslist.findIndex(b => b.id === bus.id);
    from_stop.buslist.splice(from_stop_index, 1); // remove bus from current stop object
    bus.currrent_stop_id = stopid;
    let next_stop = engine.stoplist.find(s => s.id === stop_id);
    next_stop.buslist.push(bus); // add bus to new stop object
    
    let node = document.getElementById('bus' + bus.id);
    node.parentNode.removeChild(node);
    node = document.getElementById('bus' + bus.id + '-desc');
    node.parentNode.removeChild(node);

    addBusesToStop(tostop.id, [bus]);

}




