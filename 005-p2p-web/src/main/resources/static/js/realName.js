
//同意实名认证协议
$(function() {
	$("#agree").click(function(){
		var ischeck = document.getElementById("agree").checked;
		if (ischeck) {
			$("#btnRegist").attr("disabled", false);
			$("#btnRegist").removeClass("fail");
		} else {
			$("#btnRegist").attr("disabled","disabled");
			$("#btnRegist").addClass("fail");
		}
	});
});
//打开注册协议弹层
function alertBox(maskid,bosid){
	$("#"+maskid).show();
	$("#"+bosid).show();
}
//关闭注册协议弹层
function closeBox(maskid,bosid){
	$("#"+maskid).hide();
	$("#"+bosid).hide();
}

//错误提示
function showError(id,msg) {
	$("#"+id+"Ok").hide();
	$("#"+id+"Err").html("<i></i><p>"+msg+"</p>");
	$("#"+id+"Err").show();
	$("#"+id).addClass("input-red");
}
//错误隐藏
function hideError(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id).removeClass("input-red");
}
//显示成功
function showSuccess(id) {
	$("#"+id+"Err").hide();
	$("#"+id+"Err").html("");
	$("#"+id+"Ok").show();
	$("#"+id).removeClass("input-red");
}



//追踪变量
var phone_tag=0;
var realName_tag=0;
var idCard_tag=0;
var auth_tag=0;

$(function(){
	$("#phone").blur(function(){
		//phone_tag=0;
		hideError("phone");
		var phone=$.trim($("#phone").val());
		if(phone==""){
			showError("phone","手机号码不能为空");
			phone_tag=0;
			return ;
		}
		if(phone.length!=11){
			showError("phone","手机号码必须是11位长度");
			phone_tag=0;
			return ;
		}
		if(!/^1[1-9]\d{9}$/.test(phone)){
			showError("phone","不符合手机号码格式");
			phone_tag=0;
			return ;
		}
		phone_tag=1;
		showSuccess("phone");
	});

	$("#realName").blur(function(){
		hideError("realName");
		var realName=$.trim($("#realName").val());
		if(realName==""){
			showError("realName","姓名不能为空");
			realName_tag=0;
			return ;
		}
		if(!/^[\u4e00-\u9fa5]{0,}$/.test(realName)){
			showError("realName","必须是中文名称");
			realName_tag=0;
			return ;
		}
		realName_tag=1;
		showSuccess("realName");
	});

	$("#idCard").blur(function(){
		hideError("idCard");
		var idCard=$.trim($("#idCard").val());
		if(idCard==""){
			showError("idCard","姓名不能为空");
			idCard_tag=0;
			return ;
		}
		if(!/(^\d{15}$)|(^\d{18}$)|(^\d{17}(\d|X|x)$)/.test(idCard)){
			showError("idCard","请输入15位或18位身份证");
			idCard_tag=0;
			return ;
		}
		idCard_tag=1;
		showSuccess("idCard");
	});

	$("#messageCodeBtn").click(function(){
		//模拟触发失去焦点事件
		$("#phone").blur();
		$("#realName").blur();
		$("#idCard").blur();

		var _this=$(this);
		if(phone_tag==1&&realName_tag==1&&idCard_tag==1){

			var phone=$.trim($("#phone").val());
			if(!$(this).hasClass("on")){

				$.ajax({
					type: "get",
					url: "/005-p2p-web/loan/page/messageCode",
					data: {"phone":phone},
					success: function(data){
						if(data.code=="1"){
							//倒计时
							$.leftTime(60,function(d){
								if(d.status){
									_this.addClass("on");
									_this.html((d.s=="00"?"60":d.s)+"秒后重新获取");
								}else{
									_this.removeClass("on");
									_this.html("获取验证码");
								}
							});

							alert(data.msg);
						}
						if(data.code=="0"){
							alert(data.msg);
						}
					},
					error:function(msg){
						alert( "系统维护中" );

					}
				});
			}

		}

	});

	$("#messageCode").blur(function(){
		var messageCode=$.trim($("#messageCode").val());
		if(messageCode==''){
			showError("messageCode","请输入验证码");
			return ;
		}
		if(messageCode.length!=6){
			showError("messageCode","验证码长度必须是6位");
			return ;
		}
		showSuccess("messageCode");
		auth_tag=1;
	});


	$("#btnRegist").click(function(){
		//模拟触发失去焦点事件
		$("#phone").blur();
		$("#realName").blur();
		$("#idCard").blur();
		$("#messageCode").blur();

		if(phone_tag==1&&realName_tag==1&&idCard_tag==1&&auth_tag==1){
			var phone=$.trim($("#phone").val());
			var realName=$.trim($("#realName").val());
			var idCard=$.trim($("#idCard").val());
			var messageCode=$.trim($("#messageCode").val());
			$.ajax({
				type: "post",
				url: "/005-p2p-web/loan/page/realNameSubmit",
				data: {"phone":phone,"realName":realName,"idCard":idCard,"messageCode":messageCode},
				success: function(data){

					if(data.code=="1"){
						window.location.href="/005-p2p-web/index";
					}

					if(data.code=="0"){
						alert(data.msg);
					}

				},
				error:function(msg){
					alert( "系统维护中" );

				}
			});

		}

	});
});
