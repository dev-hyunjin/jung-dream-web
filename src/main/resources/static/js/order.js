$(function() {

    productInfoKinds.forEach(productKind => {
        let text = `<option value="${productKind.productKind}"`
            + (productKind.productSoldOut == 'Y' ? `disabled` : ``) +
        `>${productKind.productKind}` + (productKind.productSoldOut == 'Y' ? `(품절)` : ``) + `</option>`;
        $('select[name=kind]').append(text);
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

    $(document).on('click', '.delivery-button', function() {
        if($(this).prop('checked')) {
            let text = `
                <div class="form-group">
                    <label>보내는사람 이름</label>
                    <input type="text" name="deliveryName" placeholder="이름을 입력해주세요" maxlength="5">
                    <span class="error-message">보내는사람 이름을 입력해주세요</span>
                </div>
                <div class="form-group">
                    <label>보내는사람 전화번호</label>
                    <input type="text" name="deliveryPhone" placeholder="전화번호를 입력해주세요" maxlength="13">
                    <span class="error-message">보내는사람 전화번호를 입력해주세요</span>
                </div>
            `;
            $(this).closest('.receive-information-container').find('.delivery-container').append(text);
        } else {
            $(this).closest('.receive-information-container').find('.delivery-container').empty();
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

    $(document).on('click', '.plus-img', function() {
        let text = `
            <!-- 주문 반복 시작 -->
            <div class="receive-information-container">
                <div class="order-plus-minus">
                    <span>주문</span>
                    <div class="img-container">
                        <span>주문자 정보와 동일</span><input type="checkbox" name="samePerson" id="same-person">
                    </div>
                </div>
                <div class="delivery-button-container">
                    <span><input type="checkbox" class="delivery-button" />(선택사항)보내는사람</span>
                </div>
                <div class="delivery-container"></div>
                <div class="form-group">
                    <label>받는사람 이름</label>
                    <input type="text" name="receiverName" placeholder="이름을 입력해주세요">
                    <span class="error-message">이름을 입력해주세요</span>
                </div>
                <div class="form-group">
                    <label>받는사람 전화번호</label>
                    <input type="text" name="receiverPhone" placeholder="전화번호를 입력해주세요">
                    <span class="error-message">전화번호를 입력해주세요</span>
                </div>
                <div class="form-group">
                    <label>주소</label>
                    <input type="text" name="postcode" class="postcode" placeholder="우편번호" readonly>
                    <div class="address-container">
                        <input type="text" name="address" placeholder="주소를 찾아주세요" readOnly/>
                        <button type="button" class="find-address">찾기</button>
                    </div>
                    <input type="text" name="addressDetail" placeholder="상세주소 입력"/>
                    <span class="error-message">주소를 찾은 후 상세주소를 입력해주세요</span>
                </div>
                <div class="form-group">
                    <label>사과 품종</label>
                    <select name="kind" id="kind">
                        <option value="">품종을 골라주세요</option>
                        `;
        productInfoKinds.forEach(productKind => {
            text += `<option value="${productKind.productKind}"`
                + (productKind.productSoldOut == 'Y' ? `disabled` : ``) +
                `>${productKind.productKind}` + (productKind.productSoldOut == 'Y' ? `(품절)` : ``) + `</option>`;
        });
        text += `
                    </select>
                </div>
                <div class="form-group">
                    <label>사과 kg</label>
                    <select name="weight" id="weight">
                        <option value="">키로수를 골라주세요</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>사과 규격</label>
                    <select name="size" id="size">
                        <option value="">규격을 골라주세요</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>사과 수량</label>
                    <div class="count-wrap _count">
                        <button type="button" class="minus">-</button>
                        <input type="text" name="amount" class="input" value="1" />
                        <button type="button" class="plus">+</button>
                    </div>
                </div>
                <div class="img-container">
<!--                    <img src="/image/minus.png" class="minus-img" alt="">-->
<!--                    <img src="/image/plus.png" class="plus-img" alt="">-->
                    <span class="minus-img">주문삭제</span>
                    <span class="plus-img">주문추가</span>
                </div>
            </div>
            <!-- 주문 반복 끝 -->
        `;

        $('.receive-informations').append(text);

        $('.total-count').text(parseInt($('.total-count').text()) + 1);
    });

    $(document).on('click', '.minus-img', function() {
        $(this).closest('.receive-information-container').remove();

        $('.total-count').text(parseInt($('.total-count').text()) - 1);
    });

    function getProductInfo(e, productKind, productWeight) {
        $.ajax({
            url: "/product-info",
            type: "post",
            data: { isFirst : (!productWeight ? 2 : 3), productKind : productKind, productWeight : productWeight},
            success: function(result) {
                if(result) {
                    if(!productWeight) {
                        const selectBox = $(e).closest('.receive-information-container').find('select[name=weight]');

                        $(e).closest('.receive-information-container').find('select[name=size]').html(`<option value="">규격을 골라주세요</option>`);

                        selectBox.html(`<option value="">kg을 골라주세요</option>`);

                        result.forEach(data => {
                            let text = `<option value="${data.productWeight}"`
                                + (data.productSoldOut == 'Y' ? `disabled` : ``) +
                                `>${data.productWeight}` + (data.productSoldOut == 'Y' ? `(품절)` : ``) + `</option>`;
                            selectBox.append(text);
                        });
                    } else {
                        const selectBox = $(e).closest('.receive-information-container').find('select[name=size]');

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

    /* 등록 버튼 클릭 */
    $('.submit-button').on('click', () => {
        let phoneRegex = /^(01[016789])-(\d{3,4})-(\d{4})$/;

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

        if(!phoneRegex.test($('input[name=ordererPhone]').val())) {
            alert('전화번호 형식을 확인해주세요');
            $('input[name=ordererPhone]').focus();
            return;
        }

        if(!$('input[name=orderPassword]').val()) {
            alert('비밀번호(주문내역 확인용)를 입력하세요');
            $('input[name=orderPassword]').focus();
            return;
        }

        if(!/^\d{6}$/.test($('input[name=orderPassword]').val())) {
            alert('비밀번호는 숫자 6자리로 설정해주세요');
            $('input[name=orderPassword]').focus();
            return;
        }

        let orderDTOS = [];

        if($('input[name=receiverName]').length > 0) {
            for (let i = 0; i < $('input[name=receiverName]').length; i++) {
                if(!$('input[name=receiverName]').eq(i).val()) {
                    alert('받는사람 이름을 입력하세요');
                    $('input[name=receiverName]').eq(i).focus();
                    return;
                }
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderReceiverName"] = $('input[name=receiverName]').eq(i).val().trim();
            }
        }

        if($('input[name=receiverPhone]').length > 0) {
            for (let i = 0; i < $('input[name=receiverPhone]').length; i++) {
                if(!$('input[name=receiverPhone]').eq(i).val()) {
                    alert('받는사람 전화번호를 입력하세요');
                    $('input[name=receiverPhone]').eq(i).focus();
                    return;
                }
                if(!phoneRegex.test($('input[name=receiverPhone]').val())) {
                    alert('전화번호 형식을 확인해주세요');
                    $('input[name=receiverPhone]').focus();
                    return;
                }
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderReceiverPhone"] = $('input[name=receiverPhone]').eq(i).val().trim();
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
                orderDTOS[i]["orderAddress"] = $('input[name=address]').eq(i).val().trim();
            }
        }

        if($('input[name=postcode]').length > 0) {
            for (let i = 0; i < $('input[name=postcode]').length; i++) {
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderPostcode"] = $('input[name=postcode]').eq(i).val().trim();
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
                orderDTOS[i]["orderAddressDetail"] = $('input[name=addressDetail]').eq(i).val().trim();
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
                orderDTOS[i]["orderKind"] = $('select[name=kind]').eq(i).val().trim();
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
                orderDTOS[i]["orderWeight"] = $('select[name=weight]').eq(i).val().trim();
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
                orderDTOS[i]["orderSize"] = $('select[name=size]').eq(i).val().trim();
            }
        }

        if($('input[name=amount]').length > 0) {
            for (let i = 0; i < $('input[name=amount]').length; i++) {
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["orderCount"] = $('input[name=amount]').eq(i).val().trim();
            }
        }

        if(!$('input[name=personalInformation]').prop('checked')) {
            alert('개인정보 제공 동의 체크해주세요');
            return;
        }

        for (let i = 0; i < orderDTOS.length; i++) {
            orderDTOS[i]["orderPassword"] = $('input[name=orderPassword]').val().trim();
            orderDTOS[i]["orderOrdererName"] = $('input[name=ordererName]').val().trim();
            orderDTOS[i]["orderOrdererPhone"] = $('input[name=ordererPhone]').val().trim();

            let deliveryName = ($('input[name=receiverName]').eq(i).closest('.receive-information-container').find('input[name=deliveryName]').val()
                ? $('input[name=receiverName]').eq(i).closest('.receive-information-container').find('input[name=deliveryName]').val() : '정재하');
            orderDTOS[i]["orderDeliveryName"] = deliveryName.trim();

            let deliveryPhone = ($('input[name=receiverName]').eq(i).closest('.receive-information-container').find('input[name=deliveryPhone]').val()
                ? $('input[name=receiverName]').eq(i).closest('.receive-information-container').find('input[name=deliveryPhone]').val() : '010-4532-4350');

            if(!phoneRegex.test(deliveryPhone)) {
                alert('전화번호 형식을 확인해주세요');
                $('input[name=receiverName]').eq(i).closest('.receive-information-container').find('input[name=deliveryPhone]').focus();
                return;
            }

            orderDTOS[i]["orderDeliveryPhone"] = deliveryPhone.trim();
        }

        $.ajax({
            url: "/order",
            type: "post",
            contentType: "application/json",
            data: JSON.stringify(orderDTOS),
            success: function(result) {
                if(result) {
                    alert('주문이 완료되었습니다');

                    const form = document.createElement('form');
                    form.method = 'POST';
                    form.action = '/order-complete';

                    const input = document.createElement('input');
                    input.type = 'hidden';
                    input.name = 'totalPrice';
                    input.value = result;
                    form.appendChild(input);

                    // Form을 문서에 추가하고 제출
                    document.body.appendChild(form);
                    form.submit();
                }
            },
            error: function(e) {
                console.log(e);
            }
        });
    });
});