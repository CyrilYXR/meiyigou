app.controller('orderController',function ($scope, $http, $location) {
    
    var orderId = $location.search()['orderId'];

    $scope.orderStatus=['','未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价'];
    $scope.paymentType=['','在线支付','货到付款'];

    //根据orderId查询order实体
    $scope.findOrder=function () {
        $http.get('order/findOne.do?id='+orderId).success(
            function (response) {
                $scope.order=response;
            }
        )
    }

    //根据orderId查询order_item实体
    $scope.findOrderItem=function () {
        $http.get('orderItem/findByOrderId.do?orderId='+orderId).success(
            function (response) {
                $scope.orderItemList=response;
            }
        )
    }

    //查询当前用户的订单列表
    $scope.findOrderList=function () {

    }
})