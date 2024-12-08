$(function() {

    $('.image-upload').on('change', function() {
        let $file = $(this)[0].files[0];
        let $input = $(this); // this를 저장

        let formData = new FormData(); //폼객체
        formData.append('file', $file);

        $.ajax({
            url: "/files/upload",
            method: "post",
            data: formData,
            contentType:false,
            processData:false,
            success: function(result) {
                $input.parents('.st-list').find('.uuid').val(result.uuids[0]);
                $input.parents('.st-list').find('.file-path').val(result.paths[0]);
            }
        });
    });

    $('.create').on('click', () => {
        $.ajax({
            url: "/admins/registration-file",
            method: "post",
            data: { fileUuid : $('.uuid').val() , filePath : $('.file-path').val() },
            success: function() {
                alert('사진 등록이 완료되었습니다.');
                location.reload();
            }
        });
    });
});