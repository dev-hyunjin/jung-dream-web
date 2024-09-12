$(function() {

    $(document).on('click', '.find-address', function() {
        kakaoAddress(this);
    });

    $(document).on('click', '.plus', function() {
        $(this).next().val(parseInt($(this).next().val()) + 1);
    });

    $(document).on('click', '.plus-img', function() {
        let text = `
            <!-- 주문 반복 시작 -->
            <div class="receive-information-container">
                <div class="order-plus-minus">
                    <span>주문</span>
                    <div class="img-container">
                        <img src="../../static/image/plus.png" class="plus-img" alt="">
                        <img src="../../static/image/minus.png" class="minus-img" alt="">
                    </div>
                </div>
                <div class="form-group">
                    <label>주소</label>
                    <input type="text" name="mailNumber" class="mail-number" placeholder="우편번호" readonly>
                    <div class="address-container">
                        <input type="text" name="address" placeholder="주소를 찾아주세요" readOnly/>
                        <button type="button" class="find-address">찾기</button>
                    </div>
                    <input type="text" name="addressDetail" placeholder="상세주소 입력"/>
                    <span class="error-message">주소를 찾은 후 상세주소를 입력해주세요</span>
                </div>
                <div class="form-group">
                    <label>사과 품종</label>
                    <select name="" id="">
                        <option value="">부사</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>사과 kg</label>
                    <select name="" id="">
                        <option value="">5kg</option>
                        <option value="">10kg</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>사과 호수</label>
                    <select name="" id="">
                        <option value="">특</option>
                        <option value="">1번</option>
                        <option value="">2번</option>
                        <option value="">3번</option>
                    </select>
                </div>
                <div class="form-group">
                    <label>사과 수량</label>
                    <div class="count-wrap _count">
                        <button type="button" class="plus">+</button>
                        <input type="text" class="input" value="1" />
                        <button type="button" class="minus">-</button>
                    </div>
                </div>
            </div>
            <!-- 주문 반복 끝 -->
        `;

        $('.receive-informations').append(text);
    });

    $(document).on('click', '.minus-img', function() {
        $(this).closest('.receive-information-container').remove();
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
                $(e).closest('.form-group').find('.mail-number').val(data.zonecode);
                $(e).closest('.form-group').find('input[name=address]').val(addr + ' ' + extraAddr);
                // 커서를 상세주소 필드로 이동한다.
                $(e).closest('.form-group').find("input[name=addressDetail").focus();
            }
        }).open();
    }
});