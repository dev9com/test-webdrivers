package com.dev9.it.bug_1;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.pagefactory.AjaxElementLocatorFactory;

import static util.Util.GITHUB_DOMAIN;
import static util.Util.HTTP_PROTOCOL;

public class PageObject {
    @FindBy(css = "a[href='/explore']") WebElement explore;

    public PageObject(final WebDriver driver) {
        driver.get(HTTP_PROTOCOL + GITHUB_DOMAIN);
        PageFactory.initElements(new AjaxElementLocatorFactory(driver, 5), this);
    }

    public void clickExplore() {
        explore.click();
    }
}
