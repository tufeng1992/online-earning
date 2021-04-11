var appId = 'aaa';
var preUrl = 'http://aaa.com/';
var appDownLoadURL = '';

function onSms() {
  // const express = /^1\d{10}$/;
  const tel = $('#input1').val();
  // if (!tel || !express.test(tel)) {
  if (!tel) {
    popupsBox('Please enter phone number')
    return;
  }
  request({url: 'sms', data: { appId, tel }}).then(res => {
    if (res) countDown();
  })
}

function onRegister() {
  const mobile = $('#input1').val();
  const verificationCode = $('#input2').val();
  const password = $('#input3').val();
  const password2 = $('#input4').val();
  const referralCode = $('#input5').val();
  if (!mobile) {
    popupsBox('Please enter phone number');
    return
  };
  if (!verificationCode) {
    popupsBox('Please enter SMS verification');
    return
  };
  if (!password) {
    popupsBox('Please enter password');
    return
  };
  if (password !== password2) {
    popupsBox('Inconsistent passwords');
    return
  };

  request({url: 'sso/register', data: { appId, mobile, password, verificationCode, referralCode }}).then(res => {
    if (res) {
      $('#jakeBox1').hide();
      $('#jakeBox2').show();
      $('.register').html('appDownLoad');
      appDownLoadURL = res.appDownLoadURL;
    };
  })
}

function getUrlParam(name) {
  var reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
  var r = window.location.search.substr(1).match(reg);
  if (r != null) return unescape(r[2]); return null;
}

function countDown() {
  let countTime = 60;

  $('#smsBtn').hide();
  $('#countTime').show().html(`${countTime}S`);
  
  const countDownTime = setInterval(() => {
    countTime--;
    $('#countTime').html(`${countTime}S`);
    if (countTime <= 0) {
      clearInterval(countDownTime);
      $('#smsBtn').show();
      $('#countTime').hide();
    }
  }, 1000);
}

function onAppDownLoad() {
  window.open(appDownLoadURL, '_blank')
}

function popupsBox(word) {
  $('#popupsBox').show().html(word);
  setTimeout(() => {
    $('#popupsBox').hide().html('');
  }, 3000)
}

function request(params) {
  return new Promise((resolve, reject) => {
    $.ajax({
      url: `${preUrl}${params.url}`,
      type: params.type || 'post',
      header: {Accept: "Access-Control-Allow-Origin:*", 'X-Requested-With': 'XMLHttpRequest'},
      contentType: 'application/json',
      data: JSON.stringify(params.data),
      dataType: "json",
      xhrFields: {withCredentials: true},
      crossDomain: true,
      success: res => {
        if (res.state === '200') {
          resolve(res)
        } else {
          reject(false);
          popupsBox(res.msg)
        }
      },
      error: err => {
        reject(false)
      }
    })
  })
}