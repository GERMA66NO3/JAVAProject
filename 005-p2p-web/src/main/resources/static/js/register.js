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

//注册协议确认
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

//追踪变量
var phone_tag=0;
var passwd_tag=0;
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

		$.ajax({
			type: "get",
			url: "/005-p2p-web/loan/page/checkPhone",
			data: {"phone":phone},
			success: function(data){

				if(data.code=="1"){
					//alert("手机号码可以注册" );
					showSuccess("phone");
					phone_tag=1;
				}

				if(data.code=="0"){
					//alert("手机号码已被占用");
					showError("phone","手机号码已被占用");
					phone_tag=0;
				}

			},
			error:function(msg){
				alert( "系统维护中" );
				phone_tag=0;
			}
		});

	});

	$("#loginPassword").blur(function(){
		hideError("loginPassword");
		var loginPassword=$.trim($("#loginPassword").val());

		if(loginPassword==""){
			showError("loginPassword","密码不能为空");
			passwd_tag=0;
			return ;
		}

		if(!/^[0-9a-zA-Z]+$/.test(loginPassword)){
			showError("loginPassword","密码只能用数字和大小写英文字母");
			passwd_tag=0;

			return ;
		}

		if(!/^(([a-zA-Z]+[0-9]+)|([0-9]+[a-zA-Z]+))[a-zA-Z0-9]*/.test(loginPassword)){
			showError("loginPassword","密码应同时包含英文和数字");
			passwd_tag=0;
			return ;
		}
		if(loginPassword.length<6||loginPassword.length>20){
			showError("loginPassword","密码长度不能低于6位或者高于20位");
			passwd_tag=0;
			return ;
		}
		showSuccess("loginPassword");
		passwd_tag=1;
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
		$("#loginPassword").blur();

		// do{
		// 	$("#phone").blur();
		// }while(phone_tag==0)
		// if(tag){
		//
		// }
		// alert("phone_tag-->"+phone_tag);
		// alert("passwd_tag--->"+passwd_tag);
		if(phone_tag==1&&passwd_tag==1&&auth_tag==1){
			var phone=$.trim($("#phone").val());
			var loginPassword=$.trim($("#loginPassword").val());
			var messageCode=$.trim($("#messageCode").val());
			$.ajax({
				type: "post",
				url: "/005-p2p-web/loan/page/registSubmit",
				data: {"phone":phone,"loginPassword":$.md5(loginPassword),"messageCode":messageCode},
				success: function(data){

					if(data.code=="1"){
						window.location.href="/005-p2p-web/loan/page/realName";
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
	$("#messageCodeBtn").click(function(){
		//模拟触发失去焦点事件
		$("#phone").blur();
		$("#loginPassword").blur();
		var _this=$(this);
		if(phone_tag==1&&passwd_tag==1){

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




});
