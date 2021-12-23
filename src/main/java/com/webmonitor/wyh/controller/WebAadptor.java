package com.webmonitor.wyh.controller;

import com.webmonitor.wyh.bean.FormatPacket;
import com.webmonitor.wyh.deal.CatchPacket;
import com.webmonitor.wyh.deal.PacketTransport;
import com.webmonitor.wyh.result.ResultMap;
import com.webmonitor.wyh.statistics.ProtocolAnalyse;
import com.webmonitor.wyh.thread.ThreadManager;
import com.webmonitor.wyh.utils.FormatTime;
import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Wuyihu
 * @version 1.0
 * @date 2021/12/22
 */
@RestController
@RequestMapping("/web")
public class WebAadptor {

    private ArrayList<PcapPacket> store = new ArrayList<>();
    /**
     * 返回所有的adaptor信息
     * @return
     */
    @PostMapping("/showAdaptors")
    public ResultMap getAdaptorsList(){
        CatchPacket catchPacket = CatchPacket.newInstance();
        if (null==catchPacket.getAlldevs() || catchPacket.getAlldevs().size()==0){
            return ResultMap.success(catchPacket.getAllDevice());
        }
        return ResultMap.success(catchPacket.getAlldevs());
    }

    @PostMapping("/selectAdaptor")
    public ResultMap selectAdaptor(@RequestParam("adaptorNum")Integer adaptorNum){
        PacketTransport pktTrans = PacketTransport.newInstance();
        pktTrans.setIndex(adaptorNum);
        pktTrans.initPcap();
        store.clear();
        pktTrans.startCatchPacket();
        return ResultMap.success("开始监听");

    }

    @PostMapping("/showInformation")
    public ResultMap getSelectedAdaptorInformation(){
        PacketTransport packetTransport = PacketTransport.newInstance();
        String deviceInfo = packetTransport.getDeviceInfo(packetTransport.getIndex());
        BlockingQueue<PcapPacket> pcapPackets = packetTransport.getPacketQueue();
        LinkedBlockingQueue<FormatPacket> fpts = new LinkedBlockingQueue<>();
        int size = pcapPackets.size();
        ProtocolAnalyse ptlAna = ProtocolAnalyse.newInstance();
//        return ResultMap.success();
        try {
            for (int i = size; i >0; i--) {
                PcapPacket packet = pcapPackets.take();
                store.add(packet);
                FormatPacket pkt = new FormatPacket();
                pkt.setInfo(ptlAna.analyse(packet));
                pkt.setNo((int) packet.getFrameNumber());
                pkt.setTime(FormatTime.formatTime(new Date(packet.getCaptureHeader().timestampInMillis())));
                pkt.setSrc(ptlAna.getSrc());
                pkt.setDest(ptlAna.getDest());
                pkt.setProtocol(ptlAna.getProtocolName());
                pkt.setLength(packet.getPacketWirelen());
                fpts.add(pkt);
            }
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
        return ResultMap.success(fpts);
    }

    @PostMapping("/showDetail")
    public ResultMap showDetail(@RequestParam("pkgNum")Integer pkgNum){
        System.out.println("stroe: "+ store.size());
        PcapPacket packet = store.get(pkgNum-1);
        String pkgStr = packet.toString();
        String pkgHex = packet.toHexdump();
        HashMap<String, String> detail = new HashMap<>();
        detail.put(pkgStr,pkgHex);
        return ResultMap.success(detail);
    }

}
