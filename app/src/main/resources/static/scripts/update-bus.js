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
        modal_bus.show();
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
let modal_bus = $('#update-bus');

// When the user clicks on the button, open the modal 
$('#btn-update-bus').click(function () {
    load_bus_details($("#selectBus").val());
    $("#allroutesrow").toggle(false);
    $("#routestoprow").toggle(false);
});

function close_bus_modal(){
    $('#changeroute').prop('checked', false); 
    modal_bus.hide();
}

// When the user clicks on <span> (x), close the modal
modal_bus.find('.close').click(function () {
    close_bus_modal();
});

// When the user clicks anywhere outside of the modal, close it
window.onclick = function (event) {
    if (event.target == modal_bus) {
        close_bus_modal();
    }
}

$("#btn-close-bus").click(function () {    
    close_bus_modal();
});

// uncheck by default 
$('#changeroute').prop('checked', false); 

var route_stops = {};
var selectedBusId;

modal_bus.hide();

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



$('#changeroute').click(function () {
    $("#allroutesrow").toggle(this.checked);
    $("#routestoprow").toggle(this.checked);
});

$("#allroutes").change(function () {
    load_stops();
});