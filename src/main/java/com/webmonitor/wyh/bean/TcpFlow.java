package com.webmonitor.wyh.bean;

import java.util.Arrays;
import java.util.List;

public class TcpFlow {

    private long firstFrameId;

    private String identify;

    private List<Long> pkts;

    public long getFirstFrameId() {
        return firstFrameId;
    }

    public void setFirstFrameId(long firstFrameId) {
        this.firstFrameId = firstFrameId;
    }

    public String getIdentify() {
        return identify;
    }

    public void setIdentify(String identify) {
        this.identify = identify;
    }

    public List<Long> getPkts() {
        return pkts;
    }

    public void setPkts(List<Long> pkts) {
        this.pkts = pkts;
    }

    @Override
    public String toString() {
        return "TcpFlow{" +
                "firstFrameId=" + firstFrameId +
                ", identify='" + identify + '\'' +
                ", pkts=" + Arrays.toString(pkts.toArray()) +
                '}'+"\n";
    }
}
