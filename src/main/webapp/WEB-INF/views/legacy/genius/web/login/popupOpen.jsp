<%--
  Migrated from genius: WebContent/WEB-INF/jsp/web/login/popupOpen.jsp
  Encoding converted to UTF-8. This file is kept as legacy lineage/reference;
  active login is routed through Spring Security + JWT in /WEB-INF/views/login/login.jsp.
--%>
<%@ page pageEncoding="UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ page isELIgnored="false"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<!DOCTYPE HTML>
<!--[if IE 8 ]><html class="ie8" lang="ko"><![endif]-->
<html lang="ko">

<head>
	<!--[if lt IE 9]>
		<script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
		<script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
	<![endif]-->
	<meta charset="utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<meta name="viewport" content="width=device-width,initial-scale=1.0,minimum-scale=1.0,maximum-scale=1.0,user-scalable=no">
	<meta name="author" content="AnyMobi-G" />
	<title>KT학습플랫폼 지니어스</title>
	<link rel="stylesheet" type="text/css" href="/anymobi/css/style.css" />
	<script src="/anymobi/lib/js/jquery/jquery-1.11.3.min.js"></script>
	
	<link rel="stylesheet" type="text/css" href="/anymobi/css/main.css" />
	<script src="/anymobi/lib/iCheck-master/iCheck-master.min.js"></script>
	<script>		
		$(document).ready(function() {

			// 행복한 도서관 바로가기
			$(".j-go-library").click(function() {
				window.close();
			});

			// 오늘 다시 열지 않음
			$(".cj-iCheck input").iCheck({
				checkboxClass: "cj-input-chkbox"
			});

			// "오늘 다시 열지 않음" 체크한 경우
			$(".cj-iCheck input").on("ifChecked", function() {
  				closePop();
			});
			
			// 이러닝 신청하러 가기
			$(".j-go-elearnning").click(function() {
				//window.open("http://news.mk.co.kr/newsRead.php?no=678424&year=2016");
				opener.location.href="/educontents/hrdIbox/hrdIboxDetail?num=294";
				self.close();
			});
			
		});
		function setCookie(name, value, expiredays){
			
			var todayDate = new Date();   
		    todayDate = new Date(parseInt(todayDate.getTime() / 86400000) * 86400000 + 54000000);  
		    if ( todayDate > new Date() )  
		    {  
		    	expiredays = expiredays - 1;  
		    }  
		    todayDate.setDate( todayDate.getDate() + expiredays );   
		    document.cookie = name + "=" + escape( value ) + "; path=/; expires=" + todayDate.toGMTString() + ";" 
			
   /*       var todayDate = new Date();
            todayDate.setDate (todayDate.getDate() + expiredays);
            document.cookie = name + "=" + escape(value) + "; path=/; expires=" + todayDate.toGMTString() + ";"; */
        }

	    function closePop(){
	            setCookie("popname", "done", 1);
	            self.close();
	    }

	    function fileDown(saveFile, realFile){
   			$("#downloadFrm").remove();
   			$("body").append('<form id="downloadFrm" method="post"></form>');
   			var $frm = $("#downloadFrm");
   			hiddenMaker("p_savefile", saveFile, "downloadFrm");
   			hiddenMaker("p_realfile", escape(encodeURIComponent(realFile)), "downloadFrm");
   			hiddenMaker("dir", "..", "downloadFrm");
   			
   			$frm.attr('action', '/servlet/controller.library.DownloadServlet').submit();
   		}
   		function hiddenMaker(name, value, formId){
   			var inputTag = "<input type='hidden' name='"+name+"' value='"+value+"'/>";
   			$("#"+formId).append(inputTag);
   		}
	</script>
</head>

<body class="popup-main">
	<!--// 수정_2016-04-10 : 시작 -->
	<div  class="popup-con" style="width:427px;height:680px;">		<!-- 가로: 500px, 세로: 630px -->
<a class="j-go-elearnning" href="javascript:void(0)"><img src="/anymobi/img/popup/popup_ai_20201026.png" alt=""/></a>
<!-- <a class="j-go-elearnning" href="javascript:void(0)"><img src="/anymobi/img/main/20160707_event_popup.jpg" alt=""/></a> -->
		<!-- <h3><span>kt그룹교육포털 리뉴얼 오픈</span></h3>
		<div class="notice">
			<p>안녕하십니까.<br>KT교육포털이 전 그룹사 임직원이 함께 사용할 수 있도록 '그룹교육포털'로<br>새롭게 출발합니다. (그룹사 오픈은 4월중 그룹사별 단계적 오픈 예정)</p>
			<p>새롭게 단장한 미니강좌 콘텐츠와 교육과정 신청/조회 기능을 보완하였으며, 본인의 자기개발계획 수립현황, 직무역량진단 결과 등을 연중 상시 조회하실 수 있습니다.</p>
			<p>그룹교육포털에 임직원 여러분들의 많은 관심과 이용 부탁드립니다.<br>감사합니다.</p>
			<span>그룹인재개발아카데미 拜上</span>
		</div>
		<div class="btn-con clearfix">
			<a href = 'javascript:fileDown("user_guide_v.1.6.0.pdf", "user_guide_v.1.6.0.pdf")'>사용자 가이드 다운로드</a>
			<a href = 'javascript:fileDown("admin_guide_v.1.5.1.pdf", "admin_guide_v.1.5.1.pdf")'>운영자 가이드 다운로드</a> 
		</div> -->
		<div class="chk-today">
			<label class="cj-iCheck" style="color:#000000"><input type="checkbox" name="learn-act">오늘은 이 창을 다시 열지 않음</label>
		</div>
	</div> 
<!-- 수정_2016-04-10 : 끝 //-->

</body>

</html>
