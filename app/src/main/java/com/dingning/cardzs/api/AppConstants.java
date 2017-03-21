package com.dingning.cardzs.api;


public class AppConstants {

        public static final String HOST = "http://api.yudianedu.cn/v3/web/Rest";

        public final static String GET_SCHOOL_INFO = HOST + "/elecclasscard/getschoolinfo";          //获取学校信息

        public final static String GET_STUDENTINFO_BY = HOST + "/elecclasscard/getstudentinfo";          //获取学生信息

        public final static String GET_EXHORTLISTBYCARD_BY = HOST + "/elecclasscard/getexhortlistbycard";          //获取学生信息列表

        public final static String GET_EXHORTBYID_BY = HOST + "/elecclasscard/getexhortbyid";          //获取信息详情

        public final static String ADD_EXHORTREPLY_BY = HOST + "/elecclasscard/addexhortreply";          //上传视频，语音

        public final static String GET_CARDVERSION_BY = HOST + "/elecclasscard/getcardversion";          //版本更新

}
