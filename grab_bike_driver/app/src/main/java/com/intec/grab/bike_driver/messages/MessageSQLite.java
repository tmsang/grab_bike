package com.intec.grab.bike_driver.messages;

import android.content.Context;

import com.intec.grab.bike_driver.utils.helper.SQLiteHelper;

import org.json.JSONArray;

public class MessageSQLite extends SQLiteHelper {

    public MessageSQLite(Context context) {
        super(context);
    }

    public void insertIfNotExists(String messageId, String title, String description, String publishDate) {
        String sql = "INSERT OR IGNORE INTO messages (id, title, description, publishDate, changeDate, flag) " +
                " VALUES ('" + messageId + "', '" + title + "', '" + description + "', '" + publishDate + "', datetime(), '0')";

        this.action(sql);
    }

    public JSONArray getMessages() {
        String sql = "SELECT * FROM messages ORDER BY publishDate DESC";

        JSONArray result = this.select(sql);
        return result;
    }

    public String getMessageStatus(String messageId) {
        String sql = "SELECT flag FROM messages WHERE id = '" + messageId + "'";

        String result = this.scalar(sql);
        return result;
    }

    public void updateMessageStatus(String messageId) {
        String sql = "UPDATE messages SET changeDate = datetime(), flag = '1' WHERE id = '" + messageId + "'";

        this.action(sql);
    }


}
