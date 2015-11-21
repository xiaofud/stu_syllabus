package com.hjsmallfly.syllabus.interfaces;

import com.hjsmallfly.syllabus.syllabus.SyllabusVersion;

/**
 * Created by STU_nwad on 2015/10/14.
 */
public interface UpdateHandler {

    public static int EXIST_UPDATE = 0;
    public static int ALREADY_UPDATED = 1;
    public static int CONNECTION_ERROR = 2;

    /**
     * 有新的版本
     * @param version
     */
    public void deal_with_update(int flag, SyllabusVersion version);

}
