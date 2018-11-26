// Get the modal
var modal = document.getElementById('update-bus');

// Get the button that opens the modal
var btn = document.getElementById("btn-update-bus");

// Get the <span> element that closes the modal
var span = document.getElementById("close-bus");
//var span = document.getElementsByClassName("close-bus")[0];

// When the user clicks on the button, open the modal 
btn.onclick = function() {
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
        modal.style.display = "block";
        document.getElementById("busspeed").value = data.speed;
        document.getElementById("buscapacity").value = data.capacity;
        document.getElementById("busroute").value = data.routeid;
        document.getElementById("busstop").value = data.nextstopindex;
        
    }, "json" );
}

// When the user clicks on <span> (x), close the modal
span.onclick = function() {
    modal.style.display = "none";
}

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal) {
        modal.style.display = "none";
    }
}

modal.style.display = "none";

function isNumber(n) {
  return !isNaN(parseFloat(n)) && isFinite(n);
}

$("#btn-update").click(function() {
       alert("Save");
});

$("#btn-close-bus").click(function() {
       //console.log("Close:");
       var modal = document.getElementById('update-bus');
       modal.style.display = "none";
});