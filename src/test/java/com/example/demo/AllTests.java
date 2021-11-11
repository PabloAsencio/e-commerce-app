package com.example.demo;

import com.example.demo.controllers.*;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
                SareetaApplicationTests.class,
                UserControllerUnitTest.class,
                UserControllerIntegrationTest.class,
                CartControllerUnitTest.class,
                OrderControllerTest.class,
                ItemControllerUnitTest.class,
                ItemControllerIntegrationTest.class
        })
public class AllTests {
}
