<%-- 모바일 통합검색 팝업 (KT_GNB_Mobile / KT_GNB_Mobile_Main include 대상, 프래그먼트) --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<div class="l-popup dim search">
    <div class="popup full">
      <button type="button" class="close-btn"></button>
      <div class="l-search">
        <div class="title">
          <h4>통합검색</h4>
        </div>
        <div class="form-wrap">
          <form name="totalSearchForm" id="totalSearchForm" method="post" onsubmit="return false">
            <div class="search-form">
              <div class="inp">
                <input type="text" name="swordNm" id="swordNm" placeholder="검색어를 입력하세요" autocomplete="off">
                <input type="hidden" name="objType" id="objType">
                <input type="hidden" name="matchTypeGnb" id="matchTypeGnb"/>
                <button type="button" class="btn search-icon" id="searchBtn"><span>검색</span></button>
              </div>
              <div class="recent-search">
              	 <label class="checkbox">
                     <input type="checkbox" id="checkGnbMatchType">
                     <i></i>
                     <span>정확하게 일치</span>
                 </label>
                <div class="serch-sub-title"><span>최근 검색어</span></div>
                <ul class="recent-search-ul"></ul>
                <button type="button" class="btn all-reset"><span>전체삭제</span></button>
              </div>
              <div class="popular-search">
                <div class="serch-sub-title"><span>인기 검색어</span></div>
                <ul class="search-hashtag-ul"></ul>
              </div>
            </div>
          </form>
        </div>
      </div>
    </div>
  </div>
<script nonce="${cspNonce}">
var preUrl = getPreURL();
$(document).ready(function(){

	// 정확하게 일치(체크박스) 체크할시 발동로직
	var checkGnbMatchbox = document.getElementById("checkGnbMatchType");
	checkGnbMatchbox.addEventListener('change',function(){
		var hiddenMatchInput = document.getElementById("matchTypeGnb");
		if(checkGnbMatchbox.checked){
			hiddenMatchInput.value = "EXACT";
		} else{
			// ""이 전체
			hiddenMatchInput.value = "";
		}
	});

	swordList();
	//해시태그, 최근검색어 클릭
	$(document).on('click', ".btn-htag, .search-list", function(){
		var form = document.getElementById("totalSearchForm");
		var swordNm = $(this).children().text();
		var objType = "SWORD";
		swordNm = swordNm.replace('#','');
		if($(this).attr("class") == "btn-htag"){
			objType="HTAG";
		}
		$("#objType").val(objType);
		$("#swordNm").val(swordNm);
		var checkGnbMatchbox = document.getElementById("checkGnbMatchType");
		var checkMatchbox = document.getElementById("checkMatchType");
		form.action= preUrl+"/search/searchDetail";
		form.method="post";
		form.submit();
	});

	//삭제버튼
	$(document).on('click','button.close, .all-reset', function(){
		var isAll = false;
		if($(this).attr('class') == 'btn all-reset'){
			isAll = true;
		}
		var map = {};
		map["swordNm"] = escape(encodeURIComponent($(this).text()));
		map["isAll"] = isAll;

		$.ajax({
			url : preUrl+"/a/search/swordResetAjax",
			async : false,
			type : 'POST',
			timeout : 3000,
			dataType : "json",
			data : JSON.stringify(map),
			error : function(e) {
				//console.log(e);
			},
			success : function(data) {
				swordList();
			}
		});
	});

//검색버튼
function search(){
	var form = document.getElementById("totalSearchForm");
	var firstsword = $("#swordNm").val();
	var sword = firstsword.trimStart();
    sword = sword.replace(/[^a-zA-Z0-9가-힝\s]/g,'');
	sword = sword.trimEnd();
	sword = sword.replace(/\s+/g,' ').trim();
// 	var specialCharPattern = /^[!@#|$%|^|&*|)|(+=._-]+$/;
	if(sword.trim() == "" || sword.length <2){
		layerAlert("검색어는 두 글자 이상 입력해 주십시오.");
		return;
	}else {
		$("#swordNm").val(sword);
		var objType = "SWORD";
		$("#objType").val(objType);
		form.action = preUrl+"/search/searchDetail";
		form.method="post";
		form.submit();
	}


}
$("#searchBtn").click(function(){
	search();
});

//최근 검색 리스트 ajax
function swordList(){
	$.ajax({
		url : preUrl+'/a/search/swordListAjax',
		async : true,
		type : 'POST',
		timeout : 3000,
		dataType : "json",
		//data : JSON.stringify(requestData),
		error : function(e) {
			//console.log(e);
		},
		success : function(data) {
			if (data.swordList.length > 0) {
				var swordList = "";
				for (var i = 0; i < data.swordList.length; i++) {
					var sword = data.swordList[i];
					swordList += '<li class="recent-search-list">';
					swordList += '<button type="button" class="btn search-list">';
					swordList += '<span>'+sword.swordNm+'</span>';
					swordList += '</button>';
					swordList += '<button type="button" class="btn close">'+sword.swordNm+'</button>';
					swordList += '</li>';
				}
				$(".recent-search-ul").eq(0).html(swordList);
			}else{
				$(".recent-search-ul").eq(0).html('');
			}
			if (data.htagList.length > 0) {
				var htagList = "";
				for (var i = 0; i < data.htagList.length; i++) {
					var htag = data.htagList[i];
					htagList += ' <li class="popular-search-list">';
					htagList += ' <button type="button" class="btn-htag"><span>#'+ htag.htagNm + '</span></button>';
					htagList += ' </li>';
				}
				$(".search-hashtag-ul").eq(0).html(htagList);

			}
		}
	});
};
});
</script>
