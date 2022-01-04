package com.webmonitor.wyh.controller;

import com.webmonitor.wyh.bean.FormatPacket;
import com.webmonitor.wyh.deal.CatchPacket;
import com.webmonitor.wyh.deal.PacketTransport;
import com.webmonitor.wyh.result.ResultMap;
import com.webmonitor.wyh.statistics.ProtocolAnalyse;
import com.webmonitor.wyh.utils.FormatTime;
import org.jnetpcap.packet.PcapPacket;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.BlockingQueue;

/**
 * @author Wuyihu
 * @version 1.0
 * @date 2021/12/22
 */
@RestController
@RequestMapping("/web")
public class WebAadptor {

    private ArrayList<PcapPacket> store = new ArrayList<>();
    private ArrayList<FormatPacket> simpleStore = new ArrayList<>();
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

    /**
     * 根据设备号选择网络适配器，并开始监听
     * @param adaptorNum
     * @return
     */
    @PostMapping("/selectAdaptor")
    public ResultMap selectAdaptor(@RequestParam("adaptorNum")Integer adaptorNum){
        PacketTransport pktTrans = PacketTransport.newInstance();
        pktTrans.setIndex(adaptorNum);
        pktTrans.initPcap();
        store.clear();
        pktTrans.startCatchPacket();
        return ResultMap.success("开始监听");

    }

    /**
     * 显示从开始监听到现在的监听到的包（简略的包列表）
     * @return
     */
    @PostMapping("/showInformation")
    public ResultMap getSelectedAdaptorInformation(){
        PacketTransport packetTransport = PacketTransport.newInstance();
        String deviceInfo = packetTransport.getDeviceInfo(packetTransport.getIndex());
        BlockingQueue<PcapPacket> pcapPackets = packetTransport.getPacketQueue();
        int size = pcapPackets.size();
        ProtocolAnalyse ptlAna = ProtocolAnalyse.newInstance();
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
                simpleStore.add(pkt);
            }
        } catch (InterruptedException e) {
                e.printStackTrace();
        }
        return ResultMap.success(simpleStore);
    }

    /**
     * 根据某个具体的包的id，显示该包具体信息
     * @param pkgNum
     * @return
     */
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

    /**
     * 对这段时间内的发送的包的数目进行
     * 流量分析
     * @return
     */
    @PostMapping("/flowStatistics")
    public ResultMap flowStatistics(){
        HashMap<String, Integer> map = new HashMap<>();
        ProtocolAnalyse ptlAna = ProtocolAnalyse.newInstance();
        for (int i = 0; i < store.size(); i++) {
            PcapPacket packet = store.get(i);
            ptlAna.analyse(packet);
            String protocolName = ptlAna.getProtocolName();
            if (!map.containsKey(protocolName)){
                map.put(protocolName,1);
            }else {
                Integer integer = map.get(protocolName);
                map.put(protocolName,integer+1);
            }
        }
        map.put("all",store.size());
        return ResultMap.success(map);
    }
    /**
     * 基于协议过滤
     */
    @PostMapping("/filter/protocol")
    public ResultMap flowFilterByProtocol(@RequestParam("protocol") String protocol){
        ArrayList<FormatPacket> list = new ArrayList<>();
        for (int i = 0; i < simpleStore.size(); i++) {
            FormatPacket formatPacket = simpleStore.get(i);
            if (formatPacket.getProtocol().equals(protocol)) {
                list.add(formatPacket);
            }
        }
        return ResultMap.success(list);
    }

    /**
     * 基于目的地址过滤
     */
    @PostMapping("/filter/des")
    public ResultMap flowFilterByDes(@RequestParam("des") String des){
        ArrayList<FormatPacket> list = new ArrayList<>();
        for (int i = 0; i < simpleStore.size(); i++) {
            FormatPacket formatPacket = simpleStore.get(i);
            if (formatPacket.getDest().equals(des)) {
                list.add(formatPacket);
            }
        }
        return ResultMap.success(list);
    }

    /**
     * 基于源地址过滤
     */
    @PostMapping("/filter/src")
    public ResultMap flowFilterBySrc(@RequestParam("src") String src){
        ArrayList<FormatPacket> list = new ArrayList<>();
        for (int i = 0; i < simpleStore.size(); i++) {
            FormatPacket formatPacket = simpleStore.get(i);
            if (formatPacket.getSrc().equals(src)) {
                list.add(formatPacket);
            }
        }
        return ResultMap.success(list);
    }
}
