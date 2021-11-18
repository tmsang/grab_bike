package com.intec.grab.bike.messages;

public class MessageDto {
    int image;
    String line1;
    String line2;
    String publishDate;

    public MessageDto(int image, String line1, String line2, String publishDate) {
        this.image = image;
        this.line1 = line1;
        this.line2 = line2;
        this.publishDate = publishDate;
    }
    public int getImage() { return image; }
    public String getLine1() {
        return line1;
    }
    public String getLine2() {
        return line2;
    }
    public String getPublishDate() { return publishDate; }
}
