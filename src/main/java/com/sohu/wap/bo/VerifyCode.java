package com.sohu.wap.bo;
 public class VerifyCode{
        private String vcode;
        private String cookie;
       public VerifyCode(String code, String cookie){
           this. vcode = code;
            this.cookie = cookie;
        }
        /**
         * @param cookie the cookie to set
         */
        public void setCookie(String cookie) {
            this.cookie = cookie;
        }
        /**
         * @return the cookie
         */
        public String getCookie() {
            return cookie;
        }
        /**
         * @param vcode the vcode to set
         */
        public void setVcode(String vcode) {
            this.vcode = vcode;
        }
        /**
         * @return the vcode
         */
        public String getVcode() {
            return vcode;
        }
        
    }