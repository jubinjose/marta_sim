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

// Get the modal
let modal_sys = $('#update-sys');

// When the user clicks on the button, open the modal 
$("#btn-update-sys").click(function() {
    modal_sys.show();
    
    $("#kSysspeed").val(engine.kspeed);
    $("#kSyscapacity").val(engine.kcapacity);
    $("#kSyswaiting").val(engine.kwaiting);
    $("#kSysbuses").val(engine.kbuses);
    $("#kSyscombined").val(engine.kcombined);

    $("#msg-sys-change").hide();
});

// When the user clicks on <span> (x), close the modal
modal_sys.find('.close').click(function() {
    modal_sys.hide();
});

// When the user clicks anywhere outside of the modal, close it
window.onclick = function(event) {
    if (event.target == modal_sys) {
        modal_sys.hide();
    }
}

modal_sys.hide();

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
       modal_sys.hide();
});