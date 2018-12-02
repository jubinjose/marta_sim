
function load_initial_data(data){

    engine = new Engine();

    engine.kspeed = data.kspeed;
    engine.kcapacity = data.kcapacity;
    engine.kwaiting = data.kwaiting;
    engine.kbuses = data.kbuses;
    engine.kcombined = data.kcombined;
    engine.efficiency = data.efficiency;
    engine.num_rewinds_possible = data.num_rewinds_possible;
    
    engine.routelist = data.routes;

    for (var j = 0; j < data.stops.length; j++) {

        let obj = data.stops[j];
        let stop =   new Stop(obj.id, obj.name, obj.waiting);
        engine.stoplist.push(stop);

    }
    
    for (var j = 0; j < data.buses.length; j++) {
    
        let obj = data.buses[j];
    
        let bus =  new Bus(obj.id, obj.current_stop_id, obj.arrival_time, obj.status, obj.rider_count, obj.route_id, obj.capacity, obj.speed);
        engine.buslist.push(bus);
    
        var current_stop = engine.get_stop(bus.current_stop_id);
        current_stop.buslist.push(bus);
    }


    this.load_bus_dropdown(data.buses);
}

function update_efficiency_rewind(){
    
    $("#efficiency").text(engine.efficiency);

    let rewind_count_span = $("#rewinds-possible");
    let btn_rewind = $("#btn-rewind");

    if (engine.num_rewinds_possible == 0){
        btn_rewind.prop("disabled", true);
        rewind_count_span.hide();
    }
    else{
        btn_rewind.prop("disabled", false);
        rewind_count_span.show();
        rewind_count_span.text(engine.num_rewinds_possible + ' remaining');
    }
}

function load_bus_dropdown(buses) {
    var options = []
    for (var j = 0; j < buses.length; j++) {
        options.push(buses[j].id)
    }
    
    $('#selectBus').empty();
    $.each(options, function(i, p) {
        $('#selectBus').append($('<option></option>').val(p).html(p));
    });
}

function draw_initial_ui(){
    
    $("#kspeed").text(engine.kspeed);
    $("#kcapacity").text(engine.kcapacity);
    $("#kwaiting").text(engine.kwaiting);
    $("#kbuses").text(engine.kbuses);
    $("#kcombined").text(engine.kcombined);

    update_efficiency_rewind();
    
    var table = document.getElementById("main-table");
    table.innerHTML = ""; // Clear any rows if present - need this when calling from Reset
    
    
    for (var j = 0; j < engine.stoplist.length; j++) {
        
        var stop = engine.stoplist[j];
    
        var row = table.insertRow();
        row.id = "stop-" + stop.id;
        row.className = "image-row"; // To leave space before next row
    
        var cell = row.insertCell();
        cell.className = 'stop';
        cell.innerHTML="<img src='images/stop-48.png' alt='stop'/>";
    
        row = table.insertRow();
        row.id = "stop-" + stop.id + "-desc";
        row.className = "desc-row"; // To leave space before next row
    
        cell = row.insertCell();
        cell.className = 'stop';
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
            let bus = engine.get_bus(data.bus.id);

            let prev_stop = engine.get_stop(bus.current_stop_id);

            // remove bus from previous stop object's buslist
            let prev_stop_bus_index = prev_stop.buslist.findIndex(b => b.id === bus.id);
            prev_stop.buslist.splice(prev_stop_bus_index, 1); 

            // Also update UI to remove bus cell and bus description cell from previous stop row
                        
            let node = document.getElementById('bus-' + bus.id);
            node.parentNode.removeChild(node);
            node = document.getElementById('bus-' + bus.id + '-desc');
            node.parentNode.removeChild(node);

            // Update front end bus object with post move data from server
            bus.arrival_time = data.bus.arrival_time;
            bus.rider_count = data.bus.rider_count;
            bus.status = data.bus.status;
            bus.current_stop_id = data.bus.current_stop_id;
            bus.capacity = data.bus.capacity;
            bus.speed = data.bus.speed;

            // Find the stop bus reached
            let stop = engine.get_stop(bus.current_stop_id);

            stop.waiting_count = data.waiting_at_stop;
            stop.buslist.push(bus); // add bus to the stop it reached

            // UI update yo show new waiting count for the stop
            $("#stop-" + stop.id + "-desc").find('td:first').html(stop.get_display_info());
            
            add_buses_to_stop(stop.id, [bus]); // UI update to add bus to new stop row

            engine.efficiency = data.efficiency;
            engine.num_rewinds_possible = data.num_rewinds_possible;
            update_efficiency_rewind();
        }
       

    }, "json" );
}

function rewind(){

    $.get( "/rewind/1", function( data ) {
        if (data.rewinds.length==0) return; // No events were rewound

        for (var i = 0; i < data.rewinds.length; i++) {
            let rewind_result = data.rewinds[i];

            // Find bus that moved
            let bus = engine.get_bus(rewind_result.bus.id);

            // Find the stop bus was at before the rewind
            let stop_to_remove_bus = engine.get_stop(bus.current_stop_id); // or rewind_result.stop.id since bus.current_stop_id should be same as bus.current_stop_id as long as UI stayed in sync
            // Find the stop where bus has to be taken back to
            let stop_to_add_bus = engine.get_stop(rewind_result.bus.current_stop_id);

             
            // update stop with waiting count
            stop_to_remove_bus.waiting_count = rewind_result.stop.waiting;

            // remove bus from stop object's buslist
            let bus_index = stop_to_remove_bus.buslist.findIndex(b => b.id === bus.id);
            stop_to_remove_bus.buslist.splice(bus_index, 1); 

            // add bus to the stop
            stop_to_add_bus.buslist.push(bus); 

            // Update bus object with pre event data
            bus.arrival_time = rewind_result.bus.arrival_time;
            bus.rider_count = rewind_result.bus.rider_count;
            bus.status = rewind_result.bus.status;
            bus.current_stop_id = stop_to_add_bus.id;

            // Remove bus cell and bus description cell from previous stop row
            let node = document.getElementById('bus-' + bus.id);
            node.parentNode.removeChild(node);
            node = document.getElementById('bus-' + bus.id + '-desc');
            node.parentNode.removeChild(node);

            // Add bus to new stop row
            add_buses_to_stop(stop_to_add_bus.id, [bus]);

            $("#stop-" + stop_to_remove_bus.id + "-desc").find('td:first').html(stop_to_remove_bus.get_display_info());

            engine.efficiency = data.efficiency;
            engine.num_rewinds_possible = data.num_rewinds_possible;
            update_efficiency_rewind();
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
        $("#btn-move-bus").click(function() {
            move_bus();
        });

        // Hookup rewind bus event handler
        $("#btn-rewind").click(function() {
            rewind();
        }); 

        // Hookup reset bus event handler
        $("#btn-reset").click(function() {
            $.get( "/reset", function( data ) {
                load_initial_data(data);
                draw_initial_ui();
            }, "json");
        });

        // // Hookup upload files event handler
        // $("#btn-initialize").click(function() {
        //     window.location.replace("./upload.html");
        // });
    }, "json" );
});
















