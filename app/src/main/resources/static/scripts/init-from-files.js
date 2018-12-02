  // Get the modal
  let modal_files = $('#upload-files');

  // When the user clicks on the button, open the modal 
  $("#btn-initialize").click(function() {
    modal_files.show();
      
      // clear value from previous upload
      let file_input = $("#setupfileupload");
      file_input.replaceWith(file_input.val('').clone(true));
      file_input = $("#riderfileupload");
      file_input.replaceWith(file_input.val('').clone(true));
  
      $("#msg-file-upload").hide();
  });
  
  // When the user clicks on <span> (x), close the modal
  modal_files.find(".close").click(function() {
    modal_files.hide();
  });
  
  // When the user clicks anywhere outside of the modal, close it
  window.onclick = function(event) {
      if (event.target == modal_files) {
        modal_files.hide();
      }
  }
  
  modal_files.hide();

$('#btn-upload').on('click', function () {

  let setup_file = $("#setupfileupload").get(0).files[0];
  let rider_file = $("#riderfileupload").get(0).files[0];

  if (setup_file == null || setup_file.name == null){
    show_failure("please select a setup file");
    return;
  }

  if (rider_file == null || rider_file.name == null){
    show_failure("please select a rider file");
    return;
  }

  var form_data = new FormData();
  form_data.append('setupfile', setup_file);
  form_data.append('riderfile', rider_file);

  $.ajax({
      url: './upload',
      type: "POST",
      data: form_data,
      processData: false,
      contentType: false,
      async: false,
      cache: false,
      dataType: 'json'
    })
    .done(function (data) {
      if (data.success == 'true') {
        $('#upload-file').hide();
        load_initial_data(data.systemstate);
        draw_initial_ui();
      } else {
        show_failure();
      }
    })
    .fail(function (data) {
      show_failure();
    })
});

function show_failure(msg) {
  $('#msg-file-upload').html(msg ? msg : 'Upload failed');
  $('#msg-file-upload').show();
}