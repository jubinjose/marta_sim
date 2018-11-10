function load_initial_data(data){

    engine.kspeed = data.kspeed;
    engine.kcapacity = data.kcapacity;
    engine.kwaiting = data.kwaiting;
    engine.kbuses = data.kbuses;
    engine.kcombined = data.kcombined;
    engine.efficiency = data.efficiency;
    
    engine.routes = data.routes;

    for (var j = 0; j < data.stops.length; j++) {
        let obj = data.stops[j];
        let stop =   new Stop(obj.id, obj.name, obj.waiting);
        engine.stoplist.push(stop);
    }
    
    for (var j = 0; j < data.buses.length; j++) {
    
        let obj = data.buses[j];
    
        let bus =  new Bus(obj.id, obj.current_stop_id, obj.arrival_time, obj.status, obj.rider_count, obj.route_id, obj.capacity, obj.speed);
        engine.buslist.push(bus);
    
        var current_stop = engine.stoplist.find(s => s.id === bus.current_stop_id);
        current_stop.buslist.push(bus);
    }
}

function draw_system_ui(){
    
    $("#kspeed").text(engine.kspeed);
    $("#kcapacity").text(engine.kcapacity);
    $("#kwaiting").text(engine.kwaiting);
    $("#kcombined").text(engine.kcombined);
    $("#efficiency").text(engine.efficiency);

    var table = document.getElementById("maintable");
    
    
    for (var j = 0; j < engine.stoplist.length; j++) {
        
        var stop = engine.stoplist[j];
    
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
        cell.innerHTML = "id: " + stop.id + "<br>name : " + stop.name + "<br>waiting : " + stop.waiting_count;
    
        add_buses_to_stop(stop.id, stop.buslist)
    
    }
}

function add_buses_to_stop(stop_id, buslist){ 

    let row = document.getElementById('stop' + stop_id);

    for (var i = 0; i < buslist.length; i++) {
        let bus = buslist[i];
        cell = row.insertCell();
        cell.id = "bus" + bus.id;
        cell.className = 'min';
        cell.innerHTML = "<img src='images/bus-48.png' alt='bus'/>";
    }
    
    row = document.getElementById('stop' + stop_id + '-desc');

    for (var i = 0; i < buslist.length; i++) {
        let bus = buslist[i];
        cell = row.insertCell();
        cell.id = "bus" + bus.id + '-desc';
        cell.className = 'min';
        cell.innerHTML = bus.status;
    }
}

function move_bus(){

    $.get( "/movebus", function( data ) {
        if (data.move === true){
            
            let bus = engine.buslist.find(b => b.id === data.bus.id);

            let prev_stop = engine.stoplist.find(s => s.id === bus.current_stop_id);
            let prev_stop_bus_index = prev_stop.buslist.findIndex(b => b.id === bus.id);

            prev_stop.buslist.splice(prev_stop_bus_index, 1); // remove bus from current stop object

            bus.current_stop_id = data.stop.id;
            let stop = engine.stoplist.find(s => s.id === data.stop.id);

            stop.buslist.push(bus); // add bus to new stop object
            
            let node = document.getElementById('bus' + bus.id);
            node.parentNode.removeChild(node);
            node = document.getElementById('bus' + bus.id + '-desc');
            node.parentNode.removeChild(node);

            add_buses_to_stop(stop_id, [bus]);

            engine.efficiency = data.efficiency;
            $("#efficiency").text(engine.efficiency);
        }
    }, "json" );
}

var engine = new Engine();

$( document ).ready(function() {
    $.get( "/", function( data ) {

        console.log(data); 

        // Load all data passed from the simulation service to UI objects
        load_initial_data(data);
        
        // Draw the UI
        draw_system_ui();

        // Hookup move bus event handler
        $( "#btn-move-bus").click(function() {
            move_bus();
        });
        
    }, "json" );
});















