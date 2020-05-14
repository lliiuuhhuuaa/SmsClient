package com.lh.sms.client;

import com.lh.sms.client.data.enums.TablesEnum;
import com.lh.sms.client.data.service.SqlData;
import com.lh.sms.client.work.msg.entity.Message;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

import androidx.test.ext.junit.runners.AndroidJUnit4;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() {
        SqlData sqlData = new SqlData();
        List<Message> key = sqlData.listObject(TablesEnum.MSG_LIST.getTable(), Message.class, 1, "key");
        System.out.println(key);
    }
}
