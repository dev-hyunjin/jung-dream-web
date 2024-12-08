$(function() {

    const url = window.location.href;

    if(url.includes('product-mgmt')){
        $('.product-mgmt').attr("id", "click");
    }

    if(url.includes('image-mgmt')){
        $('.image-mgmt').attr("id", "click");
    }

    if(url.includes('seller-mgmt')){
        $('.seller-mgmt').attr("id", "click");
    }
});