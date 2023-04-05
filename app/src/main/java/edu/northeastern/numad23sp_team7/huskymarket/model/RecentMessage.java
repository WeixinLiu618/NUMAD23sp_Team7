package edu.northeastern.numad23sp_team7.huskymarket.model;

import java.util.Date;

public class RecentMessage {
    private String user1Id;
    private String user1Name;
    private String user1Image;
    private String user2Id;
    private String user2Name;
    private String user2Image;
    private String lastMessage;
    private Date timestamp;

    public RecentMessage() {
    }

    public RecentMessage(String user1Id, String user1Name, String user1Image,
                         String user2Id, String user2Name, String user2Image,
                         String lastMessage, Date timestamp) {
        this.user1Id = user1Id;
        this.user1Name = user1Name;
        this.user1Image = user1Image;
        this.user2Id = user2Id;
        this.user2Name = user2Name;
        this.user2Image = user2Image;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getUser1Id() {
        return user1Id;
    }

    public void setUser1Id(String user1Id) {
        this.user1Id = user1Id;
    }

    public String getUser1Name() {
        return user1Name;
    }

    public void setUser1Name(String user1Name) {
        this.user1Name = user1Name;
    }

    public String getUser1Image() {
        return user1Image;
    }

    public void setUser1Image(String user1Image) {
        this.user1Image = user1Image;
    }

    public String getUser2Id() {
        return user2Id;
    }

    public void setUser2Id(String user2Id) {
        this.user2Id = user2Id;
    }

    public String getUser2Name() {
        return user2Name;
    }

    public void setUser2Name(String user2Name) {
        this.user2Name = user2Name;
    }

    public String getUser2Image() {
        return user2Image;
    }

    public void setUser2Image(String user2Image) {
        this.user2Image = user2Image;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }


}
