$(function () {
    $("#topBtn").click(setTop);
    $("#wonderfulBtn").click(setWonderful);
    $("#deleteBtn").click(setDelete);
});

function like(btn, entityType, entityId, entityUserId, postId) {
    $.post
    (
        CONTEXT_PATH + "/like",
        {"entityType": entityType, "entityId": entityId, "entityUserId": entityUserId, "postId": postId},
        function (data) {
            data = $.parseJSON(data);
            if (data.code == 0) {
                $(btn).children("i").text(data.likeCount);
                $(btn).children("b").text(data.likeStatus == 1 ? '已赞' : '赞');
            } else {
                alert(data.msg);
            }
        }
    )
}

function setTop() {
    $.ajax({
        url: CONTEXT_PATH + "/discuss/top",
        type: "POST",
        data: {"id": $("#postId").val()},
        success: function (data) {
            if (typeof data === 'string') {
                data = $.parseJSON(data);
            }
            if (data.code == 0) {
                $("#topBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("请先登录");
            } else if (xhr.status === 403) {
                alert("权限不足，需要版主权限");
            } else {
                alert("操作失败，请重试");
            }
        }
    });
}

function setWonderful() {
    $.ajax({
        url: CONTEXT_PATH + "/discuss/wonderful",
        type: "POST",
        data: {"id": $("#postId").val()},
        success: function (data) {
            if (typeof data === 'string') {
                data = $.parseJSON(data);
            }
            if (data.code == 0) {
                $("#wonderfulBtn").attr("disabled", "disabled");
            } else {
                alert(data.msg);
            }
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("请先登录");
            } else if (xhr.status === 403) {
                alert("权限不足，需要版主权限");
            } else {
                alert("操作失败，请重试");
            }
        }
    });
}

function setDelete() {
    $.ajax({
        url: CONTEXT_PATH + "/discuss/delete",
        type: "POST",
        data: {"id": $("#postId").val()},
        success: function (data) {
            if (typeof data === 'string') {
                data = $.parseJSON(data);
            }
            if (data.code == 0) {
                location.href = CONTEXT_PATH + "/index";
            } else {
                alert(data.msg);
            }
        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("请先登录");
            } else if (xhr.status === 403) {
                alert("权限不足，需要管理员权限");
            } else {
                alert("操作失败，请重试");
            }
        }
    });
}