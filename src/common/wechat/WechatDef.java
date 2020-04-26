package common.wechat;

import java.io.File;
import java.util.HashMap;

public class WechatDef {
	public final static String MSGTYPE_TEXT = "text";
	public final static String MSGTYPE_IMAGE = "image";
	public final static String MSGTYPE_LOCATION = "location";
	public final static String MSGTYPE_LINK = "link";
	public final static String MSGTYPE_EVENT = "event";
	public final static String MSGTYPE_MUSIC = "music";
	public final static String MSGTYPE_NEWS = "news";
	public final static String MSGTYPE_VOICE = "voice";
	public final static String MSGTYPE_VIDEO = "video";
	public final static String MSGTYPE_THUMB = "thumb";
	public final static String MSGTYPE_SHORTVIDEO = "shortvideo";
	public final static String EVENT_SUBSCRIBE = "subscribe";       //订阅
	public final static String EVENT_UNSUBSCRIBE = "unsubscribe";   //取消订阅
	public final static String EVENT_SCAN = "SCAN";                 //扫描带参数二维码
	public final static String EVENT_LOCATION = "LOCATION";         //上报地理位置
	public final static String EVENT_MENU_VIEW = "VIEW";                     //菜单 - 点击菜单跳转链接
	public final static String EVENT_MENU_CLICK = "CLICK";                   //菜单 - 点击菜单拉取消息
	public final static String EVENT_MENU_SCAN_PUSH = "scancode_push";       //菜单 - 扫码推事件(客户端跳URL)
	public final static String EVENT_MENU_SCAN_WAITMSG = "scancode_waitmsg"; //菜单 - 扫码推事件(客户端不跳URL)
	public final static String EVENT_MENU_PIC_SYS = "pic_sysphoto";          //菜单 - 弹出系统拍照发图
	public final static String EVENT_MENU_PIC_PHOTO = "pic_photo_or_album";  //菜单 - 弹出拍照或者相册发图
	public final static String EVENT_MENU_PIC_WEIXIN = "pic_weixin";         //菜单 - 弹出微信相册发图器
	public final static String EVENT_MENU_LOCATION = "location_select";      //菜单 - 弹出地理位置选择器
	public final static String EVENT_SEND_MASS = "MASSSENDJOBFINISH";        //发送结果 - 高级群发完成
	public final static String EVENT_SEND_TEMPLATE = "TEMPLATESENDJOBFINISH";//发送结果 - 模板消息发送结果
	public final static String EVENT_KF_SEESION_CREATE = "kfcreatesession";  //多客服 - 接入会话
	public final static String EVENT_KF_SEESION_CLOSE = "kfclosesession";    //多客服 - 关闭会话
	public final static String EVENT_KF_SEESION_SWITCH = "kfswitchsession";  //多客服 - 转接会话
	public final static String EVENT_CARD_PASS = "card_pass_check";          //卡券 - 审核通过
	public final static String EVENT_CARD_NOTPASS = "card_not_pass_check";   //卡券 - 审核未通过
	public final static String EVENT_CARD_USER_GET = "user_get_card";        //卡券 - 用户领取卡券
	public final static String EVENT_CARD_USER_DEL = "user_del_card";        //卡券 - 用户删除卡券
	public final static String EVENT_MERCHANT_ORDER = "merchant_order";        //微信小店 - 订单付款通知
	public final static String API_URL_PREFIX = "https://api.weixin.qq.com/cgi-bin";
	public final static String AUTH_URL = "/token?grant_type=client_credential&";
	public final static String MENU_CREATE_URL = "/menu/create?";
	public final static String MENU_GET_URL = "/menu/get?";
	public final static String MENU_DELETE_URL = "/menu/delete?";
	public final static String MENU_INFO_URL = "/get_current_selfmenu_info?";
	public final static String MENU_ADDCONDITIONAL_URL = "/menu/addconditional?";
	public final static String MENU_DELCONDITIONAL_URL = "/menu/delconditional?";
	public final static String MENU_TRYMATCH_URL = "/menu/trymatch?";
	public final static String GET_TICKET_URL = "/ticket/getticket?";
	public final static String CALLBACKSERVER_GET_URL = "/getcallbackip?";
	public final static String QRCODE_CREATE_URL="/qrcode/create?";
	public final static int QR_SCENE =0;
	public final static int QR_LIMIT_SCENE = 1;
	public final static String QRCODE_IMG_URL="https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=";
	public final static String SHORT_URL="/shorturl?";
	public final static String USER_GET_URL="/user/get?";
	public final static String USER_INFO_URL="/user/info?";
	public final static String USERS_INFO_URL="/user/info/batchget?";
	public final static String USER_UPDATEREMARK_URL="/user/info/updateremark?";
	public final static String GROUP_GET_URL="/groups/get?";
	public final static String USER_GROUP_URL="/groups/getid?";
	public final static String GROUP_CREATE_URL="/groups/create?";
	public final static String GROUP_UPDATE_URL="/groups/update?";
	public final static String GROUP_DELETE_URL="/groups/delete?";
	public final static String GROUP_MEMBER_UPDATE_URL="/groups/members/update?";
	public final static String GROUP_MEMBER_BATCHUPDATE_URL="/groups/members/batchupdate?";
	public final static String CUSTOM_SEND_URL="/message/custom/send?";
	public final static String MEDIA_UPLOADNEWS_URL = "/media/uploadnews?";
	public final static String MASS_SEND_URL = "/message/mass/send?";
	public final static String TEMPLATE_SET_INDUSTRY_URL = "/template/api_set_industry?";
	public final static String TEMPLATE_ADD_TPL_URL = "/template/api_add_template?";
	public final static String TEMPLATE_SEND_URL = "/message/template/send?";
	public final static String MASS_SEND_GROUP_URL = "/message/mass/sendall?";
	public final static String MASS_DELETE_URL = "/message/mass/delete?";
	public final static String MASS_PREVIEW_URL = "/message/mass/preview?";
	public final static String MASS_QUERY_URL = "/message/mass/get?";
	public final static String UPLOAD_MEDIA_URL = "http://file.api.weixin.qq.com/cgi-bin";
	public final static String MEDIA_UPLOAD_URL = "/media/upload?";
	public final static String MEDIA_UPLOADIMG_URL = "/media/uploadimg?";//图片上传接口
	public final static String MEDIA_GET_URL = "/media/get?";
	public final static String MEDIA_VIDEO_UPLOAD = "/media/uploadvideo?";
    public final static String MEDIA_FOREVER_UPLOAD_URL = "/material/add_material?";
    public final static String MEDIA_FOREVER_NEWS_UPLOAD_URL = "/material/add_news?";
    public final static String MEDIA_FOREVER_NEWS_UPDATE_URL = "/material/update_news?";
    public final static String MEDIA_FOREVER_GET_URL = "/material/get_material?";
    public final static String MEDIA_FOREVER_DEL_URL = "/material/del_material?";
    public final static String MEDIA_FOREVER_COUNT_URL = "/material/get_materialcount?";
    public final static String MEDIA_FOREVER_BATCHGET_URL = "/material/batchget_material?";
	public final static String OAUTH_PREFIX = "https://open.weixin.qq.com/connect/oauth2";
	public final static String OAUTH_AUTHORIZE_URL = "/authorize?";
	//多客服相关地址
	public final static String CUSTOM_SERVICE_GET_RECORD = "/customservice/getrecord?";
	public final static String CUSTOM_SERVICE_GET_KFLIST = "/customservice/getkflist?";
	public final static String CUSTOM_SERVICE_GET_ONLINEKFLIST = "/customservice/getonlinekflist?";
	public final static String API_BASE_URL_PREFIX = "https://api.weixin.qq.com"; //以下API接口URL需要使用此前缀
	public final static String OAUTH_TOKEN_URL = "/sns/oauth2/access_token?";
	public final static String OAUTH_REFRESH_URL = "/sns/oauth2/refresh_token?";
	public final static String OAUTH_USERINFO_URL = "/sns/userinfo?";
	public final static String OAUTH_AUTH_URL = "/sns/auth?";
	///多客服相关地址
	public final static String CUSTOM_SESSION_CREATE = "/customservice/kfsession/create?";
	public final static String CUSTOM_SESSION_CLOSE = "/customservice/kfsession/close?";
	public final static String CUSTOM_SESSION_SWITCH = "/customservice/kfsession/switch?";
	public final static String CUSTOM_SESSION_GET = "/customservice/kfsession/getsession?";
	public final static String CUSTOM_SESSION_GET_LIST = "/customservice/kfsession/getsessionlist?";
	public final static String CUSTOM_SESSION_GET_WAIT = "/customservice/kfsession/getwaitcase?";
	public final static String CS_KF_ACCOUNT_ADD_URL = "/customservice/kfaccount/add?";
	public final static String CS_KF_ACCOUNT_UPDATE_URL = "/customservice/kfaccount/update?";
	public final static String CS_KF_ACCOUNT_DEL_URL = "/customservice/kfaccount/del?";
	public final static String CS_KF_ACCOUNT_UPLOAD_HEADIMG_URL = "/customservice/kfaccount/uploadheadimg?";
	///卡券相关地址
	public final static String CARD_CREATE                     = "/card/create?";
	public final static String CARD_DELETE                     = "/card/delete?";
	public final static String CARD_UPDATE                     = "/card/update?";
	public final static String CARD_GET                        = "/card/get?";
    public final static String CARD_USER_GETCARDLIST		   = "/card/user/getcardlist?";
    public final static String CARD_BATCHGET                   = "/card/batchget?";
	public final static String CARD_MODIFY_STOCK               = "/card/modifystock?";
	public final static String CARD_LOCATION_BATCHADD          = "/card/location/batchadd?";
	public final static String CARD_LOCATION_BATCHGET          = "/card/location/batchget?";
	public final static String CARD_GETCOLORS                  = "/card/getcolors?";
	public final static String CARD_QRCODE_CREATE              = "/card/qrcode/create?";
	public final static String CARD_CODE_CONSUME               = "/card/code/consume?";
	public final static String CARD_CODE_DECRYPT               = "/card/code/decrypt?";
	public final static String CARD_CODE_GET                   = "/card/code/get?";
	public final static String CARD_CODE_UPDATE                = "/card/code/update?";
	public final static String CARD_CODE_UNAVAILABLE           = "/card/code/unavailable?";
	public final static String CARD_TESTWHILELIST_SET          = "/card/testwhitelist/set?";
	public final static String CARD_MEETINGCARD_UPDATEUSER      = "/card/meetingticket/updateuser?";    //更新会议门票
	public final static String CARD_MEMBERCARD_ACTIVATE        = "/card/membercard/activate?";      //激活会员卡
	public final static String CARD_MEMBERCARD_UPDATEUSER      = "/card/membercard/updateuser?";    //更新会员卡
	public final static String CARD_MOVIETICKET_UPDATEUSER     = "/card/movieticket/updateuser?";   //更新电影票(未加方法)
	public final static String CARD_BOARDINGPASS_CHECKIN       = "/card/boardingpass/checkin?";     //飞机票-在线选座(未加方法)
	public final static String CARD_LUCKYMONEY_UPDATE          = "/card/luckymoney/updateuserbalance?";     //更新红包金额
	public final static String SEMANTIC_API_URL = "/semantic/semproxy/search?"; //语义理解
	///数据分析接口
	public static HashMap<String, HashMap<String, String>> DATACUBE_URL_ARR = new HashMap<String, HashMap<String, String>>(){
		private static final long serialVersionUID = 1L;
		{
				put("user",new HashMap<String, String>(){//用户分析
					private static final long serialVersionUID = 1L;
					{
						put("summary","/datacube/getusersummary?");//获取用户增减数据（getusersummary）
						put("cumulate","/datacube/getusercumulate?");//获取累计用户数据（getusercumulate）
					}
				});
				put("article",new HashMap<String, String>(){//图文分析
					private static final long serialVersionUID = 1L;
					{
						put("summary","/datacube/getarticlesummary?");//获取图文群发每日数据（getarticlesummary）
						put("total","/datacube/getarticletotal?");//获取图文群发总数据（getarticletotal）
						put("read","/datacube/getuserread?");//获取图文统计数据（getuserread）
						put("readhour","/datacube/getuserreadhour?");//获取图文统计分时数据（getuserreadhour）
						put("share","/datacube/getusershare?");//获取图文分享转发数据（getusershare）
						put("sharehour","/datacube/getusersharehour?");//获取图文分享转发分时数据（getusersharehour）
					}
				});
				put("upstreammsg",new HashMap<String, String>(){//消息分析
					private static final long serialVersionUID = 1L;
					{
						put("summary","/datacube/getupstreammsg?");//获取消息发送概况数据（getupstreammsg）
						put("hour","/datacube/getupstreammsghour?");//获取消息分送分时数据（getupstreammsghour）
						put("week","/datacube/getupstreammsgweek?");//获取消息发送周数据（getupstreammsgweek）
						put("month","/datacube/getupstreammsgmonth?");//获取消息发送月数据（getupstreammsgmonth）
						put("dist","/datacube/getupstreammsgdist?");//获取消息发送分布数据（getupstreammsgdist）
						put("distweek","/datacube/getupstreammsgdistweek?");//获取消息发送分布周数据（getupstreammsgdistweek）
						put("distmonth","/datacube/getupstreammsgdistmonth?");//获取消息发送分布月数据（getupstreammsgdistmonth）
					}
				});
				put("user",new HashMap<String, String>(){//接口分析
					private static final long serialVersionUID = 1L;
					{
						put("summary","/datacube/getinterfacesummary?");//获取接口分析数据（getinterfacesummary）
						put("summaryhour","/datacube/getinterfacesummaryhour?");//获取接口分析分时数据（getinterfacesummaryhour）
					}
				});
		}
	};
	///微信摇一摇周边
	public final static String SHAKEAROUND_DEVICE_APPLYID = "/shakearound/device/applyid?";//申请设备ID
    public final static String SHAKEAROUND_DEVICE_UPDATE = "/shakearound/device/update?";//编辑设备信息
	public final static String SHAKEAROUND_DEVICE_SEARCH = "/shakearound/device/search?";//查询设备列表
	public final static String SHAKEAROUND_DEVICE_BINDLOCATION = "/shakearound/device/bindlocation?";//配置设备与门店ID的关系
	public final static String SHAKEAROUND_DEVICE_BINDPAGE = "/shakearound/device/bindpage?";//配置设备与页面的绑定关系
    public final static String SHAKEAROUND_MATERIAL_ADD = "/shakearound/material/add?";//上传摇一摇图片素材
	public final static String SHAKEAROUND_PAGE_ADD = "/shakearound/page/add?";//增加页面
	public final static String SHAKEAROUND_PAGE_UPDATE = "/shakearound/page/update?";//编辑页面
	public final static String SHAKEAROUND_PAGE_SEARCH = "/shakearound/page/search?";//查询页面列表
	public final static String SHAKEAROUND_PAGE_DELETE = "/shakearound/page/delete?";//删除页面
	public final static String SHAKEAROUND_USER_GETSHAKEINFO = "/shakearound/user/getshakeinfo?";//获取摇周边的设备及用户信息
	public final static String SHAKEAROUND_STATISTICS_DEVICE = "/shakearound/statistics/device?";//以设备为维度的数据统计接口
    public final static String SHAKEAROUND_STATISTICS_PAGE = "/shakearound/statistics/page?";//以页面为维度的数据统计接口
	///微信小店相关接口
	public final static String MERCHANT_ORDER_GETBYID = "/merchant/order/getbyid?";//根据订单ID获取订单详情
	public final static String MERCHANT_ORDER_GETBYFILTER = "/merchant/order/getbyfilter?";//根据订单状态/创建时间获取订单详情
	public final static String MERCHANT_ORDER_SETDELIVERY = "/merchant/order/setdelivery?";//设置订单发货信息
	public final static String MERCHANT_ORDER_CLOSE = "/merchant/order/close?";//关闭订单

	public final static String getWebchatFileType(File file) {
		String rString = "";
		String fileName = file.getName();
		String typeName = fileName.substring(fileName.indexOf(".") + 1);
		switch (typeName) {
			case "bmp":
				rString = WechatDef.MSGTYPE_IMAGE;
				break;
			case "png":
				rString = WechatDef.MSGTYPE_IMAGE;
				break;
			case "jpeg":
				rString = WechatDef.MSGTYPE_IMAGE;
				break;
			case "jpg":
				rString = WechatDef.MSGTYPE_IMAGE;
				break;
			case "gif":
				rString = WechatDef.MSGTYPE_IMAGE;
				break;
			case "amr":
				rString = WechatDef.MSGTYPE_VOICE;
				break;
			case "mp3":
				rString = WechatDef.MSGTYPE_VOICE;
				break;
			case "JPG":
				rString = WechatDef.MSGTYPE_THUMB;
				break;
		}
		return rString;
	}
}