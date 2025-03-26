function makePayment() {
    // Lấy thông tin từ button
    var button = document.querySelector('.register-button');
    var email = String(button.getAttribute('data-email')); // Lấy email từ thuộc tính data-email
    var reservationId = button.getAttribute('data-reservation-id');
    var tableId = button.getAttribute('data-table-id');
    var menuId = button.getAttribute('data-menu-id');
    var totalPrice = button.getAttribute('data-total-price');

    // Chuyển đổi totalPrice từ string sang int
    var tongTien = parseInt(totalPrice.replace(' VND', '').replace(/\./g, ''), 10);

    // Kiểm tra nếu giá trị tongTien không hợp lệ
    if (isNaN(tongTien) || tongTien <= 0) {
        console.error("Lỗi: Tổng tiền không hợp lệ:", tongTien);
        alert('Tổng tiền không hợp lệ.');
        return; // Dừng quá trình nếu tổng tiền không hợp lệ
    }

    // Log các giá trị để kiểm tra
    console.log("Email:", email);
    console.log("Reservation ID:", reservationId);
    console.log("Table ID:", tableId);
    console.log("Menu ID:", menuId);
    console.log("Total Price:", tongTien);

    // Gửi thông tin thanh toán qua fetch
    fetch(`/api/v1/payment/${email}/${reservationId}/${tableId}/${menuId}?amount=${tongTien}`, {
        method: 'GET',
        headers: {
            'Content-Type': 'application/json'
        }
    })
    .then(response => response.json())
    .then(data => {
        if (data.payUrl) {
            // Chuyển hướng đến URL thanh toán của MoMo
            window.location.href = data.payUrl;
        } else {
            alert('Có lỗi xảy ra, vui lòng thử lại sau.');
        }
    })
    .catch(error => {
        console.error('Error:', error);
        alert('Có lỗi xảy ra trong quá trình thanh toán. Vui lòng thử lại sau.');
    });
}




//// Đảm bảo rằng các giá trị như giá trị tour được cập nhật đúng khi trang được tải
//document.addEventListener('DOMContentLoaded', function() {
//    var giaTour = parseFloat(document.getElementById('priceSale').value);
//    var formattedGiaTour = giaTour.toLocaleString('vi-VN');
//    var totalPriceElement = document.getElementById('totalPrice');
//    totalPriceElement.textContent = formattedGiaTour + ' VND';
//});
