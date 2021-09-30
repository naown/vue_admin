package com.naown;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;

@SpringBootTest
class AdminApplicationTests {

    @Test
    void contextLoads() {
    }

    public static void main(String[] args) {
        String[] data = {"123","456","789","987"};
        String s = String.join(",", data);
        System.out.println(s);
        String[] split = s.split(",");
        System.out.println(Arrays.toString(split));
    }

}
