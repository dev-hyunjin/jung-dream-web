$(function() {

    $('tbody tr').on('click', function() {
        $.ajax({
            url: "/get-order",
            type: "post",
            data: { orderId : $(this).find('input[name=orderId]').val() },
            success: function(result) {
                if(result) {
                    $('input[name=ordererName]')
                    $('input[name=ordererPhone]')
                    $('input[name=receiverName]')
                    $('input[name=receiverPhone]')
                    $('input[name=postcode]')
                    $('input[name=address]')
                    $('input[name=addressDetail]')
                    $('select[name=kind]')
                    $('select[name=weight]')
                    $('select[name=size]')
                    $('input[name=amount]')
                }
            },
            error: function(e) {
                console.log(e);
            }
        });

        $('.modal').show();
    });
});