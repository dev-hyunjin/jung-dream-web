$(function() {

    productInfoList.forEach(productInfo => {
        let text = `
            <ul class="user-list-sub">
               <li class="product-kind">${productInfo.productKind}</li>
               <li class="product-weight">${productInfo.productWeight}</li>
               <li class="product-size">${productInfo.productSize}</li>
               <li class="product-price">${productInfo.productPrice}</li>
               <li class="product-sold-out">${productInfo.productSoldOut}</li>
               <li class="btn">
                   <button type="button" class="edit">수정</button>
                   <button type="button" class="delete">삭제</button>
               </li>
            </ul>
           <hr>
        `;

        $('.user-box').append(text);
    });

    $('.create').on('click', () => {
        $('.registration-modal').css('display', 'flex');
    });

    $('#registration-btn').on('click', () => {
        let registrationModal = $('.registration-modal');

        let $productKind = registrationModal.find('input[name=productKind]');
        let $productWeight = registrationModal.find('input[name=productWeight]');
        let $productSize = registrationModal.find('input[name=productSize]');
        let $productPrice = registrationModal.find('input[name=productPrice]');
        let $productSoldOut = registrationModal.find('select[name=productSoldOut]');

        if(!$productKind.val().trim()) {
            alert('품종을 입력해주세요.');
            $productKind.focus();
            return;
        }

        if(!$productWeight.val().trim()) {
            alert('키로수를 입력해주세요.');
            $productWeight.focus();
            return;
        }

        if(!$productSize.val().trim()) {
            alert('크기를 입력해주세요.');
            $productSize.focus();
            return;
        }

        if(!$productPrice.val().trim()) {
            alert('가격을 입력해주세요.');
            $productPrice.focus();
            return;
        }

        let productInfoDTO = {
            productKind : $productKind.val(),
            productWeight : $productWeight.val(),
            productSize : $productSize.val(),
            productPrice : $productPrice.val(),
            productSoldOut : $productSoldOut.val()
        };

        $.ajax({
            url: "/admins/registration-product-info",
            type: "post",
            contentType: "application/json",
            data: JSON.stringify(productInfoDTO),
            success: function() {
                alert('등록을 완료했습니다.')
                location.reload();
            },
            error: function(e) {
                console.log(e);
            }
        });
    });

    $(document).on('click', '.edit', function()  {
        let productInfo = $(this).parents('.user-list-sub');

        let $productKind = productInfo.find('.product-kind').text();
        let $productWeight = productInfo.find('.product-weight').text();
        let $productSize = productInfo.find('.product-size').text();

        $.ajax({
            url: "/admins/get-product-info",
            type: "post",
            data: { productKind : $productKind, productWeight : $productWeight, productSize : $productSize },
            success: function(result) {
                if(result) {
                    $('input[name=productKind]').val(result.productKind);
                    $('input[name=productWeight]').val(result.productWeight);
                    $('input[name=productSize]').val(result.productSize);
                    $('input[name=productPrice]').val(result.productPrice);
                    $('select[name=productSoldOut]').val(result.productSoldOut);
                }
            },
            error: function(e) {
                console.log(e);
            }
        });

        $('.modify-modal').css('display', 'flex');
    });

    $(document).on('click', '.delete', function()  {
        let productInfo = $(this).parents('.user-list-sub');
        let removeModal = $('.remove-modal');

        removeModal.find('input[name=productKind]').val(productInfo.find('.product-kind').text());
        removeModal.find('input[name=productWeight]').val(productInfo.find('.product-weight').text());
        removeModal.find('input[name=productSize]').val(productInfo.find('.product-size').text());

        $('.remove-modal').css('display', 'flex');
    });

    $(document).on('click', '.modal-close, .modal-real-close', function() {
        $(this).parents('.modal-container').hide();
    });

    $('#modify-btn').on('click', () => {
        let modifyModal = $('.modify-modal');

        let $productKind = modifyModal.find('input[name=productKind]');
        let $productWeight = modifyModal.find('input[name=productWeight]');
        let $productSize = modifyModal.find('input[name=productSize]');
        let $productPrice = modifyModal.find('input[name=productPrice]');
        let $productSoldOut = modifyModal.find('select[name=productSoldOut]');

        if(!$productPrice.val().trim()) {
            alert('가격을 입력해주세요.');
            $productPrice.focus();
            return;
        }

        let productInfoDTO = {
            productKind : $productKind.val(),
            productWeight : $productWeight.val(),
            productSize : $productSize.val(),
            productPrice : $productPrice.val(),
            productSoldOut : $productSoldOut
        };

        $.ajax({
            url: "/admins/modify-product-info",
            type: "post",
            contentType: "application/json",
            data: JSON.stringify(productInfoDTO),
            success: function() {
                alert('수정을 완료했습니다.')
                location.reload();
            },
            error: function(e) {
                console.log(e);
            }
        });
    });

    $('#delete-btn').on('click', () => {
        let removeModal = $('.remove-modal');

        let $productKind = removeModal.find('input[name=productKind]').val();
        let $productWeight = removeModal.find('input[name=productWeight]').val();
        let $productSize = removeModal.find('input[name=productSize]').val();

        $.ajax({
            url: "/admins/remove-product-info",
            type: "post",
            data: { productKind : $productKind, productWeight : $productWeight, productSize : $productSize },
            success: function() {
                alert('삭제되었습니다.')
                location.reload();
            },
            error: function(e) {
                console.log(e);
            }
        });
    });

});