$(function() {

	// 菜单事件
	$(".list-group .list-group-item").click(function() {
 
		var url = $(this).attr("url");
		// 先移除其他的点击样式，再添加当前的点击样式
		$(".list-group .list-group-item").removeClass("list-selected");
		$(this).addClass("list-selected");
 
		// 加载其他模块的页面到右侧工作区
		 $.ajax({ 
			 url: url, 
			 success: function(data){
				 $("#rightContainer").html(data);
		 },
		 error : function() {
		     alert("error");
		     }
		 });
	});
	
	// 选中菜单第一项
	 $(".list-group .list-group-item:first").trigger("click");
});