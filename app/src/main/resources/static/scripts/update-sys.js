// Get the modal
var modal = document.getElementById('update-sys');

// Get the button that opens the modal
var btn = document.getElementById("btn-update-sys");

// Get the <span> element that closes the modal
var span = document.getElementsByClassName("close")[0];

// When the user clicks on the button, open the modal 
btn.onclick = function() {
    modal.style.display = "block";
    document.getElementById("kSysspeed").value = engine.kspeed;
    document.getElementById("kSyscapacity").value = engine.kcapacity ;
    document.getElementById("kSyswaiting").value = engine.kwaiting;
    document.getElementById("kSysbuses").value = engine.kbuses;
    document.getElementById("kSyscombined").value = engine.kcombined;

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

function getKValue(data,area){
       var kvalue = document.getElementById(data).value;
       if ( !isNumber(kvalue) )
       {
            window.alert("Please enter value float value for "+area )
            return null ;
       }
       return kvalue ;
}

$("#btn-save").click(function() {

       var kspeedvalue = getKValue('kSysspeed','kspeed') ;
       if ( kspeedvalue == null ) return ;

       var kcapacityvalue = getKValue('kSyscapacity','kcapacity') ;
       if ( kcapacityvalue == null ) return ;

       var kwaitingvalue = getKValue('kSyswaiting','kwaiting') ;
       if ( kwaitingvalue ==null ) return ;

       var kbusesvalue = getKValue('kSysbuses','kbuses') ;
       if ( kbusesvalue == null ) return ;

       var kcombinedvalue = getKValue('kSyscombined','kcombined') ;
       if ( kcombinedvalue == null ) return ;

       //console.log("Save:");
       $.post("/ksave",
       {
          kspeed: kspeedvalue,
          kcapacity: kcapacityvalue,
          kwaiting: kwaitingvalue ,
          kbuses: kbusesvalue ,
          kcombined: kcombinedvalue
        },
       function(data, status){
           //alert("Data: " + data + "\nStatus: " + status);
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
});

$("#btn-close").click(function() {
       //console.log("Close:");
       var modal = document.getElementById('update-sys');
       modal.style.display = "none";
});