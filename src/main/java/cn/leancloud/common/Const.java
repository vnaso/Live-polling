package cn.leancloud.common;

public class Const {
    public static final String SCKEY = "SCU27968T968c6ed25f2893356cc6493550fa72db5c6ac73571472";

    public enum CheckState{
        /** 未签到 */
        UNCHECKED(0),
        /** 签到中 */
        CHECKING(1),
        /** 已签到 */
        CHECKED(2)
        ;

        int code = 0;
        CheckState(int code){
            this.code = code;
        }

        public int getCode(){
            return this.code;
        }
    }
}
