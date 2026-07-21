<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<script src="/resources/legacy/newPortal/js/mobileShare.js"></script>
<script src="/resources/legacy/anymobi/mobile/js/gnb.js"></script>
<script src="/resources/legacy/newPortal/js/new23_m.js"></script>
<style>
	.icon.btn {
		position: relative;
    	float: left;
    	top: 27px;
    	left:8px;
	}
</style>
<div class="l-gnb main-type">
	<div class="gnb">
		<div class="gnb-top">   	
            <div class="left gnb-menu">
           		<button type="button" class="btn m-gnb-back" onclick="javascript:history.back();">
           			<span class="hide">이전</span>
           		</button>
           		<button type="button" class="btn m-gnb-home" onclick="javascript:CtlExecutor3.goMain();">
           			<span class="hide">홈으로 이동</span>
           		</button>
            </div>
            <div class="center">
				<p class="gnb-category"></p> <!-- 메뉴명 -->
			</div>
            <div class="right">
            	<c:if test="${comp ne '8888'}">
            	<button type="button" class="btn refresh-btn" onclick="javascript:location.reload();"><span class="hide">새로고침</span></button>
            	<button type="button" class="btn search-btn"><span class="hide">통합검색</span></button>
            	</c:if>
            </div>
		</div>
	</div>
</div>
<%@ include file="/WEB-INF/views/common/tiles/mobile/KT_SearchPop.jsp" %><!-- 통합검색 팝업 -->

<input type="hidden" value="<c:out value='${comp}'/>" id="comp" />
<script nonce="${cspNonce}">
var preUrl = getPreURL();
var CtlExecutor3 = {
		goMain : function() {
			location.href = "/mobile/m" + '<c:url value="/main/portalMain" />';
		},
		goApplyList : function() {
			location.href = "/mobile/m/myclass/course/myCourse/myCourseList?eduStep=BEF";
		},
		goIngList : function() {
			location.href = "/mobile/m/myclass/course/myCourse/myCourseList?eduStep=MID";
		},
		goFinishList : function() {
			location.href = "/mobile/m/myclass/course/myCourse/myCourseList?eduStep=AFT";
		}
		
};

var url = location.href.split(location.host)[1];
var temp = url.split('?');
if(temp.length > 1){
	url = temp[0];
}
if(url.indexOf("/mobile/t") != -1){
	var tempUrl = url.replace('/mobile/t', '/mobile/m');
	url = tempUrl;
}

$(function () {
	
	// 메뉴명 get
	$.ajax({
		url : '/mobile/m/a/getMenuInfoAjax',
		async : true,
		type : 'POST',
		timeout : 3000,
		dataType : "json",
		data: JSON.stringify({url : url}),
		error : function(e) {
			//console.log(e);
		},
		success : function(data) {
			if(data.menu != null) {
				var menuTitle = data.menu.menuNm;
				$(".gnb-category").html(menuTitle);
			} else {
				var logoHtml = "";
				$(".gnb-top .center").empty();
				logoHtml += '<h1 class="gnb-logo">';
				logoHtml += '	<a href="javascript:void(0);" onclick="javascript:CtlExecutor3.goMain();"><span class="hide">kt 그룹교육 포털 로고</span></a>';
				logoHtml += '</h1>';
				$(".gnb-top .center").append(logoHtml);
			}
		}
 	});
	
	$("body").append("<div id='dimbox'></div>");
	$('.condition-btn').popup({
        target: $('.l-popup.condition')
	})
	$('.learning-btn').click(function(){
  	  $('#dimbox').show();
  	  $(".l-popup.learning").css('display','block');
    });
	$(".l-popup.learning .close-btn").click(function(){
  	  $('#dimbox').hide();
  	  $(".l-popup.learning").css('display','none');
    });
	
});

</script>