$(function() {

    $('input[name=phoneNumber]').on('keyup', function() {
        $(this).val($(this).val().replace(/[^0-9]/gi, "").replace(/^(\d{2,3})(\d{3,4})(\d{4})$/, `$1-$2-$3`));
    });

    $(document).on('keyup', function(e) {
        if(e.keyCode == 13) {
            login();
        }
    });

    $('.loginok').on('click', function() {
        login();
    });

    function login() {
        const $ordererName = $('input[name=name]').val().trim();
        const $ordererPhone = $('input[name=phoneNumber]').val().trim();
        const $orderPassword = $('input[name=orderPassword]').val().trim();

        if(!$ordererName) {
            alert('이름을 입력하세요');
            return;
        }

        if(!$ordererPhone) {
            alert('전화번호를 입력하세요');
            return;
        }

        $.ajax({
            url: "/login",
            type: "post",
            data: { ordererName : $ordererName, ordererPhone : $ordererPhone, orderPassword : $orderPassword },
            success: function(result) {
                if(result) {
                    if(result.msg == 'adminSuccess') {
                        alert('관리자 계정입니다');
                        location.href = '/order-history';
                    } else if(result.msg == 'success') {
                        if(/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
                            alert('모바일로 접속 시 화면을 [가로]로 전환하여 확인부탁드립니다');
                        }
                        location.href = '/order-history';
                    } else {
                        alert(result.msg);
                    }
                }
            },
            error: function(e) {
                console.log(e);
            }
        });
    }
});