app.controller('payController',function($scope, $location, $http){

    $scope.getMoney=function () {
        return $location.search()['total_amount'];
    }

    $scope.getPaymentType=function () {
        if($location.search()['method']==null || $location.search()['method']==""){
            return '货到付款'
        } else {
            return '支付宝付款';
        }

    }
    $scope.updateOrderStatus=function () {
        var out_trade_no = $location.search()['out_trade_no'];
        var transaction_id = $location.search()['trade_no'];
        $http.get('order/updateOrderStatus.do?out_trade_no='+out_trade_no+"&transaction_id="+transaction_id).success(
            function (response) {
                //alert('执行更新订单和支付日志表信息');
            }
        );
    }

})


