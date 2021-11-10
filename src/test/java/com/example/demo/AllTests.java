package com.example.demo;

import com.example.demo.controllers.CartControllerTest;
import com.example.demo.controllers.ItemControllerUnitTest;
import com.example.demo.controllers.OrderControllerTest;
import com.example.demo.controllers.UserControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
                SareetaApplicationTests.class,
                UserControllerTest.class,
                CartControllerTest.class,
                OrderControllerTest.class,
                ItemControllerUnitTest.class
        })
public class AllTests {
}
