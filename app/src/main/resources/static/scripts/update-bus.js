function isPositiveInteger(n) {
    return $.isNumeric(n) && Math.floor(n) == +n && n>=0;
}

function load_bus_details(bus_id) {

    var bus = engine.get_bus(bus_id);

    $("#busspeed").val(bus.speed);
    $("#buscapacity").val(bus.capacity);

    if (engine.routelist.length > 0) {

        var $dropdown = $("#allroutes");
        $dropdown.empty();
        console.log(engine.routelist);
        $.each(engine.routelist, function () {
            console.log(engine.get_route(this.id))
            $dropdown.append($("<option />").val(this.id).text(this.name + ' [' + this.id + ']'));
        });

        load_stops();

        $("#msg-bus-change").hide();
        bus_modal.style.display = "block";
    }
}

function load_stops(){
    let dropdown = $("#routestops");
    dropdown.empty();

    let stops = engine.get_route($("#allroutes").val()).stops.map(x => engine.get_stop(x));
    for (var j = 0; j < stops.length; j++) {
        let stop = stops[j];
        dropdown.append($("<option />").val(j).text(stop.name + ' [' + stop.id + ']'));
    }
}



// Get the modal
var bus_modal = document.getElementById('update-bus');

// Get the button that opens the modal
var bus_btn = document.getElementById("btn-update-bus");

// Get the <span> element that closes the modal
var bus_span = document.getElementById("close-bus");
//var span = document.getElementsByClassName("close-bus")[0];

// uncheck by default 
$('#changeroute').prop('checked', false); 

var route_stops = {};
var selectedBusId;

// When the user clicks on the button, open the modal 
bus_btn.onclick = function () {
    load_bus_details($("#selectBus").val());
    $("#allroutesrow").toggle(false);
    $("#routestoprow").toggle(false);
}

// When the user clicks on <span> (x), close the modal
bus_span.onclick = function () {
    bus_modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
    if (event.target == bus_modal) {
        bus_modal.style.display = "none";
    }
}


bus_modal.style.display = "none";

$("#btn-update").click(function () {

    let selectedBusId = $("#selectBus").val();
    let updatedSpeed = $("#busspeed").val().trim();
    let updatedCapacity = $("#buscapacity").val().trim();
    let updatedRouteId = $("#allroutes").val();
    let updatedRouteStopId = $("#routestops").val();
    let routechanged = $("#changeroute").is(':checked') ? 1 : 0;

    let errors = [];

    if (!isPositiveInteger(updatedSpeed)){
        errors.push('Speed has to be a non negative integer');
    }

    if (!isPositiveInteger(updatedCapacity)){
        errors.push('Capacity has to be a non negative integer');
    }

    if (errors.length >0){
        
        let str = "";
        errors.forEach(function(error){
            str += '<li>' + error + '</li>' 
        });

        $('#msg-bus-change').html('<p>There were some errors</p>')
                .append('<ul>' + str + '</ul>'); 

        $('#msg-bus-change').removeClass('msg-success');
        $('#msg-bus-change').addClass('msg-fail');
        $('#msg-bus-change').show();
        return;
    }

    var postdata = {};
    postdata.busid = selectedBusId;
    postdata.speed = updatedSpeed;
    postdata.capacity = updatedCapacity;
    postdata.routechanged = routechanged;

    if (routechanged == 1) {
        postdata.route = updatedRouteId;
        postdata.stopindex = updatedRouteStopId;
    }

    $.post("/changebus", postdata)
        .done(function (data) {
            $('#msg-bus-change').removeClass('msg-fail');
            $('#msg-bus-change').addClass('msg-success');
            $("#msg-bus-change").text('Bus update successful');
            $("#msg-bus-change").show();
            // uncheck it  
            $('#changeroute').prop('checked', false); 
            // close the modal 
        });
});

$("#btn-close-bus").click(function () {    
    // uncheck it  
    $('#changeroute').prop('checked', false); 
    var bus_modal = document.getElementById('update-bus');
    bus_modal.style.display = "none";
});

$('#changeroute').click(function () {
    $("#allroutesrow").toggle(this.checked);
    $("#routestoprow").toggle(this.checked);
});

$("#allroutes").change(function () {
    load_stops();
});