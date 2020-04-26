package common.wechat;

public class tokenCallback {
    private String appid;
    private String tokenValue;
    private long expireTime;
    public static final tokenCallback build(String appid){
        return new tokenCallback(appid);
    }
    private tokenCallback(String appid){
        this.appid = appid;
    }
    public tokenCallback token(String tokenValue){
        this.tokenValue = tokenValue;
        return this;
    }
    public tokenCallback expireTime(long newExpireTime){
        this.expireTime = newExpireTime;
        return this;
    }
    public String appid(){
        return this.appid;
    }
    public String tokenValue(){
        return this.tokenValue;
    }
    public long expireTime(){
        return this.expireTime;
    }
    /**
     * 判断是否是查询数据
     * */
    public boolean request(){
        return this.tokenValue == null;
    }
}
