$(document).ready(function(){
var myReleaseChart = echarts.init(document.getElementById('release'));
  // 显示标题，图例和空的坐标轴
myReleaseChart.setOption({
      title: {
          text: 'Case count group by release'
      },
      tooltip: {},
      legend: {
          data: ['Successful Count','Fail Count'],
      	  selected: {
      		'Fail Count' : true
      	  }
      },
      xAxis: {
          data: [],
//          axisLabel:{
//			     interval:0,//横轴信息全部显示
//			     rotate:-30,//-30度角倾斜显示
//			}
      },
      yAxis: {},
      series: [{
          name: 'Fail Count',
          label: {
              normal: {
                  show: true,
                  position: 'top'
              }
          },
          type: 'bar',
          data: []
      },{
          name: 'Successful Count',
          itemStyle:{
              normal:{
                  color:'#334455'
              }
          },
          label: {
              normal: {
                  show: true,
                  position: 'top'
              }
          },
          type: 'bar',
          data: []
      }]
  });

  myReleaseChart.showLoading();    //数据加载完之前先显示一段简单的loading动画

  var names = [];    //类别数组（实际用来盛放X轴坐标值）
  var nums = [];    //销量数组（实际用来盛放Y坐标值）
  var fnums = [];    //销量数组（实际用来盛放Y坐标值）
  $.ajax({
      type: "get",
      async: true,            //异步请求（同步请求将会锁住浏览器，用户其他操作必须等待请求完成才可以执行）
      url: "getReleaseCount.do",    
      data: {},
      dataType: "json",        //返回数据形式为json
      success: function (result) {
    	  //alert(result)
          //请求成功时执行该函数内容，result即为服务器返回的json对象
          if (result) {
              for (var i = 0; i < result.length; i++) {
                  //alert(result[i].name);
                  names.push(result[i].release);    //挨个取出类别并填入类别数组
                  nums.push(result[i].cc);    //挨个取出销量并填入销量数组
                  fnums.push(result[i].ff);
              }
              myReleaseChart.hideLoading();    //隐藏加载动画
              myReleaseChart.setOption({        //加载数据图表
                  xAxis: {
                      data: names
                  },
                  series: [{
                      // 根据名字对应到相应的系列
                      name: 'Fail Count',
                      data: fnums
                  },{
                      // 根据名字对应到相应的系列
                      name: 'Successful Count',
                      data: nums
                  }]
              });

          }

      },
      error: function (errorMsg) {
          //请求失败时执行该函数
          alert("chart ajax fail!");
          myReleaseChart.hideLoading();
      }
  })
})