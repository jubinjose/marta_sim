// Get the modal
var bus_modal = document.getElementById('update-bus');

// Get the button that opens the modal
var bus_btn = document.getElementById("btn-update-bus");

// Get the <span> element that closes the modal
var bus_span = document.getElementById("close-bus");
//var span = document.getElementsByClassName("close-bus")[0];

var route_stops = {}; 
var selectedBusId;

// When the user clicks on the button, open the modal 
bus_btn.onclick = function() {
    selectedBusId = $("#selectBus").val();
    get_bus(selectedBusId);
    $("#allroutesrow").toggle(false); 
    $("#routestoprow").toggle(false);   
}

function get_bus(bus_id){
    $.get( "/getbus/" + bus_id, function( data ) {
       
        bus_modal.style.display = "block";
        document.getElementById("busspeed").value = data.speed;
        document.getElementById("buscapacity").value = data.capacity;
        //document.getElementById("busroute").value = data.routeid;
        //document.getElementById("busstop").value = data.nextstopindex;

        var options_route = [];
        
        for (var i = 0; i < data.routes.length; i++) {
            options_route.push(data.routes[i].id);
            route_stops[data.routes[i].id] = data.routes[i].stops;
        }
        $('#allroutes').empty();
        $.each(options_route, function(i, p) {
            $('#allroutes').append($('<option></option>').val(p).html(p));
        });

        // a bit hacky but should  work 
        let options_firstroute_stops = route_stops[data.routes[0].id];
        $('#routestops').empty();
        $.each(options_firstroute_stops, function(i, p) {
            $('#routestops').append($('<option></option>').val(p).html(p));
        });
    }, "json" );
}

// When the user clicks on <span> (x), close the modal
bus_span.onclick = function() {
    bus_modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == bus_modal) {
        bus_modal.style.display = "none";
    }
}

bus_modal.style.display = "none";

function isNumber(n) {
  return !isNaN(parseFloat(n)) && isFinite(n);
}

$("#btn-update").click(function() {

    let updatedSpeed = document.getElementById("busspeed").value;
    let updatedCapacity = document.getElementById("buscapacity").value;
    let updatedRouteId = $("#allroutes").val();
    let updatedRouteStopId = $("#routestops").val();
    let routechanged = $("#changeroute").is(':checked') ? 1 : 0;
    
    var postdata = {};
    postdata.busid = selectedBusId;
    postdata.speed = updatedSpeed;
    postdata.capacity = updatedCapacity;
    postdata.routechanged = routechanged;

    if(routechanged == 1){
        postdata.route = updatedRouteId;
        postdata.stopindex = updatedRouteStopId;
    }

    $.post("/changebus", postdata)
    .done(function( data ) {
        console.log("post success");
    });
       
});

$("#btn-close-bus").click(function() {
       //console.log("Close:");
       var bus_modal = document.getElementById('update-bus');
       bus_modal.style.display = "none";
});

$('#changeroute').click(function() {
    $("#allroutesrow").toggle(this.checked);
    $("#routestoprow").toggle(this.checked);
});

$("#allroutes").change(function() {
    let selectedRouteId = $("#allroutes").val();
    options_route_stops = route_stops[selectedRouteId];
    $('#routestops').empty();
    $.each(options_route_stops, function(i, p) {
        $('#routestops').append($('<option></option>').val(p).html(p));
    });
}).change();
