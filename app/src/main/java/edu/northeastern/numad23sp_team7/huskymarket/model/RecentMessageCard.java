package edu.northeastern.numad23sp_team7.huskymarket.model;

import java.util.Date;

public class RecentMessageCard {

//    private String recentMessageId;
    private String lastMessage;

    public String getDisplayedUserId() {
        return displayedUserId;
    }

    public void setDisplayedUserId(String displayedUserId) {
        this.displayedUserId = displayedUserId;
    }

    private Date timestamp;
    private String displayedUserId;
    private String displayedUsername;
    private String displayedUserImage;

    public RecentMessageCard() {

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


    public String getDisplayedUsername() {
        return displayedUsername;
    }

    public void setDisplayedUsername(String displayedUsername) {
        this.displayedUsername = displayedUsername;
    }

    public String getDisplayedUserImage() {
        return displayedUserImage;
    }

    public void setDisplayedUserImage(String displayedUserImage) {
        this.displayedUserImage = displayedUserImage;
    }


//    public String getRecentMessageId() {
//        return recentMessageId;
//    }
//
//    public void setRecentMessageId(String recentMessageId) {
//        this.recentMessageId = recentMessageId;
//    }
}
