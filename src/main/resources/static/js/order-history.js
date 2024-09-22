$(function() {

    const urlParams = new URL(location.href).searchParams;
    let paramStartDate = urlParams.get('startDate') ? urlParams.get('startDate') : '';
    let paramEndDate = urlParams.get('endDate') ? urlParams.get('endDate') : '';
    // let paramOrdererName = urlParams.get('ordererName') ? urlParams.get('ordererName') : '';

    // 현재 날짜를 가져옵니다.
    var currentDate = new Date();

    // 현재 날짜를 "YYYY-MM-DD" 형식으로 변환합니다.
    var year = currentDate.getFullYear();
    var month = ("0" + (currentDate.getMonth() + 1)).slice(-2); // 월은 0부터 시작하므로 1을 더해줍니다.
    var day = ("0" + currentDate.getDate()).slice(-2);

    var formattedCurrentDate = year + "-" + month + "-" + day;

    // 1달 전 날짜를 가져옵니다.
    var oneMonthAgo = new Date(currentDate);
    oneMonthAgo.setMonth(oneMonthAgo.getMonth() - 1); // 1달 전으로 설정

    // 1달 전 날짜를 "YYYY-MM-DD" 형식으로 변환합니다.
    var yearAgo = oneMonthAgo.getFullYear();
    var monthAgo = ("0" + (oneMonthAgo.getMonth() + 1)).slice(-2); // 월은 0부터 시작하므로 1을 더해줍니다.
    var dayAgo = ("0" + oneMonthAgo.getDate()).slice(-2);

    var formattedOneMonthAgo = yearAgo + "-" + monthAgo + "-" + dayAgo;

    paramStartDate = (!paramStartDate ? formattedOneMonthAgo : paramStartDate);
    paramEndDate = (!paramEndDate ? formattedCurrentDate : paramEndDate);

    $('input[name=startDate]').val(paramStartDate);
    $('input[name=endDate]').val(paramEndDate);

    orderList.forEach(order => {
        let price = order.orderPrice.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");

        let text = `
            <tr>
                <input type="hidden" name="orderId" value="${order.orderId}">
                <td>${order.orderGroupId}</td>
                <td>${order.orderOrdererName}</td>
                <td>${order.orderOrdererPhone}</td>
                <td>${order.orderDate}</td>
                <td>${order.orderDeliveryName}</td>
                <td>${order.orderDeliveryPhone}</td>
                <td>${order.orderReceiverName}</td>
                <td>${order.orderReceiverPhone}</td>
                <td>${order.orderAddress} ${order.orderAddressDetail}</td>
                <td>${order.orderKind}</td>
                <td>${order.orderWeight}</td>
                <td>${order.orderSize}</td>
                <td>${order.orderCount}</td>
                <td><span class="price">${price}</span>원</td>
                <td class="order-cancel">취소하기</td>
            </tr>
        `;

        $('.list-container').append(text);
    });

    $('.search').on('click', () => {
        paramStartDate = $('input[name=startDate]').val();
        paramEndDate = $('input[name=endDate]').val();

        location.href = '/order-history?startDate=' + paramStartDate + '&endDate=' + paramEndDate/* + '&ordererName=' + paramOrdererName*/;
    });

    $('.excel-download').on('click', () => {
        location.href = '/excel?startDate=' + paramStartDate + '&endDate=' + paramEndDate;
    });
});