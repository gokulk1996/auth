package com.cme.budderfly.authenticate.cucumber.stepdefs;

import com.cme.budderfly.authenticate.AuthenticateApp;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.ResultActions;

import org.springframework.boot.test.context.SpringBootTest;

@WebAppConfiguration
@SpringBootTest
@ContextConfiguration(classes = AuthenticateApp.class)
public abstract class StepDefs {

    protected ResultActions actions;

}
