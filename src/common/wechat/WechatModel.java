package common.wechat;

import common.java.string.StringHelper;
import common.java.time.TimeHelper;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class WechatModel {
	public static class normal{
		@SuppressWarnings("unchecked")
		public final static JSONObject text(String text){
			JSONObject rs = new JSONObject();
			rs.put("Content", text);
			return rs;
		}
		@SuppressWarnings("unchecked")
		private final static JSONObject common(String id){
			JSONObject rs = new JSONObject();
			rs.put("MediaId", id);
			return rs;
		}
		public final static JSONObject image(String mediaid){
			return common(mediaid);
		}
		public final static JSONObject voice(String mediaid){
			return common(mediaid);
		}
		public final static JSONObject video(String mediaid,String title){
			return video(mediaid,title,"");
		}
		@SuppressWarnings("unchecked")
		public final static JSONObject video(String mediaid,String title,String desp){
			JSONObject rs = new JSONObject();
			rs.put("MediaId", mediaid);
			rs.put("Title", title);
			rs.put("Description", desp);
			return rs;
		}
		public final static JSONObject music(String url,String title){
			return music(url,title,"","","");
		}
		public final static JSONObject music(String url,String title,String thumbMediaId){
			return music(url,title,thumbMediaId,"","");
		}
		public final static JSONObject music(String url,String title,String thumbMediaId,String hurl){
			return music(url,title,thumbMediaId,"",hurl);
		}
		@SuppressWarnings("unchecked")
		public final static JSONObject music(String url,String title,String thumbMediaId,String desp,String hurl){
			JSONObject rs = new JSONObject();
			rs.put("MusicUrl", url);
			rs.put("HQMusicUrl", hurl);
			rs.put("ThumbMediaId", thumbMediaId);
			rs.put("Title", title);
			rs.put("Description", desp);
			return rs;
		}
		class Article{
			JSONArray _item;
			public Article(){
				_item = new JSONArray();
			}
			@SuppressWarnings("unchecked")
			public Article AddNews(String title,String picurl,String url,String desp){//添加子项目
				if(_item.size() < 8 ){
					JSONObject rs = new JSONObject();
					rs.put("Title", title);
					rs.put("PicUrl", picurl);
					rs.put("Url", url);
					rs.put("Description", desp);
					_item.add(rs);
				}
				return this;
			}
			public JSONArray toNewsArray(){
				return _item;
			}
			@SuppressWarnings("unchecked")
			public JSONObject article(){
				JSONObject rs = new JSONObject();
				rs.put("item", _item);
				return rs;
			}
		}
	}
	public static class kf{
		@SuppressWarnings("unchecked")
		public static JSONObject text(String text){
			JSONObject rs = new JSONObject();
			rs.put("content", text);
			return rs;
		}
		@SuppressWarnings("unchecked")
		private final static JSONObject common(String mediaid){
			JSONObject rs = new JSONObject();
			rs.put("media_id", mediaid);
			return rs;
		}
		
		public static JSONObject image(String mediaid){
			return common(mediaid);
		}
		public static JSONObject voice(String mediaid){
			return common(mediaid);
		}
		public static JSONObject video(String mediaid,String title){
			return video(mediaid,title,"","");
		}
		public static JSONObject video(String mediaid,String title,String thumbMediaId){
			return video(mediaid,title,thumbMediaId,"");
		}
		@SuppressWarnings("unchecked")
		public static JSONObject video(String mediaid,String title,String thumbMediaId,String desp){
			JSONObject rs = new JSONObject();
			rs.put("media_id", mediaid);
			rs.put("title", title);
			rs.put("thumb_media_id", thumbMediaId);
			rs.put("description", desp);
			return rs;
		}
		public final static JSONObject music(String url,String title){
			return music(url,title,"","","");
		}
		public final static JSONObject music(String url,String title,String thumbMediaId){
			return music(url,title,"",thumbMediaId,"");
		}
		public final static JSONObject music(String url,String title,String hurl,String thumbMediaId){
			return music(url,title,hurl,thumbMediaId,"");
		}
		@SuppressWarnings("unchecked")
		public final static JSONObject music(String url,String title,String hurl,String thumbMediaId,String desp){
			JSONObject rs = new JSONObject();
			rs.put("musicurl", url);
			rs.put("hqmusicurl", hurl);
			rs.put("title", title);
			rs.put("thumb_media_id", thumbMediaId);
			rs.put("description", desp);
			return rs;
		}
		class news{
			JSONArray _item;
			public news(){
				_item = new JSONArray();
			}
			@SuppressWarnings("unchecked")
			public news AddNews(String title,String picurl,String url,String desp){//添加子项目
				if(_item.size() < 8 ){
					JSONObject rs = new JSONObject();
					rs.put("title", title);
					rs.put("picurl", picurl);
					rs.put("url", url);
					rs.put("description", desp);
					_item.add(rs);
				}
				return this;
			}
			public JSONArray toNewsArray(){
				return _item;
			}
			@SuppressWarnings("unchecked")
			public JSONObject article(){
				JSONObject rs = new JSONObject(); 
				rs.put("articles", _item);
				return rs;
			}
		}
		public static JSONObject mpNews(String mediaid){
			return common(mediaid);
		}
		/**
		 * 		 * 	code	否	指定的卡券code码，只能被领一次。use_custom_code字段为true的卡券必须填写，非自定义code不必填写。
					openid	否	指定领取者的openid，只有该用户能领取。bind_openid字段为true的卡券必须填写，bind_openid字段为false不必填写。
					timestamp	是	时间戳，商户生成从1970年1月1日00:00:00至今的秒数,即当前的时间,且最终需要转换为字符串形式;由商户生成后传入,不同添加请求的时间戳须动态生成，若重复将会导致领取失败！。
					nonce_str	否	随机字符串，由开发者设置传入，加强签名的安全性。随机字符串，不长于32位。推荐使用大小写字母和数字，不同添加请求的nonce须动态生成，若重复将会导致领取失败！。
					signature	是	签名，商户将接口列表中的参数按照指定方式进行签名,签名方式使用SHA1,具体签名方案参见下文;由商户按照规范签名后传入。
					outer_id	否	领取场景值，用于领取渠道的数据统计，默认值为0，字段类型为整型，长度限制为60位数字。用户领取卡券后触发的事件推送中会带上此自定义场景值，不参与签名。
					
					需要加密！
		 * @param code
		 * @param openid
		 *
		 * @param nonce_str
		 * @param signature
		 * @param outer_id
		 * @return
		 */
		@SuppressWarnings("unchecked")
		public final static JSONObject cardExt(String code,String openid,String nonce_str,String signature, String outer_id){
			JSONObject rs = new JSONObject(); 
			String string;
			rs.put("code", code);
			rs.put("openid", openid);
            rs.put("timestamp", String.valueOf(TimeHelper.build().nowSecond()));
			rs.put("nonce_str", nonce_str);
			rs.put("signature", signature);
			string = ( outer_id.length() > 60 ) ? StringHelper.build(outer_id).left(60).toString() : outer_id;
			rs.put("outer_id", string);
			return rs;
		}
		public final static JSONObject cardExt(String code,String openid,String nonce_str,String signature){
			return cardExt(code,openid,nonce_str,signature,"0");
		}
		@SuppressWarnings("unchecked")
		public final static JSONObject card(String cardId,JSONObject cardExt){
			JSONObject rs = new JSONObject();
			rs.put("wxcard", cardId);
			rs.put("card_ext", cardExt.toJSONString());
			return rs;
			
		}
	}
	
	public static class uploadArticle{
		JSONArray _item;
		public uploadArticle(){
			_item = new JSONArray();
		}
		@SuppressWarnings("unchecked")
		public uploadArticle newArticle(String thumbMediaId,String author,String title,String content,String content_source_url,String desp,boolean show_cover_pic){//添加子项目
			if(_item.size() < 8 ){
				JSONObject rs = new JSONObject();
				rs.put("title", title);
				rs.put("thumb_media_id", thumbMediaId);
				rs.put("content", content);
				rs.put("content_source_url", content_source_url);
				rs.put("digest", desp);
				rs.put("author", author);
				rs.put("show_cover_pic", show_cover_pic ? 1 : 0 );
				_item.add(rs);
			}
			return this;
		}
		public JSONArray toArticleArray(){
			return _item;
		}
		@SuppressWarnings("unchecked")
		public JSONObject article(){
			JSONObject rs = new JSONObject(); 
			rs.put("articles", _item);
			return rs;
		}
	}
	public static class toall extends kf{
		@SuppressWarnings("unchecked")
		public static JSONObject video(String toallmediaid,String title,String desp){
			JSONObject rs = new JSONObject();
			rs.put("mpvideo", toallmediaid);
			return rs;
		}
		@SuppressWarnings("unchecked")
		public static JSONObject card(String card_id){
			JSONObject rs = new JSONObject();
			rs.put("card_id", card_id);
			return rs;
		}
	}
	
	public final static String MSGTYPE_TEXT = "text";
	public final static String MSGTYPE_IMAGE = "image";
	public final static String MSGTYPE_LOCATION = "location";
	public final static String MSGTYPE_LINK = "link";
	public final static String MSGTYPE_EVENT = "event";
	public final static String MSGTYPE_MUSIC = "music";
	public final static String MSGTYPE_NEWS = "news";
	public final static String MSGTYPE_MPNEWS = "mpnews";
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
}
