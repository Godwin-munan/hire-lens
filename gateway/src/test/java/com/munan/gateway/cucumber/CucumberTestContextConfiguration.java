package com.munan.gateway.cucumber;

import com.munan.gateway.web.AutomatedIntegrationTest;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;

@CucumberContextConfiguration
@AutomatedIntegrationTest
//@WebAppConfiguration
public class CucumberTestContextConfiguration {}
