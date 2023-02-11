package com.example.benchmark

import androidx.benchmark.macro.*
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * This is an example startup benchmark.
 *
 * It navigates to the device's home screen, and launches the default activity.
 *
 * Before running this benchmark:
 * 1) switch your app's active build variant in the Studio (affects Studio runs only)
 * 2) add `<profileable android:shell="true" />` to your app's manifest, within the `<application>` tag
 *
 * Run this benchmark from Studio to see startup measurements, and captured system traces
 * for investigating your app's performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleStartupBenchmark {
    @get:Rule
    val benchmarkRule = MacrobenchmarkRule()


    @Test
    fun startupCold() = startup(CompilationMode.None())

    @Test
    fun startupWarm() = startup(CompilationMode.Partial())

    @Test
    fun openCompanyDashboardCold() = openCompanyDashboard(CompilationMode.None())

    @Test
    fun openCompanyDashboardWarm() = openCompanyDashboard(CompilationMode.Partial())

    fun startup(mode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.example.einvoice",
        metrics = listOf(StartupTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = mode
    ) {
        pressHome()
        startActivityAndWait()
    }

    fun openCompanyDashboard(mode: CompilationMode) = benchmarkRule.measureRepeated(
        packageName = "com.example.einvoice",
        metrics = listOf(FrameTimingMetric()),
        iterations = 5,
        startupMode = StartupMode.COLD,
        compilationMode = mode
    ) {
        pressHome()
        startActivityAndWait()
        clickOnCompanyDashboardAndClickCreateButton()
        device.waitForIdle()
    }

}

fun MacrobenchmarkScope.clickOnCompanyDashboardAndClickCreateButton() {
    login()
    device.wait(Until.hasObject(By.res("CompaniesScreenCompanyItem")), 5000)
    clickOnCompany()
    device.wait(Until.hasObject(By.res("CompanyDashboardTab")), 5000)
    clickTabs()
    device.wait(Until.hasObject(By.res("CompanyDashboardCreateDocumentButton")), 5000)
    navigateToDocumentForm()
    device.wait(Until.hasObject(By.res("DocumentFormScreen")), 5000)
    logOut()
}

private fun MacrobenchmarkScope.logOut() {
    val logout = device.findObject(By.res("Logout"))
    logout.click()
}

private fun MacrobenchmarkScope.navigateToDocumentForm() {
    val createButton = device.findObject(By.res("CompanyDashboardCreateDocumentButton"))
    createButton.click()
}

private fun MacrobenchmarkScope.clickTabs() {
    val tabs = device.findObjects(By.res("CompanyDashboardTab"))
    tabs.forEach {
        it.click()
        device.waitForIdle()
    }
}

private fun MacrobenchmarkScope.clickOnCompany() {
    val companyCard = device.findObject(By.res("CompaniesScreenCompanyItem"))
    companyCard.click()
}

private fun MacrobenchmarkScope.login() {
    val loginButton = device.findObject(By.res("login"))
    loginButton.click()
}
