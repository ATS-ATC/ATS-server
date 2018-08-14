<%@ page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<link href="./bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="./jquery/jquery-3.2.1.js"></script>
<script src="./bootstrap/js/bootstrap.min.js"></script>
<script src="./js/echarts.js"></script>
<script src="./js/dark.js"></script>
<script src="./js/macarons.js"></script>
<!-- <meta http-equiv="refresh" content="5*60"> -->
<script type="text/javascript">
	setTimeout("self.location.reload();",30000);
	$(document).ready(function(){
	    // 基于准备好的dom，初始化echarts实例
	    var myChart = echarts.init(document.getElementById('main'),"macarons");
	    //var myDate = new Date();
	    //var myTime = myDate.toLocaleString(); //获取日期与时间
	    // 指定图表的配置项和数据
	    var option = {
	    	    title: {
	    	        x: 'center',
	    	        text: 'Case Status',
	    	        subtext: 'Case Status Rainbow bar : '+Date(),
	    	        link: 'http://http://localhost:8080/weblab/'
	    	    },
	    	    tooltip: {
	    	        trigger: 'item'
	    	    },
	    	    toolbox: {
	    	        show: true,
	    	        feature: {
	    	            dataView: {show: true, readOnly: false},
	    	            restore: {show: true},
	    	            saveAsImage: {show: true}
	    	        }
	    	    },
	    	    calculable: true,
	    	    grid: {
	    	        borderWidth: 0,
	    	        y: 80,
	    	        y2: 60
	    	    },
	    	    yAxis: [
	    	        {
	    	            type: 'category',
	    	            show: false,
	    	            nameLocation:'start',
	    	            data: [ 'Pending','Pre-Pending','Obsolete','Resubmit','Initial','Failed','Successful','Total']
	    	        }
	    	    ],
	    	    xAxis: [
	    	        {
	    	            type: 'value',
	    	            show: true
	    	        }
	    	    ],
	    	    series: [
	    	        {
	    	            name: 'Case Status',
	    	            type: 'bar',
	    	            itemStyle: {
	    	                normal: {
	    	                    color: function(params) {
	    	                        // build a color map as your need.
	    	                        var colorList = [
	    	                          '#C1232B','#B5C334','#FCCE10','#E87C25','#27727B',
	    	                           '#FE8463','#9BCA63','#FAD860','#F3A43B','#60C0DD',
	    	                           '#D7504B','#C6E579','#F4E001','#F0805A','#26C0C0'
	    	                        ];
	    	                        return colorList[params.dataIndex]
	    	                    },
	    	                    label: {
	    	                        show: true,
	    	                        position: 'right',
	    	                        formatter: '{b}\n{c}'
	    	                    }
	    	                }
	    	            },
	    	            data: [${P},${PP},${O},${R},${I},${F},${S},${T}]
	    	        }
	    	    ]
	    	};
	
	    // 使用刚指定的配置项和数据显示图表。
	    myChart.setOption(option);
	 });
</script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/echart-case-customer.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/echart-case-release.js"></script>
<title>Home</title>
</head>
<body style="background-color:#ECEFF3;padding-top:27px;">
	<div class="container-fluid">
		<div class="row clearfix" >
			<div class="col-md-6 column" style="padding-right:0px;">
				<div id="main" style="width: 100%;height:400px;background-color:#FFFFFF"></div>
			</div>
			<div class="col-md-6 column">
				<div id="container" style="width: 100%; height: 400px;  margin: 0 auto;background-color:#FFFFFF""></div>
			</div>
		</div>
		<div class="row clearfix" style="padding-top:12px;">
			<div class="col-md-12 column">
				<div id="foot" style="width: 100%; height: 400px;  margin: 0 auto;background-color:#FFFFFF""></div>
			</div>
		</div>
		<div class="row clearfix" style="padding-top:12px;">
			<div class="col-md-12 column">
				<div id="release" style="width: 100%; height: 400px;  margin: 0 auto;background-color:#FFFFFF""></div>
			</div>
		</div>
	</div>

	<script language="JavaScript">
	$(document).ready(function(){
	    // 基于准备好的dom，初始化echarts实例
	    var myContainer = echarts.init(document.getElementById('container'),"macarons");
		//app.title = 'Case Status Pie';
	
		var option2 = {
		    tooltip: {
		        trigger: 'item',
		        formatter: "{a} <br/>{b}: {c} ({d}%)"
		    },
		    toolbox: {
    	        show: true,
    	        feature: {
    	            dataView: {show: true, readOnly: false},
    	            restore: {show: true},
    	            saveAsImage: {show: true}
    	        }
    	    },
    	    legend: {  
                orient: 'vertical',  
                left: 'left',  
                x : 'left',  
                y : 'bottom'  
            }, 
		    series: [
		        {
		            name:'case status',
		            type:'pie',
		            selectedMode: 'single',
		            radius: [0, '30%'],
	
		            label: {
		                normal: {
		                    position: 'inner'
		                }
		            },
		            labelLine: {
		                normal: {
		                    show: true
		                }
		            },
		            data:[
		                /* {value:${F}, name:'Failed', selected:true},
		                {value:${I}, name:'Initial'}, 
		                {value:${S}, name:'Case Rate'}*/
		            ]
		        },
		        {
		            name:'case status',
		            type:'pie',
		            //minAngle: 10,
		            avoidLabelOverlap: true,
		            radius: ['40%', '55%'],
		            label: {
		                normal: {
		                    //formatter: '{a|{a}}{abg|}\n{hr|}\n  {b|{b}：}{c}  {per|{d}%}  ',
		                    formatter: '{hr|}\n  {b|{b}：}{c}  {per|{d}%}  ',
		                    backgroundColor: '#eee',
		                    borderColor: '#aaa',
		                    borderWidth: 1,
		                    borderRadius: 2,
		                    // shadowBlur:3,
		                    // shadowOffsetX: 2,
		                    // shadowOffsetY: 2,
		                    // shadowColor: '#999',
		                    //padding: [0, 7],
		                    rich: {
		                        a: {
		                            color: '#999',
		                            align: 'center'
		                        },
		                      	abg: {
		                             backgroundColor: '#333',
		                             width: '100%',
		                             align: 'right',
		                             height: 22,
		                             borderRadius: [4, 4, 0, 0]
		                         }, 
		                        hr: {
		                            borderColor: '#aaa',
		                            width: '100%',
		                            borderWidth: 0.5,
		                            height: 0
		                        },
		                        b: {
		                            fontSize: 10,
		                            lineHeight: 20//用于控制饼图边指示框大小，初始为33
		                        },
		                        per: {
		                            color: '#eee',
		                            backgroundColor: '#334455',
		                            padding: [2, 4],
		                            borderRadius: 2
		                        }
		                    }
		                }
		            }, 
		            /* labelLine: {
			            show: true
			        }, */
		            data:[
		                {value:${P}, name:'Pending'},
		                {value:${O}, name:'Obsolete'},
		                {value:${PP}, name:'Pre-Pending'}, 
		                {value:${R}, name:'Resubmit'},
		                {value:${S}, name:'Successful'},
		                {value:${F}, name:'Failed'},
		                {value:${I}, name:'Initial'} 
		            ]
		        }
		    ]
		};
		
		myContainer.on('click', function (param) {
		    	var index = param.dataIndex;
		    	var qtype='';
		    	switch(index){
		    		case 0:
		    			qtype='P'
		    			break;
		    		case 1:
		    			qtype='O'
	    				break;
		    		case 2:
		    			qtype='PP'
	    				break;
		    		case 3:
		    			qtype='R'
	    				break;
		    		case 4:
		    			qtype='S'
	    				break;
		    		case 5:
		    			qtype='F'
	    				break;
		    		case 6:
		    			qtype='I'
	    				break;
		    	}
		    	if(""!=qtype){
		    		//alert(qtype);
		    		window.location.href='getQueryCaseInfo.do?qtype='+qtype;
		    	}
		 }); 
		 // 使用刚指定的配置项和数据显示图表。
	    myContainer.setOption(option2);
	 });
	</script>
	
</body>
</html>