// Get the modal
var bus_modal = document.getElementById('update-bus');

// Get the button that opens the modal
var bus_btn = document.getElementById("btn-update-bus");

// Get the <span> element that closes the modal
var bus_span = document.getElementById("close-bus");
//var span = document.getElementsByClassName("close-bus")[0];

// When the user clicks on the button, open the modal 
bus_btn.onclick = function() {
    let selectedBusId = $("#selectBus").val();
    load_routes_dropdown();
    get_bus(selectedBusId);
}

function load_routes_dropdown(routes) {
    var options = [1, 2, 3];
    //for (var j = 0; j < routes.length; j++) {
    //    options.push(buses[j].id)
    //}
    
    $('#allroutes').empty();
    $.each(options, function(i, p) {
        $('#allroutes').append($('<option></option>').val(p).html(p));
    });
}

function get_bus(bus_id){
    $.get( "/getbus/" + bus_id, function( data ) {
        bus_modal.style.display = "block";
        document.getElementById("busspeed").value = data.speed;
        document.getElementById("buscapacity").value = data.capacity;
        document.getElementById("busroute").value = data.routeid;
        document.getElementById("busstop").value = data.nextstopindex;
        
    }, "json" );
}

// When the user clicks on <span> (x), close the modal
bus_span.onclick = function() {
    modal.style.display = "none";
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
       alert("Save");
});

$("#btn-close-bus").click(function() {
       //console.log("Close:");
       var bus_modal = document.getElementById('update-bus');
       bus_modal.style.display = "none";
});