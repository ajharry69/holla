package com.xently.holla.ui.list.message

import androidx.navigation.NavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.xently.holla.R
import com.xently.holla.data.model.Contact
import org.junit.Before
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class MessageListFragmentTest {
    private lateinit var scenario: FragmentScenario<MessageListFragment>
    private lateinit var navController: NavController

    @Before
    fun setUp() {
        navController = Mockito.mock(NavController::class.java)
        scenario = launchFragmentInContainer(
            themeResId = R.style.AppTheme,
            factory = MessageListFragmentFactory(Contact())
        )
        with(scenario) {
            onFragment {
                androidx.navigation.Navigation.setViewNavController(it.requireView(), navController)
            }
        }
    }
}