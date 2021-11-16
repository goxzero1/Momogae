package com.example.momogae.Chat;

import java.util.HashMap;
import java.util.Map;

public class ChatModel {
    public Map<String, String> users = new HashMap<>() ;

    public static class FileInfo {
        public String filename; //채팅 이미지
        public String filesize; //채팅 이미지
    }
}