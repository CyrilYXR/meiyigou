app.controller('baseController', function ($scope) {

    //分页控件配置
    $scope.paginationConf = {
        currentPage: 1,//当前页
        totalItems: 10,//总记录数
        itemsPerPage: 10,//每页记录数
        perPageOptions: [10, 20, 30, 40, 50],//下拉框分页选项
        onChange: function () {  //页码变更触发的函数
            $scope.reloadList();
        }

    }

    //刷新数据
    $scope.reloadList=function(){
        $scope.search($scope.paginationConf.currentPage, $scope.paginationConf.itemsPerPage)
    }

    //用户勾选的ID集合
    $scope.selectIds=[];

    //更新勾选的ID集合
    $scope.updateSelection=function ($event, id) {
        if($event.target.checked){
            $scope.selectIds.push(id);  //push向集合添加元素
        } else {
            var index = $scope.selectIds.indexOf(id);   //查找值的位置
            $scope.selectIds.splice(index,1);   //param1 移除的位置  param2 移除的个数
        }

    }
    
    $scope.jsonToString=function (jsonString,key) {
        var json = JSON.parse(jsonString);
        var value = "";
        for(var i=0;i<json.length;i++){
            if(i > 0){
                value += ",";
            }
            value += json[i]['text'];
        }
        return value;
    }
})
