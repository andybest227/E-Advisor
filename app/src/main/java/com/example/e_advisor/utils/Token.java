package com.example.e_advisor.utils;
public class Token {
        private static Token instance;
        private String token;

        public Token(){
            //Empty construction
        }

        public static synchronized Token getInstance(){
            if (instance == null){
                instance = new Token();
            }
            return instance;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
}
