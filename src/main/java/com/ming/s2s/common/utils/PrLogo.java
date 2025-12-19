package com.ming.s2s.common.utils;

import com.alibaba.druid.support.json.JSONUtils;

public class PrLogo {
        public  static void  prLogo(){
                String logo =
                        "  __________________________________________________________  \n" +
                                " /   _____/\\_____  \\  /   _____/                            \\ \n" +
                                " \\_____  \\  /  ____/  \\_____  \\      SQL TO SPRINGBOOT      / \n" +
                                " /        \\/       \\  /        \\          (S 2 S)          /  \n" +
                                "/_______  /\\_______ \\/_______  /                            \\ \n" +
                                "        \\/         \\/        \\/      Powered by Ming       /  \n" +
                                " __________________________________________________________/   \n";

                System.out.println("\u001B[36m" + logo + "\u001B[0m"); // 使用青色打印
            }

}
