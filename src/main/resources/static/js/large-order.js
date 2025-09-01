$(function() {

    // key = `${isFirst}|${productKind}|${productWeight||''}`
    // value = 서버에서 받은 "배열 데이터" 그대로 저장
    const _productInfoDataCache = {};

    $('.form-download-btn').on('click', () => {
        // $.ajax({
        //     url: "/excel-order-form-download",
        //     type: "get",
        //     success: function () {
        //         alert('양식 다운로드가 완료되었습니다.');
        //         return;
        //     },
        //     error: function(e) {
        //         alert('오류가 발생하였습니다.');
        //         console.log(e);
        //     }
        // });
        location.href = "/excel-order-form-download";
    });

    $('.form-upload-btn').on('click', () => {
        $('input[name=formUploadFile]').click();
    });

    $('input[name=formUploadFile]').on('change', function() {
        const fileObj = this.files && this.files[0];
        if (!fileObj) return;

        const fd = new FormData();
        fd.append('file', fileObj); // ← MultipartFile "file" 이름과 매칭

        // ajax /read-excel-order 호출 후 화면에 리스트 넣기
        $.ajax({
            url: "/read-excel-order",
            type: "post",
            data: fd,
            processData: false,      // 중요: 쿼리스트링 변환 금지
            contentType: false,      // 중요: 브라우저가 multipart 경계 자동 세팅
            async: false,
            // 만약 Spring Security CSRF 사용 중이면 아래 주석 해제해서 토큰 넣어주세요
            // headers: { 'X-CSRF-TOKEN': $('meta[name="_csrf"]').attr('content') },
            success: function(result) {
                if(result) {
                    result.forEach(order => {
                        let text = `
                            <!-- 주문 반복 시작 -->
                            <div class="receive-information-container">
                                <div class="order-plus-minus">
                                    <span>주문</span>
                                </div>
                                <div class="delivery-container">
                                    <div class="form-group">
                                        <label>보내는사람 이름</label>
                                        <input type="text" name="deliveryName" placeholder="이름을 입력해주세요" maxlength="5" value="${order.orderDeliveryName}">
                                        <span class="error-message">보내는사람 이름을 입력해주세요</span>
                                    </div>
                                    <div class="form-group">
                                        <label>보내는사람 전화번호</label>
                                        <input type="text" name="deliveryPhone" placeholder="전화번호를 입력해주세요" maxlength="13" value="${order.orderDeliveryPhone}">
                                        <span class="error-message">보내는사람 전화번호를 입력해주세요</span>
                                    </div>
                                </div>
                                <div class="form-group">
                                    <label>받는사람 이름</label>
                                    <input type="text" name="receiverName" placeholder="이름을 입력해주세요" value="${order.orderReceiverName}">
                                    <span class="error-message">이름을 입력해주세요</span>
                                </div>
                                <div class="form-group">
                                    <label>받는사람 전화번호</label>
                                    <input type="text" name="receiverPhone" placeholder="전화번호를 입력해주세요" value="${order.orderReceiverPhone}">
                                    <span class="error-message">전화번호를 입력해주세요</span>
                                </div>
                                <div class="form-group">
                                    <label>주소</label>
                                    <input type="text" name="postcode" class="postcode" placeholder="우편번호" value="${order.orderPostcode}" readonly>
                                    <div class="address-container">
                                        <input type="text" name="address" placeholder="주소를 찾아주세요" value="${order.orderAddress}" readOnly/>
                                        <button type="button" class="find-address">찾기</button>
                                    </div>
                                    <input type="text" name="addressDetail" placeholder="상세주소 입력" value="${order.orderAddressDetail}">
                                    <span class="error-message">주소를 찾은 후 상세주소를 입력해주세요</span>
                                </div>
                                <div class="form-group">
                                    <label>사과 품종</label>
                                    <select name="kind" id="kind">
                                        <option value="">품종을 골라주세요</option>
                                        `;
                            productInfoKinds.forEach(productKind => {
                                text += `<option value="${productKind.productKind}"`

                                // 품절 여부가 Y, P이면 비활성화
                                switch(productKind.productSoldOut) {
                                    case 'Y':
                                    case 'P':
                                        text += `disabled`;
                                        break;
                                }
                                // + (productKind.productSoldOut == 'Y' ? `disabled` : ``) +

                                // 선택된 값 처리
                                if (productKind.productKind == order.orderKind) {
                                    text += ` selected`;
                                }

                                text += `>${productKind.productKind}`;

                                // 품절 여부가 Y이면 품절, P이면 준비중이라고 표시
                                switch(productKind.productSoldOut) {
                                    case 'Y':
                                        text += `(품절)`;
                                        break;
                                    case 'P':
                                        text += `(준비중)`;
                                        break;
                                }
                                // + (productKind.productSoldOut == 'Y' ? `(품절)` : ``) +

                                text += `</option>`;
                            });
                            text += `
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label>사과 kg</label>
                                    <select name="weight" id="weight">
                                        <option value="">키로수를 골라주세요</option>
                                    `;

                            text += getProductInfo(order.orderKind, null, order.orderWeight);
                            text += `
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label>사과 규격</label>
                                    <select name="size" id="size">
                                        <option value="">규격을 골라주세요</option>
                                    `;

                            text += getProductInfo(order.orderKind, order.orderWeight, order.orderSize);
                            text += `
                                    </select>
                                </div>
                                <div class="form-group">
                                    <label>사과 수량</label>
                                    <div class="count-wrap _count">
                                        <button type="button" class="minus">-</button>
                                        <input type="text" name="amount" class="input" value="${order.orderCount}" />
                                        <button type="button" class="plus">+</button>
                                    </div>
                                </div>
                                <div class="img-container">
                                    <span class="minus-img">주문삭제</span>
                                    <span class="plus-img">주문추가</span>
                                </div>
                            </div>
                            <!-- 주문 반복 끝 -->
                        `;

                        $('.receive-informations').append(text);

                        $('.total-count').text(parseInt($('.total-count').text()) + 1);
                    });
                }
            },
            error: function(e) {
                alert('오류가 발생했습니다.');
                console.log(e);
            }
        });
    });

    function getProductInfo(productKind, productWeight, selectData) {
        const isFirst = (!productWeight ? 2 : 3);
        const cacheKey = `${isFirst}|${productKind}|${productWeight || ''}`;

        // 캐시에서 "데이터 배열"을 먼저 찾음
        let list = _productInfoDataCache[cacheKey];

        if (!list) {
            $.ajax({
                url: "/product-info",
                type: "post",
                data: { isFirst: isFirst, productKind: productKind, productWeight: productWeight },
                async: false, // 기존 흐름 유지
                success: function(result) {
                    list = result || [];
                    _productInfoDataCache[cacheKey] = list; // 데이터 배열 캐싱
                }
            });
        }

        // 매번 selectData 기준으로 HTML 렌더링
        let text = '';

        if (!productWeight) {
            // weight 옵션
            list.forEach(data => {
                text += `<option value="${data.productWeight}"`;

                // 품절/준비중 비활성화
                if (data.productSoldOut === 'Y' || data.productSoldOut === 'P') {
                    text += ` disabled`;
                }

                // 선택값 처리 (문자열 비교 안전)
                if (String(data.productWeight) === String(selectData)) {
                    text += ` selected`;
                }

                // 라벨
                text += `>${data.productWeight}`;
                if (data.productSoldOut === 'Y') text += `(품절)`;
                else if (data.productSoldOut === 'P') text += `(준비중)`;
                text += `</option>`;
            });
        } else {
            // size 옵션
            list.forEach(data => {
                text += `<option value="${data.productSize}"`;

                if (data.productSoldOut === 'Y' || data.productSoldOut === 'P') {
                    text += ` disabled`;
                }

                // 선택값 처리: 전체값 또는 첫 글자 중 하나로 맞춰줌
                if (
                    String(data.productSize) === String(selectData) ||
                    String(data.productSize?.[0]) === String(selectData)
                ) {
                    text += ` selected`;
                }

                text += `>${data.productSize}`;
                if (data.productSoldOut === 'Y') text += `(품절)`;
                else if (data.productSoldOut === 'P') text += `(준비중)`;
                text += `</option>`;
            });
        }

        return text;
    }

    // function getProductInfo(productKind, productWeight, selectData) {
    //     let text;
    //
    //     $.ajax({
    //         url: "/product-info",
    //         type: "post",
    //         data: { isFirst : (!productWeight ? 2 : 3), productKind : productKind, productWeight : productWeight},
    //         async: false,
    //         success: function(result) {
    //             if(result) {
    //                 if(!productWeight) {
    //                     result.forEach(data => {
    //                         text += `<option value="${data.productWeight}"`;
    //
    //                         // 품절 여부가 Y, P이면 비활성화
    //                         switch(data.productSoldOut) {
    //                             case 'Y':
    //                             case 'P':
    //                                 text += `disabled`;
    //                                 break;
    //                         }
    //
    //                         // 선택된 값 처리
    //                         if (data.productWeight == selectData) {
    //                             text += ` selected`;
    //                         }
    //
    //                         // + (data.productSoldOut == 'Y' ? `disabled` : ``) +
    //                         text += `>${data.productWeight}`
    //
    //                         // 품절 여부가 Y이면 품절, P이면 준비중이라고 표시
    //                         switch(data.productSoldOut) {
    //                             case 'Y':
    //                                 text += `(품절)`;
    //                                 break;
    //                             case 'P':
    //                                 text += `(준비중)`;
    //                                 break;
    //                         }
    //
    //                         // + (data.productSoldOut == 'Y' ? `(품절)` : ``) +
    //                         text += `</option>`;
    //                     });
    //                 } else {
    //                     result.forEach(data => {
    //                         text += `<option value="${data.productSize}"`;
    //
    //                         // 품절 여부가 Y, P이면 비활성화
    //                         switch(data.productSoldOut) {
    //                             case 'Y':
    //                             case 'P':
    //                                 text += `disabled`;
    //                                 break;
    //                         }
    //                         // + (data.productSoldOut == 'Y' ? `disabled` : ``) +
    //
    //                         // 선택된 값 처리
    //                         if (data.productSize[0] == selectData) {
    //                             text += ` selected`;
    //                         }
    //
    //                         text += `>${data.productSize}`;
    //
    //                         // 품절 여부가 Y이면 품절, P이면 준비중이라고 표시
    //                         switch(data.productSoldOut) {
    //                             case 'Y':
    //                                 text += `(품절)`;
    //                                 break;
    //                             case 'P':
    //                                 text += `(준비중)`;
    //                                 break;
    //                         }
    //
    //                         // + (data.productSoldOut == 'Y' ? `(품절)` : ``) +
    //                         text += `</option>`;
    //
    //                     });
    //                 }
    //             }
    //         }
    //     });
    //
    //     return text;
    // }
});