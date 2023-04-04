package edu.northeastern.numad23sp_team7.huskymarket.model;

import java.util.Date;

public class RecentMessage {
    private String senderId;
    private String senderName;
    private String senderImage;
    private String receiverId;
    private String receiverName;
    private String receiverImage;
    private String lastMessage;
    private Date timestamp;

    public RecentMessage() {
    }

    public RecentMessage(String senderId, String senderName, String senderImage,
                         String receiverId, String receiverName, String receiverImage,
                         String lastMessage, Date timestamp) {
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderImage = senderImage;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.receiverImage = receiverImage;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderImage() {
        return senderImage;
    }

    public void setSenderImage(String senderImage) {
        this.senderImage = senderImage;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    public String getReceiverImage() {
        return receiverImage;
    }

    public void setReceiverImage(String receiverImage) {
        this.receiverImage = receiverImage;
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
