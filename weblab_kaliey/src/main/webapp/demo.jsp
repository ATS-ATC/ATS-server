<script type='text/javascript'>

window.onload = function(){
　　alert("页面加载完成====》onload");
}

$(window).load(function(){
　　alert("jquery===》window load" );
})

$(document).ready(function () {
　　alert("jquery====》document ready");
});

$(function(){
　　alert("jquery====》document onload");
});

function aaa(){
　　alert("静态标签====》onload");
}
</script>

<body onload="aaa()">

</body>