package com.dingning.cardzs.utils;

import java.util.Set;

/**
 * Created by Allen on 2016/12/14.
 */

public class StringUtils {

    public static String getParentIds(Set<String> mParentIds){

        StringBuilder sb = new StringBuilder();
        for (String p: mParentIds){
            sb.append(p);
            sb.append(",");
        }
        return sb.substring(0, sb.length() -1);
    }

    public static String filterParams(String params){

        String  str = params.replace("[","");
        return str.replace("]","");
    }

    public static int getTotalPage(int total, int num){

        int page = total/num;
        int residue = total%num;

        if(residue >0){
            page ++;
        }
        return page;
    }

    public static int getCurrentPage(int firstItemPosition, int num){

        int page = 0;

        if(firstItemPosition == 0 || firstItemPosition < num){
            page ++;
            return  page;
        }

        page = firstItemPosition/num;
        int residue = firstItemPosition % num;


        if(residue >0){
            page = page++;
        }
        return page;
    }
}
