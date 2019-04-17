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
});
