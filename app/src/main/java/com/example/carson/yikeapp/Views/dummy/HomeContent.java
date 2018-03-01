package com.example.carson.yikeapp.Views.dummy;

/**
 * Helper class for providing sample content for ic_user interfaces created by
 * Android template wizards.
 * <p>
 */
public class HomeContent {
    /**
     * A dummy item representing a piece of content.
     */
    public static class BNBHomeItem {
        public final String moreDetail;
        public final String id;
        public final String hotelId;
        public final String name;
        public final String host;
        public final String time;
        public final String duration;
        public final String loca;

        //设置传入值-id-name-host-time-duration
        public BNBHomeItem(String id,String name, String host,String hotelId, String time,String duration,String loca,String moreDetail) {
            this.id = id;
            this.name = name;
            this.host = host;
            this.hotelId = hotelId;
            this.time = time;
            this.duration = duration;
            this.loca = loca;
            this.moreDetail = moreDetail;
        }

        @Override
        public String toString() {
            //设置tostring
            return name+host+time+duration+loca;
        }
    }
}
