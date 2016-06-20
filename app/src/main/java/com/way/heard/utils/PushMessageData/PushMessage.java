package com.way.heard.utils.PushMessageData;

import android.content.Context;

import com.orm.androrm.CharField;
import com.orm.androrm.DateField;
import com.orm.androrm.Filter;
import com.orm.androrm.Model;
import com.orm.androrm.QuerySet;
import com.way.heard.base.HeardApp;

import java.util.Date;
import java.util.List;

public class PushMessage extends Model {

    protected CharField message_from;
    protected CharField message_content;
    protected DateField date;

    public PushMessage() {
        super(true);
        message_from = new CharField();
        message_content = new CharField();
        date = new DateField();
    }

    public static PushMessage create(String mMessagefrom, String mMessagecontent, Date mDate) {
        PushMessage pushMessage = new PushMessage();
        pushMessage.setMessageFrom(mMessagefrom);
        pushMessage.setMessageContent(mMessagecontent);
        pushMessage.setDate(mDate);
        pushMessage.save();
        return pushMessage;
    }


    public String getMessageFrom() {
        return message_from.get();
    }

    public void setMessageFrom(String username) {
        message_from.set(username);
    }

    public String getMessageContent() {
        return message_content.get();
    }

    public void setMessageContent(String content) {
        message_content.set(content);
    }

    public void setDate(Date d) {
        date.set(d);
    }

    public Date getDate() {
        return date.get();
    }

    private static String formatProjectForQuery(String name) {
        String name1 = name;
        return name1;
    }

    public static List<PushMessage> logSortByProjectType(String Key_) {
        String query_string = formatProjectForQuery(Key_);
        Filter filter = new Filter();
        filter.contains("message_content", query_string);
        return PushMessage.objects().filter(filter).orderBy("message_content").toList();
    }

    public boolean save() {
        int id = PushMessage.objects(context(), PushMessage.class).all().count() + 1;
        return this.save(context(), id);
    }

    public boolean edit() {
        return this.save(context());
    }

    public boolean delete() {
        return this.delete(context());
    }

    public static List<PushMessage> all() {
        return PushMessage.objects().all().orderBy("-date").toList();
    }

//    public static void deleteAll(){
//        DeleteStatement mDelete=new DeleteStatement();
//        mDelete.from("PushMessage");
//    }

    public static List<PushMessage> FilterByMessageContent(String content) {
        Filter filter = new Filter();
        filter.contains("message_content", content);
        return PushMessage.objects().filter(filter).orderBy("message_content").toList();
    }

    public static QuerySet<PushMessage> objects() {
        return PushMessage.objects(context(), PushMessage.class);
    }

    /**
     * Get application context
     *
     * @return
     */
    private static Context context() {
        return HeardApp.getContext();
    }
}
