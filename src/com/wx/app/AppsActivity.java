package com.wx.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.wx.app.util.DownLoadImage;
import com.wx.app.util.DownLoadImage.BitmapCallBack;
import com.wx.app.util.HttpUtil;
import com.wx.app.util.WXUtil;

@SuppressLint("ShowToast")
public class AppsActivity extends Activity {

	// �Լ�΢��Ӧ�õ� appId
	public static String WX_APP_ID = "";
	// �Լ�΢��Ӧ�õ� appSecret
	public static String WX_SECRET = "";
	
	public static String WX_CODE = "";

	public static IWXAPI wxApi;
	public static boolean isWXLogin = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apps);
		wxApi = WXAPIFactory.createWXAPI(this, WX_APP_ID, true);
		wxApi.registerApp(WX_APP_ID);

		findViewById(R.id.wx_login).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				isWXLogin = true;
				SendAuth.Req req = new SendAuth.Req();
				req.scope = "snsapi_userinfo";
				req.state = "wechat_sdk_demo";
				wxApi.sendReq(req);
			}
		});

		findViewById(R.id.wx_share).setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				final SendMessageToWX.Req req = new SendMessageToWX.Req();
				WXWebpageObject webpage = new WXWebpageObject();
				// Ҫ��ת�ĵ�ַ
				webpage.webpageUrl = "http://www.baidu.com";
				final WXMediaMessage msg = new WXMediaMessage(webpage);
				msg.title = "����";
				msg.description = "Ҫ����΢�ŵ�����,Ҫ����ʹ�� \n �Ѿ�����";
				// ����ͼƬ��ַ png ��ʽ
				// eg�� http://h.hiphotos.baidu.com/image/w%3D310/sign=58272176271f95caa6f594b7f9177fc5/aa18972bd40735fa29e06ab19c510fb30f2408a1.png
				String imageUrl = "";
				// 0:���͵����� 1:���͵�����Ȧ 2:�ղ�
				final int shareWhat = 0;
				if (imageUrl.length() == 0) {
					// �����ͼƬ���ܳ���32k ���򵯲���΢�ŷ����
					Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
					msg.thumbData = WXUtil.bmpToByteArray(bmp, true);
					req.transaction = buildTransaction("webpage");
					req.message = msg;
					req.scene = shareWhat;
					wxApi.sendReq(req);
				} else {
					// ���̲߳��ܷ������磬�����߳�����ͼƬ
					new DownLoadImage(imageUrl).loadBitmap(new BitmapCallBack() {
						public void getBitmap(Bitmap bitmap) {
							// �����ͼƬ���ܳ���32k ѹ��ͼƬ
							Bitmap thumbBmp = Bitmap.createScaledBitmap(bitmap, 100, 100, true);
							bitmap.recycle();
							msg.thumbData = WXUtil.bmpToByteArray(thumbBmp, true);
							req.transaction = buildTransaction("webpage");
							req.message = msg;
							req.scene = shareWhat;
							wxApi.sendReq(req);
						}
					});
				}
			}
		});
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (isWXLogin) {
			loadWXUserInfo();
		}
	}

	/**
	 * @methods: ���΢���û���Ϣ
	 * @author: lianzhi
	 * @Date: 2015-3-5
	 */
	private void loadWXUserInfo() {
		new Thread(new Runnable() {
			public void run() {
				String accessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token?appid=" + WX_APP_ID + "&secret=" + WX_SECRET + "&code=" + WX_CODE + "&grant_type=authorization_code";
				String tokenResult = HttpUtil.httpsGet(accessTokenUrl);
				if (null != tokenResult) {
					JSONObject tokenObj = JSON.parseObject(tokenResult);
					String accessToken = tokenObj.getString("access_token");
					String openId = tokenObj.getString("openid");
					String userUrl = "https://api.weixin.qq.com/sns/userinfo?access_token=" + accessToken + "&openid=" + openId;
					String wxUserInfo = HttpUtil.httpsGet(userUrl);
					// JSONObject userObj = JSON.parseObject(wxUserInfo);
					Toast.makeText(AppsActivity.this, wxUserInfo, 5000).show();
				}
			}
		}).start();
		isWXLogin = false;
	}

}
