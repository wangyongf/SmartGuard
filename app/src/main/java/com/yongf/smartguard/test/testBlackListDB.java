package com.yongf.smartguard.test;

import android.test.AndroidTestCase;

import com.yongf.smartguard.db.BlackListDBOpenHelper;
import com.yongf.smartguard.db.dao.BlackListDao;

/**
 * @author Scott Wang
 * @Description:
 * @date 2016/2/11 20:02
 * @Project SmartGuard
 */
public class testBlackListDB extends AndroidTestCase {
    BlackListDBOpenHelper helper = new BlackListDBOpenHelper(getContext());
    BlackListDao dao = new BlackListDao(getContext());

    public void testCreateDB() throws Exception {
        helper.getWritableDatabase();
    }

    public void testFind() throws Exception {
        boolean result = dao.findOne("110");
        assertEquals(true, result);
    }

    public void testInsert() throws Exception {
        dao.insert("110", "1");
    }

    public void testUpdate() throws Exception {
        dao.update("110", "2");
    }

    public void testDelete() throws Exception {
        dao.delete("110");
    }
}
