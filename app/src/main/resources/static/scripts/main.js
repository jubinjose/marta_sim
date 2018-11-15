
function load_initial_data(data){

    engine = new Engine();

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

function draw_initial_ui(){
    
    $("#kspeed").text(engine.kspeed);
    $("#kcapacity").text(engine.kcapacity);
    $("#kwaiting").text(engine.kwaiting);
    $("#kbuses").text(engine.kbuses);
    $("#kcombined").text(engine.kcombined);
    $("#efficiency").text(engine.efficiency);

    var table = document.getElementById("main-table");
    table.innerHTML = ""; // Clear any rows if present - need this when calling from Reset
    
    
    for (var j = 0; j < engine.stoplist.length; j++) {
        
        var stop = engine.stoplist[j];
    
        var row = table.insertRow();
        row.id = "stop-" + stop.id;
        row.stop = stop;
    
        var cell = row.insertCell();
        cell.className = 'min';
        cell.innerHTML="<img src='images/stop-48.png' alt='stop'/>";
    
        row = table.insertRow();
        row.id = "stop-" + stop.id + "-desc";
        row.className = "desc-row"; // To leave space before next row
    
        cell = row.insertCell();
        cell.className = 'min';
        cell.innerHTML = stop.get_display_info();
    
        add_buses_to_stop(stop.id, stop.buslist)
    
    }
}

function add_buses_to_stop(stop_id, buslist){ 

    let stop_row = document.getElementById('stop-' + stop_id);
    let stop_desc_row = document.getElementById('stop-' + stop_id + '-desc');

    for (var i = 0; i < buslist.length; i++) {
        let bus = buslist[i];

        let bus_cell = stop_row.insertCell();
        bus_cell.id = "bus-" + bus.id;
        bus_cell.className = 'min';
        bus_cell.innerHTML = "<img src='images/bus-48.png' alt='bus'/>";

        bus_desc_cell = stop_desc_row.insertCell();
        bus_desc_cell.id = "bus-" + bus.id + '-desc';
        bus_desc_cell.className = 'min';
        bus_desc_cell.innerHTML = bus.status;
    }
}

function move_bus(){

    $.get( "/movebus", function( data ) {
        if (data.move === true){
            
            // find bus that moved
            let bus = engine.buslist.find(b => b.id === data.bus.id);

            let prev_stop = engine.stoplist.find(s => s.id === bus.current_stop_id);

            // remove bus from current stop object's buslist
            let prev_stop_bus_index = prev_stop.buslist.findIndex(b => b.id === bus.id);
            prev_stop.buslist.splice(prev_stop_bus_index, 1); 

            // Update bus object with post move bus data from server
            bus.arrival_time = data.bus.arrival_time;
            bus.rider_count = data.bus.rider_count;
            bus.status = data.bus.status;
            bus.current_stop_id = data.bus.current_stop_id;

            // Find the stop bus reached
            let stop = engine.stoplist.find(s => s.id === data.stop.id);

            stop.waiting_count = data.stop.waiting;

            stop.buslist.push(bus); // add bus to the stop it reached
            
            // Remove bus cell and bus description cell from previous stop row
            let node = document.getElementById('bus-' + bus.id);
            node.parentNode.removeChild(node);
            node = document.getElementById('bus-' + bus.id + '-desc');
            node.parentNode.removeChild(node);

            // Add bus to new stop row
            add_buses_to_stop(data.stop.id, [bus]);

            $("#stop-" + data.stop.id + "-desc").find('td:first').html(stop.get_display_info());

            engine.efficiency = data.efficiency;
            $("#efficiency").text(engine.efficiency);
        }
    }, "json" );
}

function replay(){

    $.get( "/replay", function( data ) {
        alert("replay");
        if (data.move === true){
            
            // find bus that moved
            let bus = engine.buslist.find(b => b.id === data.bus.id);

            let prev_stop = engine.stoplist.find(s => s.id === bus.current_stop_id);

            // remove bus from current stop object's buslist
            let prev_stop_bus_index = prev_stop.buslist.findIndex(b => b.id === bus.id);
            prev_stop.buslist.splice(prev_stop_bus_index, 1); 

            // Update bus object with post move bus data from server
            bus.arrival_time = data.bus.arrival_time;
            bus.rider_count = data.bus.rider_count;
            bus.status = data.bus.status;
            bus.current_stop_id = data.bus.current_stop_id;

            // Find the stop bus reached
            let stop = engine.stoplist.find(s => s.id === data.stop.id);

            stop.waiting_count = data.stop.waiting;

            stop.buslist.push(bus); // add bus to the stop it reached
            
            // Remove bus cell and bus description cell from previous stop row
            let node = document.getElementById('bus-' + bus.id);
            node.parentNode.removeChild(node);
            node = document.getElementById('bus-' + bus.id + '-desc');
            node.parentNode.removeChild(node);

            // Add bus to new stop row
            add_buses_to_stop(data.stop.id, [bus]);

            $("#stop-" + data.stop.id + "-desc").find('td:first').html(stop.get_display_info());

            engine.efficiency = data.efficiency;
            $("#efficiency").text(engine.efficiency);
        }
    }, "json" );
}

var engine;

$( document ).ready(function() {
    $.get( "/", function( data ) {

        console.log(data); 

        // Load all data passed from the simulation service to UI objects
        load_initial_data(data);
        
        // Draw the UI
        draw_initial_ui();

        // Hookup move bus event handler
        $( "#btn-move-bus").click(function() {
            move_bus();
        });

        // Hookup reset bus event handler
        $( "#btn-reset-bus").click(function() {
            $.get( "/reset", function( data ) {
                load_initial_data(data);
                draw_initial_ui();
            }, "json");
        });

         // Hookup replay bus event handler
         $( "#btn-replay").click(function() {
            replay();
        });
        
    }, "json" );
});















