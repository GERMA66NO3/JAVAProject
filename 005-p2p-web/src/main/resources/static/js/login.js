var referrer = "";//登录后返回页面
referrer = document.referrer;
if (!referrer) {
	try {
		if (window.opener) {                
			// IE下如果跨域则抛出权限异常，Safari和Chrome下window.opener.location没有任何属性              
			referrer = window.opener.location.href;
		}  
	} catch (e) {
	}
}

//按键盘Enter键即可登录
$(document).keyup(function(event){
	if(event.keyCode == 13){
		login();
	}
});


//点获取验证码时对手机号，密码的验证
$(function () {
	$("#messageCodeBtn").click(
		function () {
			//对手机号码验证
			var phone = $.trim($("#phone").val());

			var reg = /^1[3-9]\d{9}$/;
			if(phone == ""){
				$("#showId").html("手机号码不能为空");
				return ;
			}
			if(phone.length != 11){
				$("#showId").html("手机号码必须是11位");
				return ;
			}

			if(!reg.test(phone)){
				$("#showId").html("请输入正确的手机号码");
				return ;
			}

			//对密码验证

			var loginPassword = $.trim($("#loginPassword").val());
			if(loginPassword == ""){
				$("#showId").html("密码不能为空");
				return;
			}


			//倒计时
			var _this=$(this);
			if(!$(this).hasClass("on")){
				//发送短信
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



	)
	
});


function Login() {

	//对手机号码验证
	var phone = $.trim($("#phone").val());

	var reg = /^1[3-9]\d{9}$/;
	if(phone == ""){
		$("#showId").html("手机号码不能为空");
		return ;
	}
	if(phone.length != 11){
		$("#showId").html("手机号码必须是11位");
		return ;
	}

	if(!reg.test(phone)){
		$("#showId").html("请输入正确的手机号码");
		return ;
	}

	//对密码验证

	var loginPassword = $.trim($("#loginPassword").val());
	if(loginPassword == ""){
		$("#showId").html("密码不能为空");
		return;
	}


	//对验证码验证
	var messageCode = $.trim($("#messageCode").val());
	if(messageCode == ""){
		$("#showId").html("验证码不能为空");
		return ;
	}

	if(messageCode.length!= 6){
		$("#showId").html("验证码必须是6位");
		return ;
	}

	$.ajax({
		type: "post",
		url: "/005-p2p-web/loan/page/loginSubmit",
		data: {"phone":phone,"password":$.md5(loginPassword),"messageCode":messageCode},
		success: function(data){
			if(data.code=="1"){
				window.location.href=$("#ReturnUrl").val();
			}
			if(data.code=="0"){
				$("#showId").html(data.msg);
			}
		},
		error:function(msg){
			$("#showId").html( "系统维护中" );

		}
	});
}



