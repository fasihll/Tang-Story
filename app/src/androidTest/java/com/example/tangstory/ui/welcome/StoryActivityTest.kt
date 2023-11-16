package com.example.tangstory.ui.welcome


import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.*
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.tangstory.R
import com.example.tangstory.helper.EspressoIdlingResource
import com.example.tangstory.ui.story.story.StoryActivity
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith



@RunWith(AndroidJUnit4::class)
@LargeTest
class StoryActivityTest{

    @get:Rule
    val activity = ActivityScenarioRule(StoryActivity::class.java)

    @Before
    fun setUp(){
        Intents.init()
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }



    @Test
    fun logoutTest(){
       onView(withId(R.id.action_logout)).check(ViewAssertions.matches(isDisplayed()))
        onView(withId(R.id.action_logout)).perform(click())
        onView(withText("Logged out")).inRoot(RootMatchers.isSystemAlertWindow()).check(ViewAssertions.matches
            (isDisplayed()))
    }

    @After
    fun tearDown(){
        Intents.release()
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }


}