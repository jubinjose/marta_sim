// Get the modal
var modal = document.getElementById('update-sys');

// Get the button that opens the modal
var btn = document.getElementById("btn-update-sys");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

// When the user clicks on the button, open the modal 
btn.onclick = function() {
    modal.style.display = "block";
    
    $("#kSysspeed").val(engine.kspeed);
    $("#kSyscapacity").val(engine.kcapacity);
    $("#kSyswaiting").val(engine.kwaiting);
    $("#kSysbuses").val(engine.kbuses);
    $("#kSyscombined").val(engine.kcombined);

    $("#msg-sys-change").hide();
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

function validateNumber(num){
       if ( !isNumber(num) )
       {
            return {"error":"Please enter a numeric value for "};
       }
       else{
           return {};
       }
}

$("#btn-save").click(function() {

    let errors = [];

    let kspeedvalue = $("#kSysspeed").val();
    let validation_result = validateNumber(kspeedvalue);
    if (validation_result.error) {
        errors.push(validation_result.error + 'kspeed');
    }

    let kcapacityvalue = $("#kSyscapacity").val();
    validation_result = validateNumber(kcapacityvalue);
    if (validation_result.error) {
        errors.push(validation_result.error + 'kcapacity');
    }

    let kwaitingvalue = $("#kSyswaiting").val();
    validation_result = validateNumber(kwaitingvalue);
    if (validation_result.error) {
        errors.push(validation_result.error + 'kwaiting');
    }

    let kbusesvalue = $("#kSysbuses").val();
    validation_result = validateNumber(kbusesvalue);
    if (validation_result.error) {
        errors.push(validation_result.error + 'kbuses');
    }

    let kcombinedvalue = $("#kSyscombined").val();
    validation_result = validateNumber(kcombinedvalue);
    if (validation_result.error) {
        errors.push(validation_result.error + 'kcombined');
    }

    if (errors.length >0){
        
        let str = "";
        errors.forEach(function(error){
            str += '<li>' + error + '</li>' 
        });

        $('#msg-sys-change').html('<p>There were some errors</p>')
                .append('<ul>' + str + '</ul>'); 

        $('#msg-sys-change').removeClass('msg-success');
        $('#msg-sys-change').addClass('msg-fail');
        $('#msg-sys-change').show();
        return;
    }

    $.post("/ksave",
    {
        kspeed: kspeedvalue,
        kcapacity: kcapacityvalue,
        kwaiting: kwaitingvalue ,
        kbuses: kbusesvalue ,
        kcombined: kcombinedvalue
    },
    function(data, status){
        $("#efficiency").text(data);
    });

    engine.kspeed = kspeedvalue ;
    engine.kcapacity = kcapacityvalue ;
    engine.kwaiting = kwaitingvalue ;
    engine.kbuses = kbusesvalue ;
    engine.kcombined = kcombinedvalue;

    $("#kspeed").text(engine.kspeed);
    $("#kcapacity").text(engine.kcapacity);
    $("#kwaiting").text(engine.kwaiting);
    $("#kbuses").text(engine.kbuses);
    $("#kcombined").text(engine.kcombined);

    $('#msg-sys-change').removeClass('msg-fail');
    $('#msg-sys-change').addClass('msg-success');
    $("#msg-sys-change").text('System update successful');
    $("#msg-sys-change").show();
});

$("#btn-close").click(function() {
       var modal = document.getElementById('update-sys');
       modal.style.display = "none";
});