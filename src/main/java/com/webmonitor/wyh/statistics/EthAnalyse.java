package com.webmonitor.wyh.statistics;

import org.jnetpcap.protocol.lan.Ethernet;

public class EthAnalyse extends Statistics{

    private static EthAnalyse ethAna = new EthAnalyse();

    private EthAnalyse(){}

    public static EthAnalyse newInstance(){
        return ethAna;
    }

    public String analyse(Ethernet eth){
        return EthType.toString(eth.type());
    }


    @Override
    public void clean() {

    }


    /**
     * A table of EtherType values and their names.
     *
     * @author Mark Bednarczyk
     * @author Sly Technologies, Inc.
     */
    public enum EthType {

        /**
         * The IEE e_802 do t1 q.
         */
        IEEE_802DOT1Q(0x8100, "vlan - IEEE 802.1q"),
        /**
         * The I p4.
         */
        IP4(0x800, "ip version 4"),
        /**
         * The I p6.
         */
        IP6(0x86DD, "ip version 6"),

        LLDP(0x88CC, "LLDP"),

        EAPOL(0x888e, "EAPOL"),
        ;


        /**
         * To string.
         *
         * @param id the id
         * @return the string
         */
        public static String toString(int id) {
            for (EthType t : values()) {
                if (t.id == id) {
                    return t.description;
                }
            }

            return null;
        }

        /**
         * Value of.
         *
         * @param type the type
         * @return the ethernet type
         */
        public static EthType valueOf(int type) {
            for (EthType t : values()) {
                if (t.id == type) {
                    return t;
                }
            }

            return null;
        }

        /**
         * The description.
         */
        private final String description;

        /**
         * The id.
         */
        private final int id;

        /**
         * Instantiates a new ethernet type.
         *
         * @param id the id
         */
        private EthType(int id) {
            this.id = id;
            this.description = name().toLowerCase();
        }

        /**
         * Instantiates a new ethernet type.
         *
         * @param id          the id
         * @param description the description
         */
        private EthType(int id, String description) {
            this.id = id;
            this.description = description;

        }
    }
}
