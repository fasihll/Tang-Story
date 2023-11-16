package com.example.tangstory.ui.auth.login

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.tangstory.R
import com.example.tangstory.helper.EspressoIdlingResource
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
@LargeTest
class LoginActivityTest{

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setUp(){
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }



    @Test
    fun loginTest(){
       onView(withId(R.id.ed_login_email))
            .perform(typeText("fasih@gmail.com"),closeSoftKeyboard())
        onView(withId(R.id.ed_login_password))
            .perform(typeText("fasih123"),closeSoftKeyboard())
        onView(withId(R.id.loginButton))
            .inRoot(RootMatchers.isTouchable())
            .perform(click())
        onView(withText("success")).inRoot(RootMatchers.isSystemAlertWindow()).check(ViewAssertions
            .matches
            (isDisplayed()))
    }

    @After
    fun tearDown(){
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }


}