package common.wechat;

import common.java.encrypt.Md5;
import common.java.encrypt.Sha1;
import common.java.encrypt.UrlCode;
import common.java.file.FileHelper;
import common.java.nlogger.nlogger;
import common.java.stream.InputStreamHelper;
import common.java.string.StringHelper;
import common.java.time.TimeHelper;
import common.java.xml.XmlHelper;
import kong.unirest.HttpRequestWithBody;
import kong.unirest.Unirest;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;

/*
 * 1：完成与微信服务器确认->实现会话
 * 		完成了不加密时候处理
 * 2：完善接口->实现功能交互
 * 		完成了菜单相关功能
 * 		完成了网页授权相关功能
 * 3：利用消息机制响应来自微信的请求->实现双向交互
 * 4：完成token授权->实现权限交互
 * 		已完成
 * */
public class WechatHelper {//独立的 微信封装类
	private String encodingAesKey;
	private String encrypt_type;
	private String appid;
	private String appsecret;
	private String jsapi_ticket;

	public boolean debug =  false;

    private boolean menu_status;		//菜单是否开启

    /**
     * @apiNote 获得Accesstoken后调用回调,当前入参是null是返回授权token,当入参非null时,视入参为token,存入缓存
     */
	private Function<tokenCallback,String> AccessTokenCallBack;
    /**
     * @apiNote 获得jsTicket后调用回调,当前入参是null是返回jsTicket后调用回调,当入参非null时,视入参为jsTicket后调用回调,存入缓存
     */
    private Function<tokenCallback,String> TicketCallBack;
    /**
     * @apiNote UserToken,当前入参是null是返回UserToken后调用回调,当入参非null时,视入参为UserToken后调用回调,存入缓存
     */
    private Function<tokenCallback,String> UserAccessTokenCallBack;

    public WechatHelper setAccessTokenCallBack(Function<tokenCallback,String> cb){
        AccessTokenCallBack = cb;
	    return this;
    }
    public WechatHelper setTicketCallBack(Function<tokenCallback,String> cb){
        TicketCallBack = cb;
        return this;
    }
    public WechatHelper setUserAccessTokenCallBack(Function<tokenCallback,String> cb){
        UserAccessTokenCallBack = cb;
        return this;
    }

    public WechatHelper(String _appid, String _appsecret) {
		_wechatHelper(null,null,_appid,_appsecret,false,false);
	}

    public WechatHelper(String _token, String _encode, String _appid, String _appsecret) {
		_wechatHelper(_token,_encode,_appid,_appsecret,false,false);
	}

    public WechatHelper(String _token, String _encode, String _appid, String _appsecret, boolean _debug, boolean _logcallback) {
		_wechatHelper(_token,_encode,_appid,_appsecret,_debug,_logcallback);
	}

    /**
     * 验证微信服务器发送信号
     *
     * @param reqParameter
     * @return
     */
    public static String checkSignature(JSONObject reqParameter) {
        String signature = "";
        String timestamp;
        String nonce, _token, tmpStr, echostr;
        tmpStr = "";
        echostr = "putao520:error";
        if (reqParameter != null) {
            signature = (String)reqParameter.get("signature","");
            timestamp = (String)reqParameter.get("timestamp","");
            nonce = (String)reqParameter.get("nonce","");
            echostr = (String)reqParameter.get("echostr","");
            _token = (String)reqParameter.get("token","");

            List<String> tmpArr = new ArrayList<String>();
            tmpArr.add(_token);
            tmpArr.add(timestamp);
            tmpArr.add(nonce);

            Collections.sort(tmpArr);
            tmpStr = StringHelper.join(tmpArr, "");
            tmpStr = Sha1.build(tmpStr);
        }
        return (tmpStr.equals(signature)) ? echostr : "putao520:error";
    }
	public void _wechatHelper(String _token,String _encode,String _appid,String _appsecret,boolean _debug,boolean _logcallback){
		//token = _token;
		encodingAesKey = _encode;
		appid = _appid;
		appsecret = _appsecret;
		debug = _debug;
		/*
		if( token == null ){	//这是一个不包含token的实例化请求
			update_token();		//获得token值
		}
		*/
    }

    /**
     * 默认text消息
     *
     * @param toUser
     * @param fromUser
     * @param content
     * @return
     */
    public final static String replyMSG(String toUser, String fromUser, JSONObject content) {
        return replyMSG(toUser, fromUser, content, WechatDef.MSGTYPE_TEXT);
    }

    /**
     * 生成回复消息-生肉
     *
     * @param toUser   会话对象
     * @param fromUser 我
     * @param content  回复内容 JSONObject结构
     * @param msgtype  消息类型
     * @return
     */
    public final static String replyMSG(String toUser, String fromUser, JSONObject content, String msgtype) {
        Element node;
        Element root = XmlHelper.newElement("xml");
        root.add(XmlHelper.newElementCDATA("ToUserName", toUser));
        root.add(XmlHelper.newElementCDATA("FromUserName", fromUser));
        root.add(XmlHelper.newElementCDATA("MsgType", msgtype));
        root.add(XmlHelper.newElementLong("CreateTime", TimeHelper.build().nowSecond()));
        switch (msgtype) {
            case WechatDef.MSGTYPE_NEWS:
                root.add(XmlHelper.newElementLong("ArticleCount", (long) ((JSONArray) content.get("item")).size()));
                node = DocumentHelper.createElement("Articles");
                break;
            case WechatDef.MSGTYPE_TEXT:
                node = root;
                break;
            default:
                node = DocumentHelper.createElement(StringHelper.build(msgtype).upperCaseFirst().toString());
                break;
        }
        node = XmlHelper.appendjson2xml(node, content);//如果是多项LIST也必须是有KEY的array
        if (!root.equals(node)) {
            root.add(node);
        }
        //到此时，root生成完毕回复的XML了
        return root.asXML();
    }

    public static final String getRedirectURL(int _appid, String url) {
        String appid = String.valueOf(_appid);
        String newURL = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=" + appid + "&redirect_uri=" + UrlCode.encode("http://" + url) + "&response_type=code&scope=snsapi_base&connect_redirect=1";
        return newURL;
    }
	/**基于post的文件请求，不带多余参数
	 * @param url
	 * @return
	 */
	private byte[] _api_token_post_get_file(String url){
		return _api_token_post_get_file(url,null);
	}
	
	/**基于post的文件请求，可带参数
	 * @param url
	 * @return
	 */
	private byte[] _api_token_post_get_file(String url,JSONObject parameter){
		return posthttpfile(url + "access_token=" + update_token(),parameter);
	}
	
	private String api_token(String url){
		return fastHttp(url + "access_token=" + update_token(),null,true);
	}
	
	/**带token请求
	 * @param url
	 * @return
	 */
	private JSONObject _api_token(String url){
		return replyJSON(url + "access_token=" + update_token());
	}
	/**带token和额外参数请求
	 * @param url
	 * @param paramerurl
	 * @return
	 */
	private JSONObject _api_token(String url,String paramerurl){
		return replyJSON(url + "access_token=" + update_token() + paramerurl);
	}
	/**带token在下载文件文件
	 * @param url
	 * @return
	 */
	private byte[] _api_token_file(String url){
		return httpfile(url);
	}
	/**带token请求
	 * @param url
	 * @return
	 */
	/*
	private JSONObject _userapi_token(String url){
		return replyJSON(url + "access_token=" + access_token);
	}
	*/
	/**带token post请求
	 * @param url
	 * @param jsonObj
	 * @return
	 */
	private JSONObject _api_token_post(String url,JSONObject jsonObj){
		return replyJSON(url + "access_token=" + update_token(), jsonObj, false);
    }

    private String fastHttp(String url, String parameter, boolean isget) {
	    String rs;
	    try{
            if( isget ){
                rs = Unirest.get(url).asString().getBody();
            }
            else {
                rs = Unirest.post(url).body(parameter).asString().getBody();
            }
        }
	    catch (Exception e){
	        nlogger.logInfo(e,url);
	        rs = "";
        }
        return rs;
	}
	/**带token和额外参数的post请求
	 * @param url
	 * @return
	 */
	/*
	private JSONObject _userapi_token_post(String url,JSONObject jsonObj){
		return replyJSON(url + "access_token=" + access_token,jsonObj,false);
	}
	*/
	@SuppressWarnings("unchecked")
	private JSONObject replyJSON(String url){
		return replyJSON(url,(String)null,true);
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject replyJSON(String url,String parameter,boolean isget) {
        return resultfilter(fastHttp(url, parameter, isget));
    }

    private byte[] httpfile(String url){
        byte[] rb;
	    try{
	        rb = InputStreamHelper.input2byte(Unirest.post(url).asObject(i->i.getContent()).getBody());
        }
	    catch (Exception e){
	        nlogger.logInfo(e,url);
            rb = null;
        }
		return rb;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject replyJSON(String url,JSONObject parameter,boolean isget){
		return replyJSON(url,parameter.toJSONString(),isget);
	}
	
	/**更新返回数据到本类
	 * @param cacheJSON
	 */
	private String updateTokenData(JSONObject cacheJSON){
		String token = null;
		if( cacheJSON != null){
			token = cacheJSON.get("access_token").toString();
			menu_status = Boolean.parseBoolean(cacheJSON.get("menustatus").toString()) ;
		}
		return token;
	}
	
	@SuppressWarnings("unchecked")
    private JSONObject resultfilter(String str) {
        JSONObject rlt = JSONObject.toJSON(str);
        if (rlt != null) {
            if (rlt.containsKey("errcode") && !rlt.get("errcode").toString().equals("0")) {
                System.err.println(rlt);//如果错误直接输出错误值
                rlt.put("status", "false");
            } else {
                rlt.put("status", "true");
            }
        }
        return rlt;
    }

    private byte[] posthttpfile(String url, String parameter){
	    byte[] rb;
	    try{
	        rb = InputStreamHelper.input2byte(Unirest.post(url).body(parameter).asObject(i->i.getContent()).getBody());
        }
	    catch (Exception e){
	        nlogger.logInfo(e ,url + "|" + parameter);
	        rb = null;
        }
		return rb;
	}
    /** JSAPI验证标记
     * @param url JS授权验证域名
     * @return
    */
    @SuppressWarnings("unchecked")
	public String signature(String url) {
        JSONObject ret = new JSONObject();
        String nonce_str = create_nonce_str();
        String timestamp = create_timestamp();
        String string1;
        String signature = "";

        // 获得JS票据
        getJSApiTicket();

        // 注意这里参数名必须全部小写，且必须有序
        if( jsapi_ticket != null ){
	        string1 = "jsapi_ticket=" + jsapi_ticket +
	                  "&noncestr=" + nonce_str +
	                  "&timestamp=" + timestamp +
	                  "&url=" + url;
	        //System.out.println(string1);
	        try
	        {
	            MessageDigest crypt = MessageDigest.getInstance("SHA-1");
	            crypt.reset();
	            crypt.update(string1.getBytes(StandardCharsets.UTF_8));
	            signature = byteToHex(crypt.digest());
	        }
	        catch (NoSuchAlgorithmException e)
	        {
	            nlogger.logInfo(e);
	        }
            ret.put("url", url);
	        ret.put("jsapi_ticket", jsapi_ticket);
	        ret.put("nonceStr", nonce_str);
	        ret.put("timestamp", timestamp);
	        ret.put("signature", signature);
        }
        return ret.toJSONString();
    }

    private static String byteToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash)
        {
            formatter.format("%02x", b);
        }
        String result = formatter.toString();
        formatter.close();
        return result;
    }

    private static String create_nonce_str() {
        return UUID.randomUUID().toString();
    }

    private static String create_timestamp() {
        return Long.toString(System.currentTimeMillis() / 1000);
    }

    private byte[] posthttpfile(String url, JSONObject parameter) {
        byte[] rb;
        try{
            // rb = InputStreamHelper.input2byte( Unirest.post(url).body(parameter.toJSONString()).asBinary().getBody() );
            rb =  Unirest.post(url).body(parameter.toJSONString()).asObject( i->i.getContentAsBytes() ).getBody();
        }
        catch (Exception e){
            nlogger.logInfo(e, url + "|" + parameter.toJSONString());
            rb = null;
        }
        return rb;
    }

    /**
     * 带token post表单(含文件)上传
     *
     * @param url
     * @param jsonObj
     * @return
     */
    private JSONObject _api_token_post_file(String url, JSONObject jsonObj) {
        String rs;
        String _url = url + "access_token=" + update_token();
        try{
            HttpRequestWithBody hrb = Unirest.post(_url);
            for( String key : jsonObj.keySet() ){
                hrb.field(key, jsonObj.get(key));
            }
            rs = hrb.asString().getBody();
        }
        catch (Exception e){
            nlogger.logInfo(e, _url);
            rs = null;
        }
        return JSONObject.toJSON(rs);
    }

    private void getJSApiTicket() {
        String jsticket = null;
        if( TicketCallBack != null ){
            jsticket = TicketCallBack.apply(tokenCallback.build(appid));
        }
        if( jsticket == null ){
            JSONObject rlt = _api_token(WechatDef.API_URL_PREFIX + WechatDef.GET_TICKET_URL, "&type=jsapi");
            if ("true".equals(rlt.get("status"))) {
                jsticket = rlt.get("ticket").toString();
                TicketCallBack.apply( tokenCallback.build(appid).token(jsticket).expireTime(rlt.getLong("expires_in") - 10) );
            }
        }
        jsapi_ticket = jsticket;
        System.out.println("jsapi_ticket:" + jsapi_ticket);
    }

    /**
     * 获得api接口token
     */
    @SuppressWarnings("unchecked")
    private String update_token() {
        String token = null;
        JSONObject cacheJSON = null;
        if( AccessTokenCallBack != null ){
            cacheJSON = JSONObject.toJSON( AccessTokenCallBack.apply(tokenCallback.build(appid)) );
        }
        if( cacheJSON == null ){
            JSONObject rlt = replyJSON(WechatDef.API_URL_PREFIX + WechatDef.AUTH_URL + "appid=" + appid + "&secret=" + appsecret);
            if ("true".equals(rlt.get("status"))) {
                token = rlt.get("access_token").toString();
                getMenuStatus(token);
                rlt.put("menustatus", menu_status);
                token = updateTokenData(rlt);
                AccessTokenCallBack.apply(tokenCallback.build(appid).token(token).expireTime(rlt.getLong("expires_in") - 10));
            }
        }
        else{//缓存存在
            token = updateTokenData(cacheJSON);
        }
        return token;
	}

	/**
	 * 获得自定义菜单状态
	 */
	private void getMenuStatus(String token){
        //return replyJSON(url + "access_token=" + update_token());
        String url = WechatDef.API_URL_PREFIX + WechatDef.MENU_INFO_URL;
		JSONObject rlt = replyJSON(url + "access_token=" + token);
		if( rlt != null){
			try{
				menu_status = rlt.get("is_menu_open").toString().equals("1");
			}
			catch(Exception e){
				menu_status = false;
			}
        }
    }

    /**
     * 获得菜单
     *
     * @return
     */
    public String pullMenu() {
        return _api_token(WechatDef.API_URL_PREFIX + WechatDef.MENU_GET_URL ).toJSONString();
	}
	
	/**
	 * 通过网页上授权获得用户openid
	 * @return
	 */
	public String getOpenID(String code){
		String openid;
		JSONObject rlt = getAccessToken(code);
		try{
			openid = rlt.get("openid").toString();
			String access_token = rlt.get("access_token").toString();
			if( ! StringHelper.invaildString(access_token) ){//用户授权获得后记录access_token
                UserAccessTokenCallBack.apply( tokenCallback.build(appid + "_" + openid).token(access_token).expireTime(rlt.getLong("expires_in") - 10) );
			}
		}
		catch (Exception e) {
			// TODO: handle exception
			nlogger.logInfo(e);
			openid = "";
		}
		return openid;
	}
	
	/**通过openid获得对应的accesstoken
	 * @param openid
	 * @return
	 */
    public String getWebToken(String openid) {
        return UserAccessTokenCallBack.apply( tokenCallback.build(appid + "_" + openid) );
    }

    /**
     * 更新菜单
     *
     * @param menuString
     * @return
     */
    public String updateMenu(String menuString) {
        return replyJSON(WechatDef.API_URL_PREFIX + WechatDef.MENU_CREATE_URL + "access_token=" + update_token(), menuString, false).toJSONString();
    }

    /**
     * 删除菜单
     *
     * @return
     */
    public String deleteMenu() {
        return _api_token(WechatDef.API_URL_PREFIX + WechatDef.MENU_DELETE_URL).toJSONString();
    }

    public final static String replyMSG(Document getXML, JSONObject content) {
        return replyMSG(getXML, content, null);
    }

    public final static String replyMSG(Document getXML, JSONObject content, String msgtype) {
        Element root = getXML.getRootElement();
        String mgType = msgtype == null ? root.element("MsgType").getStringValue() : msgtype;
        return replyMSG(root.element("FromUserName").getStringValue(), root.element("ToUserName").getStringValue(), content, mgType);
    }

    private JSONObject getAccessToken(String code) {
        JSONObject rlt = replyJSON(WechatDef.API_BASE_URL_PREFIX + WechatDef.OAUTH_TOKEN_URL + "&appid=" + appid + "&secret=" + appsecret + "&code=" + code + "&grant_type=authorization_code");
        return rlt;
    }

	/**
	 * 获得用户信息
	 * @param openid 通过网页授权token openid
	 * @return
	 */
	public String getUserInfo4web(String openid) {
        JSONObject rlt = null;
        String access_token = getWebToken(openid);
        try {
            if (access_token != null) {
                rlt = replyJSON(WechatDef.API_BASE_URL_PREFIX + WechatDef.OAUTH_USERINFO_URL + "access_token=" + access_token + "&openid=" + openid + "&lang=zh_CN");
			}
		} catch(Exception e){
            rlt = null;
            nlogger.logInfo(e);
        }
        return ( rlt != null && rlt.get("status").toString().equals("true")) ? rlt.toJSONString() : "";
    }

    private JSONObject lasterror;

    /**
     * 获得微信服务器列表
     *
     * @return
     */
    public JSONObject getServerIP() {
        return _api_token(WechatDef.API_URL_PREFIX + WechatDef.CALLBACKSERVER_GET_URL);
    }

    /**
     * 新增客服
     *
     * @param id
     * @param password
     * @param name
     * @return
     */
    public boolean addkf(String id, String password, String name) {
        return kf_server(id, password, name, 1);
    }

    /**编辑客服
     * @param id
     * @param password
     * @param name
     * @return
	 */
	public boolean updatekf(String id, String password, String name) {
        return kf_server(id, password, name, 2);
    }

    /**删除客服
	 * @param id
	 * @param password
	 * @param name
	 * @return
	 */
	public boolean deletekf(String id,String password,String name){
		return kf_server(id,password,name,3);
	}

	/**
	 * @param id
	 * @param password
	 * @param name
	 * @param mode 1:增加，2:编辑，3:删除
	 * @return
	 */
    @SuppressWarnings("unchecked")
    private boolean kf_server(String id,String password,String name,int mode){
		JSONObject _ac = new JSONObject();
        _ac.put("kf_account", id);
        _ac.put("password", Md5.build(password));
        _ac.put("nickname", name);
        String url;
        switch (mode) {
            case 2:
			url = WechatDef.API_BASE_URL_PREFIX + WechatDef.CS_KF_ACCOUNT_UPDATE_URL;
                break;
            case 3:
			url = WechatDef.API_BASE_URL_PREFIX + WechatDef.CS_KF_ACCOUNT_DEL_URL;
			break;
		default:
			url = WechatDef.API_BASE_URL_PREFIX + WechatDef.CS_KF_ACCOUNT_ADD_URL;
			break;
		}
		lasterror = _api_token_post( url,_ac );
		return lasterror != null && lasterror.get("status").equals("true");
    }

    /**修改客服头像
	 * @param id
     * @param uri
     * @return
     */
    public boolean updatekfheadimg(String id, String uri) {
        JSONObject rlt = null;
        File file = FileHelper.newTempFile(uri);
        if (file != null) {
            String url = WechatDef.API_BASE_URL_PREFIX + WechatDef.CS_KF_ACCOUNT_UPLOAD_HEADIMG_URL + "/access_token=" + update_token() + "&kf_account=" + id;
            String rs;
            try{
                rs = Unirest.post(url).field( file.getName(), file ).asString().getBody();
            }
            catch (Exception e){
                nlogger.logInfo(e,url);
                rs = null;
            }
			rlt = resultfilter(rs);
        }
        return (rlt != null && rlt.get("status").toString().equals("true") );
	}
	
	/**获得客服列表
	 * @return
	 */
	public String getkflist(){
		String url = WechatDef.API_URL_PREFIX + WechatDef.CUSTOM_SERVICE_GET_KFLIST;
		return api_token(url);
	}
	
	/**按组群发消息
	 * @param groupid
	 * @param content
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject send2all(int groupid,JSONObject content,String type){
		JSONObject jObject = new JSONObject();
		//if( groupid != 0 ){
			JSONObject _obj = new JSONObject();
			if( groupid > 0){
				_obj.put("tag_id", groupid);
				_obj.put("is_to_all", false);
			}
			else{
				_obj.put("is_to_all", true);
			}
			jObject.put("filter", _obj);
		//}
		return sendMSG(jObject,content,type);
	}
	/**按照openid列表群发信息
	 * @param openids
	 * @param content
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public JSONObject send2all(JSONArray openids, JSONObject content, String type) {
        JSONObject jObject = new JSONObject();
        jObject.put("touser", openids.toJSONString());
        return sendMSG(jObject, content, type);
    }

    /**
     * 客服回复消息
     *
     * @param toUser
     * @param kfID    不为空包含客户账号
     * @param content 按照消息类型，填充
     * @param msgtype
     * @return
     */
    @SuppressWarnings("unchecked")
    public JSONObject replykf(String toUser, String kfID, JSONObject content, String msgtype) {
        JSONObject repObj = new JSONObject();
        repObj.put("touser", toUser);
        repObj.put("msgtype", msgtype);
        repObj.put(msgtype, content);
        if (kfID != null) {
            JSONObject cus = new JSONObject();
            cus.put("kf_account", kfID);
            repObj.put("customservice", cus);
        }
        String url = WechatDef.API_URL_PREFIX + WechatDef.CUSTOM_SEND_URL;
        return _api_token_post(url,repObj);
    }
	
	/**
	 * @param jObject	对象
	 * @param content	推送的内容对象
	 * @param type		推送的类型
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private JSONObject sendMSG(JSONObject jObject, JSONObject content, String type) {
        jObject.put(type, content);
        jObject.put("msgtype", type);
		//https://api.weixin.qq.com/cgi-bin/message/mass/sendall?access_token=ACCESS_TOKEN
		jObject.put("send_ignore_reprint",0);
		String url = WechatDef.API_URL_PREFIX + WechatDef.MASS_SEND_GROUP_URL ;
		// nlogger.logInfo(jObject);
		return _api_token_post(url, jObject);
    }

    /**
     * 删除群发任务
     *
     * @param MSGId
     * @return
     */
    @SuppressWarnings("unchecked")
	public JSONObject deleteMSG(long MSGId){
		JSONObject jObject = new JSONObject();
		jObject.put("msg_id", MSGId);
		String url = WechatDef.API_URL_PREFIX + WechatDef.MASS_DELETE_URL;
        return _api_token_post(url, jObject);
	}
	
	/**获得群发任务状态
	 * @param MSGId
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject stateMSG(long MSGId){
		JSONObject jObject = new JSONObject();
		String url = WechatDef.API_URL_PREFIX + WechatDef.MASS_QUERY_URL;
		jObject.put("msg_id", MSGId);
		return _api_token_post(url,jObject);
	}

	/**创建用户组
	 * @param name
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject createUserGroup(String name) {
        //https://api.weixin.qq.com/cgi-bin/groups/create?access_token=ACCESS_TOKEN
		JSONObject jObject = new JSONObject();
		JSONObject cObject = new JSONObject();
		cObject.put("name", name);
        jObject.put("group", cObject);
        String url = WechatDef.API_URL_PREFIX + WechatDef.GROUP_CREATE_URL;
		return _api_token_post(url,jObject);
	}
	
	/**获得用户分组数据
	 * @return
	 */
	public JSONObject getUserGroup(){
		String url = WechatDef.API_URL_PREFIX + WechatDef.GROUP_GET_URL;
		return _api_token(url);
    }

    /**根据Openid获得用户组id
     * @param openid
     * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getUserGroupID(String openid){
		JSONObject jObject = new JSONObject();
		jObject.put("openid", openid);
		String url = WechatDef.API_URL_PREFIX + WechatDef.USER_GROUP_URL;
		return _api_token_post(url,jObject);
	}
	
	/**更新用户组名称
	 * @param groupID
     * @param newName
     * @return
     */
    @SuppressWarnings("unchecked")
    public JSONObject updateUserGroup(long groupID,String newName){
		JSONObject jObject = new JSONObject();
		jObject.put("id", groupID);
		jObject.put("name", newName);
		String url = WechatDef.API_URL_PREFIX + WechatDef.GROUP_UPDATE_URL;
		return _api_token_post(url,jObject);
	}
	
	/**移动用户到新的用户组
	 * @param openid
	 * @param toGroupID
     * @return
     */
    @SuppressWarnings("unchecked")
    public JSONObject moveUser2Group(String openid,long toGroupID){
		JSONObject jObject = new JSONObject();
		jObject.put("openid", openid);
		jObject.put("to_groupid", toGroupID);
		String url = WechatDef.API_URL_PREFIX + WechatDef.GROUP_MEMBER_UPDATE_URL;
		return _api_token_post(url,jObject);
	}
	
	/**批量移动用户到新的用户组
     * @param openids
	 * @param toGroupID
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public JSONObject moveUsers2Group(JSONArray openids,long toGroupID){
		JSONObject jObject = new JSONObject();
		jObject.put("openid_list", openids.toJSONString());
		jObject.put("to_groupid", toGroupID);
		String url = WechatDef.API_URL_PREFIX + WechatDef.GROUP_MEMBER_UPDATE_URL;
		return _api_token_post(url,jObject);
	}
	
	/**删除用户分组
	 * @param groupID
	 * @return
     */
    @SuppressWarnings("unchecked")
    public JSONObject deleteUserGroup(long groupID){
		JSONObject jObject = new JSONObject();
		JSONObject cObject = new JSONObject();
		cObject.put("id", groupID);
		jObject.put("group",cObject);
        String url = WechatDef.API_URL_PREFIX + WechatDef.GROUP_DELETE_URL;
        return _api_token_post(url, jObject);
    }

    /**通过正常api token和user openid获得用户信息
	 * @param openid
	 * @return
	 */
	public JSONObject getUserInfo(String openid){
		return replyJSON(WechatDef.API_URL_PREFIX + WechatDef.USER_INFO_URL + "access_token=" + update_token() + "&openid=" + openid + "&lang=zh_CN");
	}

	/**通过正常api token和user openid获得用户组信息
     * @param userObj
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject getUsersInfo(JSONArray userObj){
		JSONObject tmp = new JSONObject();
		JSONArray  newArray = new JSONArray();
        for (Object _oObject : userObj) {
            tmp.put("openid", _oObject.toString());
			tmp.put("lang","zh-CN");
			newArray.add( tmp );
		}
		tmp = new JSONObject();
		tmp.put("user_list", newArray.toJSONString());
		String url = WechatDef.API_URL_PREFIX + WechatDef.USERS_INFO_URL;
		return _api_token_post(url,tmp);
	}

	//场景值ID，临时二维码时为32位非0整型
	/*
	 * http请求方式: POST
URL: https://api.weixin.qq.com/cgi-bin/qrcode/create?access_token=TOKEN
POST数据格式：json
POST数据例子：{"action_name": "QR_LIMIT_SCENE", "action_info": {"scene": {"scene_id": 123}}}
或者也可以使用以下POST数据创建字符串形式的二维码参数：
{"action_name": "QR_LIMIT_STR_SCENE", "action_info": {"scene": {"scene_str": "123"}}}
	 * */
	@SuppressWarnings("unchecked")
	public JSONObject createQRtmp(long expiretime,long eventid){

		JSONObject jObject = new JSONObject();
		jObject.put("expire_seconds", ( expiretime == 0 ) ? 30 : ( expiretime > 2592000) ? 2592000 : expiretime);
        jObject.put("action_name", "QR_SCENE");
        JSONObject _info = new JSONObject();
		JSONObject _scene = new JSONObject();
		_scene.put("scene_id", eventid);
		_info.put("scene", _scene);
		jObject.put("action_info", _info);
		String url = WechatDef.API_URL_PREFIX + WechatDef.CARD_QRCODE_CREATE;
		return _api_token_post(url,jObject);
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject createQR(long eventid,String eventName){

		JSONObject jObject = new JSONObject();
		jObject.put("action_name","QR_LIMIT_SCENE");
		JSONObject _info = new JSONObject();
		JSONObject _scene = new JSONObject();
		int len = eventName.length();
        if (eventid >= 1 && eventid <= 100000) {
            _scene.put("scene_id", eventid);
		}
        if( len > 0 && len < 65) {
            _scene.put("scene_str", eventName);
        }
        _info.put("scene", _scene);
        jObject.put("action_info", _info);
        String url = WechatDef.API_URL_PREFIX + WechatDef.CARD_QRCODE_CREATE;
		return _api_token_post(url,jObject);
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject mediaObject(File file){
		JSONObject media = new JSONObject();
        media.put("media", file);
		return media;
	}

	/**通过ticket获得QR码文件流
	 * @param ticket
	 * @return
	 */
	public byte[] getQR4Ticket(String ticket){
		String safeTicket = UrlCode.encode(ticket);
		String url = WechatDef.QRCODE_IMG_URL + safeTicket;
		return _api_token_file(url);
	}

	/**上传临时媒体素材
	 * @param file
	 * @return
	 */
	public JSONObject uploadmediatmp(File file) {
        String mediaType = WechatDef.getWebchatFileType(file);
        return uploadmediatmp(file, mediaType);
    }

    /**
     * 上传临时媒体素材
     *
     * @param file
     * @return
     */
    public JSONObject uploadmediatmp(File file, String mediaType) {
		String url = WechatDef.API_URL_PREFIX + WechatDef.MEDIA_UPLOAD_URL + "access_token=" + update_token() + "&type=" + mediaType;
		String rs;
		try{
		    HttpRequestWithBody hrb = Unirest.post(url);
		    JSONObject mjson = mediaObject(file);
		    for(String key : mjson.keySet()){
		        hrb.field(key, mjson.get(key));
            }
		    rs = hrb.asString().getBody();
        }
		catch (Exception e){
		    nlogger.logInfo(e ,url);
		    rs= null;
        }
		return JSONObject.toJSON(rs);
	}

	/**上传永久媒体素材
	 * @param file
	 * @return
	 */
	public JSONObject uploadmedia(File file) {
        String mediaType = WechatDef.getWebchatFileType(file);
        return uploadmedia(file, mediaType);
    }

    /**
     * 上传永久媒体素材
     *
     * @param file
     * @return
     */
    public JSONObject uploadmedia(File file, String mediaType){
		String url = WechatDef.API_URL_PREFIX + WechatDef.MEDIA_FOREVER_UPLOAD_URL + "access_token=" + update_token() + "&type=" + mediaType;
		String rs;
		try{
		    HttpRequestWithBody hrb = Unirest.post(url);
		    JSONObject mjson = mediaObject(file);
		    for(String key : mjson.keySet()){
		        hrb.field(key,mjson.get(key));
            }
		    rs = hrb.asString().getBody();
        }
		catch (Exception e){
		    nlogger.logInfo(e, url);
		    rs = null;
        }
		return JSONObject.toJSON(rs);
    }

    /**
     * 上传永久图文消息
     * 需要匹配一个素材图文模型
     *
     * @param articles
     * @return
     */
    @SuppressWarnings("unchecked")
	public JSONObject uploadArticles(JSONArray articles) {
        JSONObject jObject = new JSONObject();
        jObject.put("articles", articles);
		return _api_token_post(WechatDef.API_URL_PREFIX + WechatDef.MEDIA_FOREVER_NEWS_UPLOAD_URL, jObject);
	}
	
	/**上传永久图片素材
	 * @param file
	 * @return
	 */
	public JSONObject uploadimage(File file){
		String url = WechatDef.API_URL_PREFIX + WechatDef.MEDIA_UPLOADIMG_URL;
        return _api_token_post_file(url, mediaObject(file));
	}
	
	/**删除永久素材
	 * @param mediaID
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject deleteMaterial(String mediaID){
		JSONObject jObject = new JSONObject();
		jObject.put("media_id", mediaID);
		String url = WechatDef.API_URL_PREFIX + WechatDef.MEDIA_FOREVER_DEL_URL;
		return _api_token_post(url, jObject);
	}
	
	/**更新图文新闻
	 * @param mediaid
	 * @param index
     * @param article
     * @return
     */
    @SuppressWarnings("unchecked")
    public JSONObject updateArticles(String mediaid,int index,JSONObject article){
		JSONObject jObject = new JSONObject();
		jObject.put("media_id", mediaid);
        jObject.put("index", index);
        jObject.put("articles", article);
		String url = WechatDef.API_URL_PREFIX + WechatDef.MEDIA_FOREVER_NEWS_UPDATE_URL;
		return _api_token_post(url, jObject);
	}
	
	/**获得临时素材
	 * @param mediaid
	 * @return
	 */
	public byte[] materialTempData(String mediaid){
		String url = WechatDef.API_URL_PREFIX + WechatDef.MEDIA_GET_URL;
		return _api_token_file(url + "access_token=" + update_token() + "&media_id=" + mediaid);
    }
	
	/**获得图文,视频素材信息(永久)
	 * @param mediaid
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public JSONObject materialJSON(String mediaid){
		JSONObject jObject = new JSONObject();
		jObject.put("media_id", mediaid);
		String url = WechatDef.API_URL_PREFIX + WechatDef.MEDIA_FOREVER_GET_URL;
        return _api_token_post(url, jObject);
	}
	
	/**获得二进制文件的字节集(永久)
	 * @param mediaid
	 * @return
	 */
	@SuppressWarnings("unchecked")
    public byte[] materialByte(String mediaid) {
        JSONObject jObject = new JSONObject();
        jObject.put("media_id", mediaid);
        String url = WechatDef.API_URL_PREFIX + WechatDef.MEDIA_FOREVER_GET_URL;
        return _api_token_post_get_file(url, jObject);
    }

    /**
     * 获得素材数量
     *
     * @return
     */
    public JSONObject materialCount() {
        String url = WechatDef.API_URL_PREFIX + WechatDef.MEDIA_FOREVER_COUNT_URL;
        return _api_token(url);
    }
	
	/**分页方式获得素材数据
	 * @param type
	 * @param pageidx
	 * @param pageno
	 * @return
	 */
	public JSONObject matericalPage(String type, int pageidx, int pageno){
		if( pageidx < 1){
			pageidx = 1;
		}
		return matericallist(type, (pageidx - 1) * pageno,pageno);
	}

    /**获得指定类型的素材列表
     * @param type
     * @param skip
     * @param limit
     * @return
     */
	@SuppressWarnings("unchecked")
	public JSONObject matericallist(String type,int skip,int limit){

		JSONObject jObject = new JSONObject();
		jObject.put("type", type);
		jObject.put("offset", skip);
		jObject.put("count", limit);
		String url = WechatDef.API_URL_PREFIX + WechatDef.MEDIA_FOREVER_BATCHGET_URL;
		return _api_token_post(url, jObject);
	}
	
	@SuppressWarnings("unchecked")
    public JSONObject mediaid2toallmediaid(String mediaid, String title, String desp){
		//https://file.api.weixin.qq.com/cgi-bin/media/uploadvideo?access_token=ACCESS_TOKEN
		JSONObject jObject = new JSONObject();
		jObject.put("media_id", mediaid);
		jObject.put("title", title);
		jObject.put("description", desp);
		String url = WechatDef.UPLOAD_MEDIA_URL + WechatDef.MEDIA_VIDEO_UPLOAD;
		return _api_token_post(url,jObject);
	}
}
