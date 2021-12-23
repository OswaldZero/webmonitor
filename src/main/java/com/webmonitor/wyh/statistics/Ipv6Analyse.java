package com.webmonitor.wyh.statistics;


import org.jnetpcap.protocol.network.Ip6;

public class Ipv6Analyse extends Statistics{

    private static Ipv6Analyse ipv6Ana = new Ipv6Analyse();

    private Ipv6Analyse(){}

    public static Ipv6Analyse newInstance(){
        return ipv6Ana;
    }

    public String analyse(Ip6 ip6){
        return Ip6Type.toString(ip6.next());
    }



    @Override
    public void clean() {

    }

    public enum Ip6Type {

        /**
         * Authentication Header [RFC4302].
         */
        AH("Authentication Header", 51),

        /**
         * any host internal protocol [IANA].
         */
        ANY_LOC("any host internal protocol", 61),

        /**
         * ARGUS [RWS4].
         */
        ARGUS("ARGUS", 13),

        /**
         * BBN RCC Monitoring [SGC].
         */
        BBN_RCC_MON("BBN RCC Monitoring", 10),

        /**
         * BNA [Salamon].
         */
        BNA("BNA", 49),

        /**
         * CBT [Ballardie].
         */
        CBT("CBT", 7),

        /**
         * Chaos [NC3].
         */
        CHAOS("Chaos", 16),

        /**
         * Datagram Congestion Control Protocol [RFC4340].
         */
        DCCP("Datagram Congestion Control Protocol", 33),

        /**
         * DCN Measurement Subsystems [DLM1].
         */
        DCN_MEAS("DCN Measurement Subsystems", 19),

        /**
         * Datagram Delivery Protocol [WXC].
         */
        DDP("Datagram Delivery Protocol", 37),

        /**
         * Dynamic Source Routing Protocol [RFC4728].
         */
        DSR("Dynamic Source Routing Protocol", 48),

        /**
         * Exterior Gateway Protocol [RFC888][DLM1].
         */
        EGP("Exterior Gateway Protocol", 8),

        /**
         * EMCON [BN7].
         */
        EMCON("EMCON", 14),

        /**
         * Encap Security Payload [RFC4303].
         */
        ESP("Encap Security Payload", 50),

        /**
         * Fibre Channel [Rajagopal].
         */
        FC("Fibre Channel", 133),

        /**
         * Gateway-to-Gateway [RFC823].
         */
        GGP("Gateway-to-Gateway", 3),

        /**
         * General Routing Encapsulation [Li].
         */
        GRE("General Routing Encapsulation", 47),

        /**
         * Host Monitoring [RFC869][RH6].
         */
        HMP("Host Monitoring", 20),

        /**
         * IPv6 Hop-by-Hop Option [RFC1883].
         */
        HOPORT("IPv6 Hop-by-Hop Option", 0),

        /**
         * Integrated Net Layer Security TUBA [GLENN].
         */
        I_NLSP("Integrated Net Layer Security  TUBA", 52),

        /**
         * Internet Control Message [RFC792].
         */
        ICMP("Internet Control Message", 1),

        /**
         * Inter-Domain Policy Routing Protocol [MXS1].
         */
        IDPR("Inter-Domain Policy Routing Protocol", 35),

        /**
         * IDPR Control Message Transport Proto [MXS1].
         */
        IDPR_CMTP("IDPR Control Message Transport Proto", 38),

        /**
         * Inter-Domain Routing Protocol [Hares].
         */
        IDRP("Inter-Domain Routing Protocol", 45),

        /**
         * Internet Group Management [RFC1112].
         */
        IGMP("Internet Group Management", 2),

        /**
         * any private interior gateway [IANA] (used by Cisco for their IGRP).
         */
        IGP("any private interior gateway", 9),

        /**
         * IL Transport Protocol [Presotto].
         */
        IL("IL Transport Protocol", 40),

        /**
         * Unreliable datagram protocol.
         */
        IP("IP in IP (encapsulation)", 4),

        /**
         * IP-within-IP Encapsulation Protocol [JI6].
         */
        IPIP("IP-within-IP Encapsulation Protocol", 94),

        /**
         * Ipv6 [Deering].
         */
        IPv6("Ipv6", 41),

        /**
         * Fragment Header for IPv6 [Deering].
         */
        IPv6_FRAG("Fragment Header for IPv6", 44),

        /**
         * ICMP for IPv6 [RFC1883].
         */
        IPv6_ICMP("ICMP for IPv6", 58),

        /**
         * No Next Header for IPv6 [RFC1883].
         */
        IPv6_NoNxt("No Next Header for IPv6", 59),

        /**
         * Destination Options for IPv6 [RFC1883].
         */
        IPv6_Opts("Destination Options for IPv6", 60),

        /**
         * Routing Header for IPv6 [Deering].
         */
        IPv6_ROUTE("Ipv6", 43),

        /**
         * IPX in IP [Lee].
         */
        IPX_In_IP("IPX in IP", 111),

        /**
         * Internet Reliable Transaction [RFC938][TXM].
         */
        IRTP("Internet Reliable Transaction", 28),

        /**
         * ISO Transport Protocol Class 4 [RFC905][RC77].
         */
        ISO_TP4("ISO Transport Protocol Class 4", 29),

        /**
         * Leaf-1 [BWB6].
         */
        LEAF_1("Leaf-1", 25),

        /**
         * Leaf-2 [BWB6].
         */
        LEAF_2("Leaf-2", 26),

        /**
         * MERIT Internodal Protocol [HWB].
         */
        MERIT_INP("MERIT Internodal Protocol", 32),

        /**
         * MFE Network Services Protocol [MFENET][BCH2].
         */
        MFE_NSP("MFE Network Services Protocol", 31),

        /**
         * IP Mobility [Perkins].
         */
        MOBILE("IP Mobility", 55),

        /**
         * MPLS-in-IP [RFC4023].
         */
        MPLS_in_IP("MPLS-in-IP", 137),

        /**
         * Multiplexing [IEN90][JBP].
         */
        MUX("Multiplexing", 18),

        /**
         * NBMA Address Resolution Protocol [RFC1735].
         */
        NARP("NBMA Address Resolution Protocol", 54),

        /**
         * Bulk Data Transfer Protocol [RFC969][DDC1].
         */
        NETBLT("Bulk Data Transfer Protocol", 30),

        /**
         * PUP [PUP][XEROX].
         */
        NVP_II("Network Voice Protocol", 11),

        /**
         * Protocol Independent Multicast [Farinacci].
         */
        PIM("Protocol Independent Multicast", 103),

        /**
         * Packet Radio Measurement [ZSU].
         */
        PRM("Packet Radio Measurement", 21),

        /**
         * CBT [Ballardie].
         */
        PUP("PUP", 12),

        /**
         * Reliable Data Protocol [RFC908][RH6].
         */
        RDP("Reliable Data Protocol", 27),

        /**
         * Reservation Protocol [Braden].
         */
        RSVP("Reservation Protocol", 46),

        /**
         * Source Demand Routing Protocol [DXE1].
         */
        SDRP("Source Demand Routing Protocol", 42),

        /**
         * SKIP [Markson].
         */
        SKIP("SKIP", 57),

        /**
         * Stream Control Transport Protocol [RFC4960].
         */
        SCTP("SCTP", 132),

        /**
         * Stream [RFC1190][RFC1819].
         */
        ST("Stream", 5),

        /**
         * Schedule Transfer Protocol [JMP].
         */
        STP("Schedule Transfer Protocol", 118),

        /**
         * IP with Encryption [JI6].
         */
        SWIPE("IP with Encryption", 53),

        /**
         * Transmission Control [RFC793].
         */
        TCP("Transmission Control", 6),

        /**
         * Third Party Connect Protocol [SAF3].
         */
        THIRD_PC("Third Party Connect Protocol", 34),

        /**
         * Transport Layer Security Protocol [Oberg] using Kryptonet key management.
         */
        TLSP("Transport Layer Security Protocol", 56),

        /**
         * TP++ Transport Protocol [DXF].
         */
        TP_PLUS("TP++ Transport Protocol", 39),

        /**
         * Trunk-1 [BWB6].
         */
        TRUNK_1("Trunk-1", 23),

        /**
         * Trunk-2 [BWB6].
         */
        TRUNK_2("Trunk-2", 24),

        /**
         * User Datagram [RFC768][JBP].
         */
        UDP("User Datagram", 17),

        /**
         * Cross Net Debugger [IEN158][JFH2].
         */
        XNET("Cross Net Debugger", 15),

        /**
         * XEROX NS IDP [ETHERNET][XEROX].
         */
        XNS_IDP("XEROX NS IDP", 22),

        /**
         * XTP [GXC].
         */
        XTP("XTP", 36),

        OSPF_IGP("OSPF", 89);

        /**
         * Name of the constant.
         *
         * @param type ip type number
         * @return constants name
         */
        public static String toString(int type) {
            for (Ip6Type t : values()) {
                for (int i : t.typeValues) {
                    if (i == type) {
                        return t.description;
                    }
                }
            }

            return Integer.toString(type);
        }

        /**
         * Converts a numerical type to constant.
         *
         * @param type Ip4 type number
         * @return constant or null if not found
         */
        public static Ip6Type valueOf(int type) {
            for (Ip6Type t : values()) {
                for (int i : t.typeValues) {
                    if (i == type) {
                        return t;
                    }
                }
            }

            return null;
        }

        /**
         * The description.
         */
        private final String description;

        /**
         * The type values.
         */
        private final int[] typeValues;

        /**
         * Instantiates a new ip4 type.
         *
         * @param typeValues the type values
         */
        private Ip6Type(int... typeValues) {
            this.typeValues = typeValues;
            this.description = name().toLowerCase();
        }

        /**
         * Instantiates a new ip4 type.
         *
         * @param description the description
         * @param typeValues  the type values
         */
        private Ip6Type(String description, int... typeValues) {
            this.typeValues = typeValues;
            this.description = description;

        }
    }
}
