app.service('uploadService', function ($http) {

    //上传文件
    this.uploadFile=function () {
        var formdata = new FormData();
        formdata.append('file', file.files[0]);  //file 文件上传框的name

        return $http({
            method:'POST',
            url:"../upload.do",
            data: formdata,
            headers: {'Content-Type':undefined},
            transformRequest: angular.identity

        })
    }
})