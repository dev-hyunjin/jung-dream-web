$(function() {

    productInfoKinds.forEach(productKind => {
        let text = `<option value="${productKind.productKind}"`
            + (productKind.productSoldOut == 'Y' ? `disabled` : ``) +
            `>${productKind.productKind}` + (productKind.productSoldOut == 'Y' ? `(품절)` : ``) + `</option>`;
        $('select[name=kind]').append(text);
    });

    $(document).on('click', '.close-container span', function() {
        $('.modal').hide();
    });

    $(document).on('keyup', 'input[name=ordererPhone], input[name=receiverPhone], input[name=deliveryPhone]', function() {
        $(this).val($(this).val().replace(/[^0-9]/gi, "").replace(/^(\d{2,3})(\d{3,4})(\d{4})$/, `$1-$2-$3`));
    });

    $(document).on('change', 'select[name=kind]', function() {
        getProductInfo(this, $(this).val(), null);
    });

    $(document).on('change', 'select[name=weight]', function() {
        getProductInfo(this, $(this).closest('.receive-information-container').find('select[name=kind]').val(), $(this).val());
    });

    $(document).on('click', '.find-address', function() {
        kakaoAddress(this);
    });

    $(document).on('click', '#same-person', function() {
        if($(this).prop('checked')) {
            $(this).closest('.receive-information-container').find('input[name=receiverName]').val($('input[name=ordererName]').val());
            $(this).closest('.receive-information-container').find('input[name=receiverPhone]').val($('input[name=ordererPhone]').val());
        }
    });

    $(document).on('click', '.plus', function() {
        $(this).prev().val(parseInt($(this).prev().val()) + 1);
    });

    $(document).on('click', '.minus', function() {
        if($(this).next().val() == 1) {
            return;
        }

        $(this).next().val(parseInt($(this).next().val()) - 1);
    });

    function getProductInfo(productKind, productWeight) {
        $.ajax({
            url: "/product-info",
            type: "post",
            data: { isFirst : (!productWeight ? 2 : 3), productKind : productKind, productWeight : productWeight},
            async: false,
            success: function(result) {
                if(result) {
                    if(!productWeight) {
                        const selectBox = $('select[name=weight]');

                        $('select[name=size]').html(`<option value="">규격을 골라주세요</option>`);

                        selectBox.html(`<option value="">kg을 골라주세요</option>`);

                        result.forEach(data => {
                            let text = `<option value="${data.productWeight}"`
                                + (data.productSoldOut == 'Y' ? `disabled` : ``) +
                                `>${data.productWeight}` + (data.productSoldOut == 'Y' ? `(품절)` : ``) + `</option>`;
                            selectBox.append(text);
                        });
                    } else {
                        const selectBox = $('select[name=size]');

                        selectBox.html(`<option value="">규격을 골라주세요</option>`);
                        result.forEach(data => {
                            let text = `<option value="${data.productSize}"`
                                + (data.productSoldOut == 'Y' ? `disabled` : ``) +
                                `>${data.productSize}` + (data.productSoldOut == 'Y' ? `(품절)` : ``) + `</option>`;
                            selectBox.append(text);
                        });
                    }
                }
            }
        });
    }

    $('tbody tr').on('click', function(e) {
        if ($(e.target).closest('.order-cancel').length > 0) {
            return;
        }

        $.ajax({
            url: "/get-order",
            type: "post",
            data: { orderId : $(this).find('input[name=orderId]').val() },
            async: false,
            success: function(result) {
                if(result) {
                    $('input[name=orderId]').val(result.orderId);
                    $('input[name=ordererName]').val(result.orderOrdererName);
                    $('input[name=ordererPhone]').val(result.orderOrdererPhone);

                    if(result.orderDeliveryName && result.orderDeliveryPhone) {
                        $('input[name=deliveryName]').val(result.orderDeliveryName);
                        $('input[name=deliveryPhone]').val(result.orderDeliveryPhone);
                    } else {
                        $('.delivery-container').empty();
                    }

                    $('input[name=receiverName]').val(result.orderReceiverName);
                    $('input[name=receiverPhone]').val(result.orderReceiverPhone);
                    $('input[name=postcode]').val(result.orderPostcode);
                    $('input[name=address]').val(result.orderAddress);
                    $('input[name=addressDetail]').val(result.orderAddressDetail);

                    $('select[name=kind]').val(result.orderKind).prop('selected', true);

                    getProductInfo(result.orderKind, null);
                    $('select[name=weight]').val(result.orderWeight).prop('selected', true);

                    getProductInfo(result.orderKind, result.orderWeight);
                    $('select[name=size]').val(result.orderSize).prop('selected', true);

                    $('input[name=amount]').val(result.orderCount);
                }
            },
            error: function(e) {
                console.log(e);
            }
        });

        $('.modal').show();
    });

    // 카카오 주소 API
    function kakaoAddress(e) {
        new daum.Postcode({
            oncomplete: function(data) {
                // 팝업에서 검색결과 항목을 클릭했을때 실행할 코드를 작성하는 부분.

                // 각 주소의 노출 규칙에 따라 주소를 조합한다.
                // 내려오는 변수가 값이 없는 경우엔 공백('')값을 가지므로, 이를 참고하여 분기 한다.
                var addr = ''; // 주소 변수
                var extraAddr = ''; // 참고항목 변수

                //사용자가 선택한 주소 타입에 따라 해당 주소 값을 가져온다.
                if (data.userSelectedType === 'R') { // 사용자가 도로명 주소를 선택했을 경우
                    addr = data.roadAddress;
                } else { // 사용자가 지번 주소를 선택했을 경우(J)
                    addr = data.jibunAddress;
                }

                // 사용자가 선택한 주소가 도로명 타입일때 참고항목을 조합한다.
                if(data.userSelectedType === 'R'){
                    // 법정동명이 있을 경우 추가한다. (법정리는 제외)
                    // 법정동의 경우 마지막 문자가 "동/로/가"로 끝난다.
                    if(data.bname !== '' && /[동|로|가]$/g.test(data.bname)){
                        extraAddr += data.bname;
                    }
                    // 건물명이 있고, 공동주택일 경우 추가한다.
                    if(data.buildingName !== '' && data.apartment === 'Y'){
                        extraAddr += (extraAddr !== '' ? ', ' + data.buildingName : data.buildingName);
                    }
                    // 표시할 참고항목이 있을 경우, 괄호까지 추가한 최종 문자열을 만든다.
                    if(extraAddr !== ''){
                        extraAddr = ' (' + extraAddr + ')';
                    }
                    // 조합된 참고항목을 해당 필드에 넣는다.
                    // document.getElementById("sample6_extraAddress").value = extraAddr;

                } else {
                    // document.getElementById("sample6_extraAddress").value = '';
                }

                // 우편번호와 주소 정보를 해당 필드에 넣는다.
                $(e).closest('.form-group').find('.postcode').val(data.zonecode);
                $(e).closest('.form-group').find('input[name=address]').val(addr + ' ' + extraAddr);
                // 커서를 상세주소 필드로 이동한다.
                $(e).closest('.form-group').find('input[name=addressDetail]').focus();
            }
        }).open();
    }

    /* 수정 버튼 클릭 */
    $('.submit-button').on('click', () => {
        if(!$('input[name=ordererName]').val()) {
            alert('주문자 이름을 입력하세요');
            $('input[name=ordererName]').focus();
            return;
        }

        if(!$('input[name=ordererPhone]').val()) {
            alert('주문자 전화번호를 입력하세요');
            $('input[name=ordererPhone]').focus();
            return;
        }

        let orderDTOS = [];

        if($('input[name=deliveryName]').length > 0) {
            for (let i = 0; i < $('input[name=deliveryName]').length; i++) {
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }

                let deliveryName = ($('input[name=deliveryName]').eq(i).val() ? $('input[name=deliveryName]').eq(i).val() : '정재하');
                orderDTOS[i]["orderDeliveryName"] = deliveryName.trim();
            }
        }

        if($('input[name=deliveryPhone]').length > 0) {
            for (let i = 0; i < $('input[name=deliveryPhone]').length; i++) {
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }

                let deliveryPhone = ($('input[name=deliveryPhone]').eq(i).val() ? $('input[name=deliveryPhone]').eq(i).val() : '010-4532-4350');
                orderDTOS[i]["orderDeliveryPhone"] = deliveryPhone.trim();
            }
        }

        if($('input[name=receiverName]').length > 0) {
            for (let i = 0; i < $('input[name=receiverName]').length; i++) {
                if(!$('input[name=receiverName]').eq(i).val()) {
                    alert('수신자 이름을 입력하세요');
                    $('input[name=receiverName]').eq(i).focus();
                    return;
                }
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderReceiverName"] = $('input[name=receiverName]').eq(i).val();
            }
        }

        if($('input[name=receiverPhone]').length > 0) {
            for (let i = 0; i < $('input[name=receiverPhone]').length; i++) {
                if(!$('input[name=receiverPhone]').eq(i).val()) {
                    alert('수신자 전화번호를 입력하세요');
                    $('input[name=receiverPhone]').eq(i).focus();
                    return;
                }
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderReceiverPhone"] = $('input[name=receiverPhone]').eq(i).val();
            }
        }

        if($('input[name=address]').length > 0) {
            for (let i = 0; i < $('input[name=address]').length; i++) {
                if(!$('input[name=address]').eq(i).val()) {
                    alert('주소를 찾아주세요');
                    $('input[name=address]').eq(i).focus();
                    return;
                }
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderAddress"] = $('input[name=address]').eq(i).val();
            }
        }

        if($('input[name=postcode]').length > 0) {
            for (let i = 0; i < $('input[name=postcode]').length; i++) {
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderPostcode"] = $('input[name=postcode]').eq(i).val();
            }
        }

        if($('input[name=addressDetail]').length > 0) {
            for (let i = 0; i < $('input[name=addressDetail]').length; i++) {
                if(!$('input[name=addressDetail]').eq(i).val()) {
                    alert('상세주소를 입력하세요');
                    $('input[name=addressDetail]').eq(i).focus();
                    return;
                }
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderAddressDetail"] = $('input[name=addressDetail]').eq(i).val();
            }
        }

        if($('select[name=kind]').length > 0) {
            for (let i = 0; i < $('select[name=kind]').length; i++) {
                if(!$('select[name=kind]').eq(i).val()) {
                    alert('품종을 골라주세요');
                    $('select[name=kind]').eq(i).focus();
                    return;
                }
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderKind"] = $('select[name=kind]').eq(i).val();
            }
        }

        if($('select[name=weight]').length > 0) {
            for (let i = 0; i < $('select[name=weight]').length; i++) {
                if(!$('select[name=weight]').eq(i).val()) {
                    alert('키로수를 골라주세요');
                    $('select[name=weight]').eq(i).focus();
                    return;
                }
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderWeight"] = $('select[name=weight]').eq(i).val();
            }
        }

        if($('select[name=size]').length > 0) {
            for (let i = 0; i < $('select[name=size]').length; i++) {
                if(!$('select[name=size]').eq(i).val()) {
                    alert('규격을 골라주세요');
                    $('select[name=size]').eq(i).focus();
                    return;
                }
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderSize"] = $('select[name=size]').eq(i).val();
            }
        }

        if($('input[name=amount]').length > 0) {
            for (let i = 0; i < $('input[name=amount]').length; i++) {
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderCount"] = $('input[name=amount]').eq(i).val();
            }
        }

        for (let i = 0; i < orderDTOS.length; i++) {
            orderDTOS[i]["orderId"] = $('input[name=orderId]').val();
            orderDTOS[i]["orderOrdererName"] = $('input[name=ordererName]').val();
            orderDTOS[i]["orderOrdererPhone"] = $('input[name=ordererPhone]').val();
        }

        $.ajax({
            url: "/order-update",
            type: "post",
            contentType: "application/json",
            data: JSON.stringify(orderDTOS[0]),
            success: function() {
                alert('수정이 완료되었습니다');

                location.reload();
            },
            error: function(e) {
                console.log(e);
            }
        });
    });

    $(document).on('click', '.order-cancel', function() {
        if(!confirm('이미 입금을 하신분들은 주문 취소 전 먼저 위 번호로 문의 바랍니다.\n정말로 주문을 취소하시겠습니까?')) {
            return;
        }

        $.ajax({
            url: "/order-delete",
            type: "post",
            data: { orderId : $(this).closest('tr').find('input[name=orderId]').val() },
            success: function() {
                alert('주문이 취소되었습니다');

                location.reload();
            },
            error: function(e) {
                console.log(e);
            }
        });
    });
});