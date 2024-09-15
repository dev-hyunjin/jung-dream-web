$(function() {

    productInfoKinds.forEach(productKind => {
        $('select[name=kind]').append(`<option value="${productKind.productKind}">${productKind.productKind}</option>`);
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
        $(this).next().val(parseInt($(this).next().val()) + 1);
    });

    $(document).on('click', '.minus', function() {
        if($(this).prev().val() == 1) {
            return;
        }

        $(this).prev().val(parseInt($(this).prev().val()) - 1);
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
                <div class="form-group">
                    <label>수신자 이름</label>
                    <input type="text" name="receiverName" placeholder="이름을 입력해주세요">
                    <span class="error-message">이름을 입력해주세요</span>
                </div>
                <div class="form-group">
                    <label>수신자 전화번호</label>
                    <input type="text" name="receiverPhone" placeholder="전화번호를 입력해주세요(01012345678)">
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
            text += `<option value="${productKind.productKind}">${productKind.productKind}</option>`;
        });
        text += `
                    </select>
                </div>
                <div class="form-group">
                    <label>사과 kg</label>
                    <select name="weight" id="weight">
                        <option value="">kg을 골라주세요</option>
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
                    <img src="/image/minus.png" class="minus-img" alt="">
                    <img src="/image/plus.png" class="plus-img" alt="">
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
                            selectBox.append(`<option value="${data.productWeight}">${data.productWeight}</option>`);
                        });
                    } else {
                        const selectBox = $(e).closest('.receive-information-container').find('select[name=size]');

                        selectBox.html(`<option value="">규격을 골라주세요</option>`);
                        result.forEach(data => {
                            selectBox.append(`<option value="${data.productSize}">${data.productSize}</option>`);
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
                orderDTOS[i]["receiverName"] = $('input[name=receiverName]').eq(i).val();
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
                orderDTOS[i]["receiverPhone"] = $('input[name=receiverPhone]').eq(i).val();
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
                orderDTOS[i]["address"] = $('input[name=address]').eq(i).val();
            }
        }

        if($('input[name=postcode]').length > 0) {
            for (let i = 0; i < $('input[name=postcode]').length; i++) {
                if (!orderDTOS[i]) {
                    orderDTOS[i] = {};
                }
                orderDTOS[i]["postcode"] = $('input[name=postcode]').eq(i).val();
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
                orderDTOS[i]["addressDetail"] = $('input[name=addressDetail]').eq(i).val();
            }
        }

        if(!$('input[name=personalInformation]').prop('checked')) {
            alert('개인정보 제공 동의 체크해주세요');
            return;
        }
        
        // $.ajax({
        //     url: "/admins/product/save",
        //     type: "post",
        //     contentType: "application/json",
        //     data: JSON.stringify({
        //         productVO: productVO,
        //         productOptionVOS: productOptionVOS,
        //         productFileVOS: productFileVOS
        //     }),
        //     success: function() {
        //         document.location.reload(true);
        //     }
        // });
    });
});