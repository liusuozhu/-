package com.wx.app.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.wx.app.AppsActivity;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AppsActivity.wxApi.handleIntent(getIntent(), this);
	}

	public void onReq(BaseReq req) {
		finish();
	}

	public void onResp(BaseResp resp) {
		switch(resp.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				if(AppsActivity.isWXLogin){
					SendAuth.Resp sendResp = (SendAuth.Resp) resp;
					AppsActivity.WX_CODE = sendResp.code;
					finish();
				}else{
					Toast.makeText(this, "成功!", Toast.LENGTH_LONG).show();
				}
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				Toast.makeText(this, "取消!", Toast.LENGTH_LONG).show();
				break;
			case BaseResp.ErrCode.ERR_AUTH_DENIED:
				Toast.makeText(this, "被拒绝", Toast.LENGTH_LONG).show();
				break;
			default:
				Toast.makeText(this, "失败!", Toast.LENGTH_LONG).show();
				break;
		}
		AppsActivity.isWXLogin=false;
		finish();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		AppsActivity.wxApi.handleIntent(intent, this);
		finish();
	}
}
