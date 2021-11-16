package com.example.momogae.Chat;

public class ChatRoomModel {
    private String roomID;
    private String title;
    private String photo;
    private String lastMsg;
    private String lastDatetime;
    private Integer userCount;
    private Integer unreadCount;

    public String getRoomID() {
        return roomID;
    }
    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getPhoto() {
        return photo;
    } //프로필사진
    public void setPhoto(String photo) {
        this.photo = photo;
    } //프로필사진
    public String getLastMsg() {
        return lastMsg;
    }
    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }
    public String getLastDatetime() {
        return lastDatetime;
    }
    public void setLastDatetime(String lastDatetime) {
        this.lastDatetime = lastDatetime;
    }
    public Integer getUserCount() {
        return userCount;
    } // 채팅에 참여하는 유저 수
    public void setUserCount(Integer userCount) {
        this.userCount = userCount;
    } //채팅에 참여하는 유저 수
    public Integer getUnreadCount() {
        return unreadCount;
    } //읽지 않은 사람의 수
    public void setUnreadCount(Integer unreadCount) { this.unreadCount = unreadCount; } //읽지 않은 사람의 수
}