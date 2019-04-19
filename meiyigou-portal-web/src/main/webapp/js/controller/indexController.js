//首页控制器
app.controller('indexController',function($scope,loginService,$controller,$http){

    $controller('baseController',{$scope:$scope});//继承

    $scope.showName=function(){
        loginService.showName().success(
            function(response){
                $scope.loginName=response.loginName;
            }
        );
    }

    //查询我的订单列表
    $scope.search=function(page,rows){
        $http.post('order/findOrderPage.do?pageNum='+page+'&pageSize='+rows).success(
            function(response){
                $scope.list=response.rows;
                $scope.paginationConf.totalItems=response.total;//更新总记录数
            }
        );
    }

    //支付类型，1、在线支付，2、货到付款
    $scope.paymentType=['','在线支付','货到付款'];

    //状态：1、未付款，2、已付款，3、未发货，4、已发货，5、交易成功，6、交易关闭,7、待评价
    $scope.status=['','未付款','已付款','未发货','已发货','交易成功','交易关闭','待评价'];

    //删除订单
    $scope.deleOrderById=function (orderId) {
        $http.get('order/delete.do?ids='+orderId).success(
            function (response) {
                alert(response.message);
                $scope.reloadList();
            }
        );
    }
});
