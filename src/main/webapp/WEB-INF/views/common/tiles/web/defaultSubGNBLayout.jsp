<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<div class="new2023">
    <div class="sub-visual">
        <div class="visual-box">
            <div class="visual">
                <h2 id="subTitle"></h2>
                <p id="subDesc"></p>
            </div>
        </div>
    </div>
</div>
<script nonce="${cspNonce}">
    var url = location.href.split(location.host)[1];
    var temp = url.split('?');
    if (temp.length > 1) {
        url = temp[0];
    }

    $(document).ready(function () {
        $.ajax({
            url: '/a/getMenuInfoAjax2',
            async: true,
            type: 'POST',
            timeout: 3000,
            dataType: "json",
            data: JSON.stringify({
                url: url
            }),
            error: function (e) {
                //console.log(e);
            },
            success: function (data) {
                console.log(data)
                var subTitle = data.menu.menuNm;
                var subText = data.menu.menuText;
                if (data.menu.linkUrl === '/educontents/genius/genieList') {
                    subTitle = '지니튜브';
                }
                $("#subTitle").html(subTitle);
                $("#subDesc").html(subText);

                if (data.menu.imgFileNm !== '' && data.menu.imgFileNm != null) {
                    if (data.serverType === 'local') {
                        return;
                    }
                    $(".sub-visual").css('background', 'url(' + data.menu.imgFileNm + ')');
                    $(".sub-visual").css('background-repeat', 'no-repeat');
                    $(".sub-visual").css('background-position', 'center');
                    $(".sub-visual").css('background-size', 'cover');
                }
            }
        });
    });
</script>