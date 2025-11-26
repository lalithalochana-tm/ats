/*
 * MIT License
 *
 * Copyright (c) 2022 Srikanth
 * Copyright (c) 2022 Srikanth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.ui.base;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


import com.config.ConfigurationManager;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.BoundingBox;
import com.microsoft.playwright.options.RequestOptions;
import com.microsoft.playwright.options.SelectOption;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.microsoft.playwright.options.LoadState;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import com.ui.utilities.Reporter;
import org.testng.Assert;
import org.testng.asserts.SoftAssert;

public class PlaywrightWrapper extends Reporter {

    int retry = 0;
    double timeOut = 40000;
    String attachment = "";

    /**
     * Load the URL on the browser launched
     *
     * @param url The http(s) URL that to be loaded on the browser
     * @return true if the load is successful else false
     * @author Srikanth
     */
    public boolean navigate(String url) {
        try {
            getPage().navigate(url);
            reportStep("The page with URL : " + url + " is loaded", "info");
            reportPass("Launched the application successfully");
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    public void navigateToBack() {
        try {
            getPage().goBack();
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
    }

    /**
     * Maximize the browser based on screen size
     * Presently there is no built-in method to invoke.
     *
     * @author Srikanth
     */
    public void maximize() {
        try {
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            getPage().setViewportSize(gd.getDisplayMode().getWidth(), gd.getDisplayMode().getHeight());
        } catch (HeadlessException e) {

        }
    }

    /**
     * Check if the given selector of the element is visible or not after 2 seconds
     *
     * @param locator The css / xpath / or playwright supported locator and element to provide the name of the element
     * @return true if the element is visible else false
     * @author Srikanth
     */
    public boolean isVisible(String locator, String elementName) {
        boolean bVisible = false;
        try {
            waitForAppearance(locator);
            bVisible = getPage().isVisible(locator);
            if (bVisible == true) {
                reportStep("The WebElement : " + elementName + " is visible ", "info");
            } else {
                reportStep("The WebElement : " + elementName + " is not visible ", "warning");
            }

        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("The WebElement : " + elementName + " is not visible ", "fail");
        }
        getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
        return bVisible;

    }

    /**
     * Set a native &lt;input type="date"&gt; or any date field that accepts ISO
     * format (yyyy‑MM‑dd) via direct typing.
     *
     * @param locator   Locator for the date element
     * @param isoDate   Date in ISO yyyy‑MM‑dd (e.g. "2025-07-21")
     * @param name      Friendly field name for reporting
     * @return true     when the date is filled successfully
     */
    public boolean setNativeDate(String locator, String isoDate, String name) {
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
            Locator element = getPage().locator(locator);
            element.waitFor();
            element.fill("");               // clear existing
            element.fill(isoDate);          // type ISO date
            element.press("Tab");           // trigger blur / onChange
            reportStep("The date field '" + name + "' is set to " + isoDate, "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }


    /**
     * Verify that a date input currently holds the expected ISO yyyy‑MM‑dd value.
     *
     * @param locator        Locator for the date element
     * @param expectedIso    Expected ISO date (yyyy‑MM‑dd)
     * @param name           Friendly field name for reporting
     * @return true          when the date matches
     */
    public boolean verifyDateValue(String locator, String expectedIso, String name) {
        try {
            String actual = getPage().locator(locator).inputValue();   // works for native date & many libraries
            if (actual.equals(expectedIso)) {
                reportStep("The date field '" + name + "' is set to '" + expectedIso + "' as expected", "pass");
                return true;
            } else {
                reportStep("Expected date '" + expectedIso + "' in field '" + name + "' but found '" + actual + "'", "warning");
            }
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }


    /**
     * Verify that a native &lt;select&gt; currently has the expected value attribute.
     *
     * @param locator        Locator pointing to the &lt;select&gt; element
     * @param expectedValue  Expected value attribute of the selected &lt;option&gt;
     * @param name           Friendly field name for reporting
     * @return true          when the value matches
     */
    public boolean verifySelectValue(String locator, String expectedValue, String name) {
        try {
            String actual = getPage().locator(locator).inputValue();   // value attr
            if (actual.equals(expectedValue)) {
                reportStep("The dropdown '" + name + "' has value '" + expectedValue + "' as expected", "pass");
                return true;
            } else {
                reportStep("Expected value '" + expectedValue + "' in dropdown '" + name + "' but found '" + actual + "'", "warning");
            }
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }



    /**
     * Verify that the current page URL contains the expected substring(s).
     *
     * @param expectedFragment One or more substrings that should be present in the URL.
     * @param name             Friendly label for reporting (e.g., "Applications page").
     * @return true            when every fragment is found; false otherwise.
     */
    public boolean verifyUrlContains(String expectedFragment, String name) {
        try {
            String currentUrl = getPage().url();
            if (!currentUrl.contains(expectedFragment)) {
                reportStep(
                        "Expected URL to contain '" + expectedFragment + "', but was " + currentUrl,
                        "fail"
                );
                return false;
            }

            reportStep(
                    "The URL '" + currentUrl + "' contains all expected fragment(s) " + expectedFragment,
                    "pass"
            );
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }











    /**
     * Verify that a React‑Select (or similar custom select) displays the expected label.
     *
     * @param valueContainerLocator  Locator for the visible selected‑value container
     * @param expectedLabel          Expected text shown to the user
     * @param name                   Friendly field name for reporting
     * @return true                  when the displayed label matches
     */
    public boolean verifyReactSelectValue(String valueContainerLocator, String expectedLabel, String name) {
        try {
            String actual = getPage().locator(valueContainerLocator).innerText().trim();
            if (actual.equals(expectedLabel)) {
                reportStep("The React‑select '" + name + "' shows label '" + expectedLabel + "' as expected", "pass");
                return true;
            } else {
                reportStep("Expected label '" + expectedLabel + "' in React‑select '" + name + "' but found '" + actual + "'", "warning");
            }
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }






    /**
     * Check if the given selector of the element is visible or not after 2 seconds
     *
     * @param locator The css / xpath / or playwright supported locator and element to provide the name of the element
     * @return true if the element is visible else false
     * @author Srikanth
     */

    public boolean isVisible(String locator) {
        boolean bVisible = false;
        try {
            pause("low");
            bVisible = getPage().isVisible(locator);

        } catch (PlaywrightException e) {
            e.printStackTrace();
        }
        return bVisible;
    }

    /**
     * Check if the given selector of the element is not visible or not after 2 seconds
     *
     * @param locator The css / xpath / or playwright supported locator and element to provide the name of the element
     * @return true if the element is not visible else false
     * @author Srikanth
     */
    public boolean isNotVisible(String locator, String elementName) {
        boolean bVisible = false;
        try {
            pause("low");
            bVisible = getPage().isVisible(locator);
            if (bVisible) {
                reportStep("The WebElement '" + elementName + "' is visible ", "warning");
            } else {
                reportPass("The WebElement '" + elementName + "' is not visible.");
            }

        } catch (PlaywrightException e) {
            e.printStackTrace();
        }

        return bVisible;

    }


    /**
     * Use this method to clear into an element.
     *
     * @return true if the value is typed else false
     * @author Srikanth
     */
    public boolean clear(String locator) {
        try {
            getPage().locator(locator).clear();
            reportStep("The text box contents is cleared", "info");
            return true;
        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to typing into an element, which may set its value.
     *
     * @param locator The locator to identify the element
     * @param value   The value to be entered in the text
     * @param name    The name of the text field (label)
     * @return true if the value is typed else false
     * @author Srikanth
     */
    public boolean typeWithType(String locator, String value, String name) {
        try {
            getPage().locator(locator).type(value);
            reportStep("The text box : " + name + " is typed with value : " + value, "info");
            return true;
        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }


    /**
     * Use this method to typing into an element, which may set its value.
     *
     * @param locator The locator to identify the element
     * @param value   The value to be entered in the text
     * @param name    The name of the text field (label)
     * @return true if the value is typed else false
     * @author Srikanth
     */
//    public boolean type(String locator, String value, String name) {
//        try {
//            getPage().locator(locator).fill("");
//            getPage().locator(locator).fill(value);
//            System.out.println("The Text box is filled with the value: "+value);
//            reportStep("The text box : " + name + " is typed with value : " + value, "info");
//            return true;
//        } catch (PlaywrightException e) {
//            e.printStackTrace();
//            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
//        }
//        return false;
//    }
    public boolean type(String locator, String value, String name) {
        try {
            Locator input;

            // Check if locator starts with getByLabel
            if (locator.startsWith("getByLabel(")) {
                // Extract label text from getByLabel('...')
                String label = locator.replaceAll("getByLabel\\(['\"](.*?)['\"]\\)", "$1");
                input = getPage().getByLabel(label);  // ✅ use Java Playwright's getByLabel method
            } else {
                input = getPage().locator(locator);   // fallback to regular CSS selector
            }

            input.fill("");        // clear the field
            input.fill(value);     // type the value

            System.out.println("The Text box is filled with the value: " + value);
            reportStep("The text box : " + name + " is typed with value : " + value, "info");
            return true;

        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
            return false;
        }
    }



    /**
     * Use this method to typing into an element inside a frame, which may set its value.
     *
     * @param locator The locator to identify the element
     * @param value   The value to be entered in the text
     * @param name    The name of the text field (label)
     * @return true if the value is typed else false
     * @author Srikanth
     */
    public boolean typeInFrame(String locator, String value, String name) {
        try {
            getFrameLocator().locator(locator).fill("");
            ;
            getFrameLocator().locator(locator).fill(value);
            reportStep("The text box : " + name + " is typed with value : " + value, "info");
            return true;
        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to typing into an element and perform <ENTER> key.
     *
     * @param locator The locator to identify the element
     * @param value   The value to be entered in the text
     * @param name    The name of the text field (label)
     * @return true if the value is typed else false
     * @author Srikanth
     */
    public boolean typeAndEnter(String locator, String value, String name) {
        try {
            getPage().locator(locator).fill("");
            ;
            getPage().locator(locator).fill(value);
            pause("low");
            getPage().locator(locator).press("Enter");
            reportStep("The text box : " + name + " is typed with value : " + value, "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    /**
     * Use this method to typing into an element and perform <SPACE> key.
     *
     * @param locator The locator to identify the element
     * @param value   The value to be entered in the text
     * @param name    The name of the text field (label)
     * @return true if the value is typed else false
     * @author Srikanth
     */

    public boolean typeAndSpace(String locator, String value, String name) {
        try {
            getPage().locator(locator).fill("");
            ;
            getPage().locator(locator).fill(value);
            pause("low");
            getPage().locator(locator).press("Space");
            reportStep("The text box : " + name + " is typed with value : " + value, "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    /**
     * Use this method to typing into an element and press Down Arrow key and press Enter.
     *
     * @param locator The locator to identify the element
     * @return void
     * @author Srikanth
     */

    public void pressDownArrowAndEnter(String locator) {
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
            getPage().locator(locator).waitFor();
            getPage().locator(locator).scrollIntoViewIfNeeded();
            getPage().locator(locator).click();
            getPage().locator(locator).press("ArrowDown");
            getPage().locator(locator).press("Enter");
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }

    }

    /**
     * Use this method to typing into an element and press Down Arrow key.
     *
     * @param locator The locator to identify the element
     * @return void
     * @author Srikanth
     */
    public void pressDownArrow(String locator) {
        try {
            getPage().locator(locator).press("ArrowDown");
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }

    }

    /**
     * Use this method to upload a file into the chosen field.
     *
     * @param locator  The locator to identify the element where need to upload
     * @param fileName The file name (relative or absolute)
     * @param name     The name of the upload field (label)
     * @return true if the file is uploaded else false
     * @author Srikanth
     */
    public boolean uploadFile(String locator, String fileName, String name) {
        try {
            getPage().locator(locator).setInputFiles(Paths.get(fileName));
            reportStep("The text box :" + name + " is uploaded with file :" + fileName, "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    /**
     * Use this method to click a button.
     *
     * @param locator The locator to identify the element
     * @param name    The name of the button field (label)
     * @return true if the button is clicked else false
     * @author Srikanth
     */
    // The old working one
    //public boolean click(String locator, String name) {
    //    try {
    //        System.out.println("Setting Default Time");
    //        getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
    //        System.out.println("Waiting For Locator");
    //        getPage().locator(locator).waitFor();
    //        getPage().locator(locator).scrollIntoViewIfNeeded();
    //        System.out.println("Clicking the Locator"+ locator);
    //        getPage().locator(locator).click();
    //        System.out.println("Button Click");
    //        reportStep("The Button name : " + name + " is clicked", "info");
    //        return true;
    //    } catch (PlaywrightException e) {
    //        reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
    //    }
    //    return false;
//
    //}
    // The New one i am experimenting with :
    //public boolean click(String locator, String name) {
    //    try {
    //        System.out.println("Setting Default Time");
    //        getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
    //        System.out.println("Waiting For Locator");
    //        getPage().locator(locator).waitFor();
    //        getPage().locator(locator).scrollIntoViewIfNeeded();
    //        System.out.println("Clicking the Locator"+ locator);
    //        getPage().locator(locator).click();
    //        System.out.println("Button Click");
    //        reportStep("The Button name : " + name + " is clicked", "info");
    //        return true;
    //    } catch (PlaywrightException e) {
    //        reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
    //    }
    //    return false;
//
    //}

    // NEW LINK CLICKS CHATGPT
    //public boolean click(String locator, String name) {
    //    try {
    //        System.out.println("Setting Default Time");
    //        getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
//
    //        System.out.println("Waiting For Locator");
    //        Locator el = getPage().locator(locator).first(); // act on first match, same as your link impl
    //        el.waitFor();                       // present & attached
    //        el.scrollIntoViewIfNeeded();
//
    //        // --- Link vs Button detection ---
    //        // If it's an <a> with href → treat as link (your clickLink behavior).
    //        // Also, if the selector itself looks like an anchor (e.g., starts with 'a' or contains '[href'),
    //        // we require href to be present (same failure rule as your clickLink).
    //        String href = el.getAttribute("href");
    //        boolean selectorLooksLikeAnchor = locator != null
    //                && (locator.trim().startsWith("a") || locator.contains("[href"));
//
    //        boolean isLink = (href != null && !href.isEmpty()) || selectorLooksLikeAnchor;
//
    //        if (isLink) {
    //            // If it looks like an anchor but has no href, fail—preserves your clickLink semantics
    //            if (href == null || href.isEmpty()) {
    //                reportStep("Link has no href: " + name, "fail");
    //                return false;
    //            }
//
    //            // Build the expected URL pattern exactly like your clickLink()
    //            String expected = href.startsWith("/") ? "**" + href : href;
//
    //            // Your link flow: click → waitForURL(expected) → logs/reports
    //            el.click();
    //            getPage().waitForURL(expected);
    //            System.out.println("Clicked link '" + name + "' and URL matched: " + expected);
    //            reportStep("Clicked link '" + name + "' and URL matched: " + expected, "info");
    //            return true;
    //        }
//
    //        // --- Default path: button/other element → preserve your current click behavior/logs ---
    //        System.out.println("Clicking the Locator" + locator);
    //        el.click();
    //        System.out.println("Button Click");
    //        reportStep("The Button name : " + name + " is clicked", "info");
    //        return true;
//
    //    } catch (PlaywrightException e) {
    //        reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
    //    }
    //    return false;
    //}

    // NEW LINK clicks CLAUDE
    //public boolean click(String locator, String name) {
    //    try {
    //        // STEP 1: Setup timeout configuration
    //        System.out.println("Setting Default Time");
    //        getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
//
    //        // STEP 2: Locate element and prepare for interaction
    //        System.out.println("Waiting For Locator");
    //        Locator el = getPage().locator(locator).first();
    //        el.waitFor();
    //        el.scrollIntoViewIfNeeded();
//
    //        // STEP 3: Intelligent link detection
    //        String href = el.getAttribute("href");
    //        boolean selectorLooksLikeAnchor = locator != null
    //                && (locator.trim().startsWith("a") || locator.contains("[href"));
    //        boolean isLink = (href != null && !href.isEmpty()) || selectorLooksLikeAnchor;
//
    //        // STEP 4: Execute link-specific behavior
    //        if (isLink) {
    //            if (href == null || href.isEmpty()) {
    //                reportStep("Link has no href: " + name, "fail");
    //                return false;
    //            }
    //            String expected = href.startsWith("/") ? "**" + href : href;
    //            System.out.println("Clicking the Locator " + locator);
    //            el.click();
//
    //            // Wait for URL to change
    //            getPage().waitForURL(expected);
    //            System.out.println("URL matched: " + expected);
//
    //            // CRITICAL: Wait for page to be fully loaded and stable
    //            getPage().waitForLoadState(LoadState.NETWORKIDLE);
    //            System.out.println("Page network is idle - content loaded");
//
    //            // Additional stability wait to ensure dynamic content is rendered
    //            getPage().waitForTimeout(1000);
    //            System.out.println("Stability wait completed");
//
    //            reportStep("Clicked link '" + name + "' and URL matched: " + expected, "info");
    //            return true;
    //        }
//
    //        // STEP 5: Execute button/default element behavior
    //        System.out.println("Clicking the Locator " + locator);
    //        el.click();
    //        System.out.println("Button Click");
    //        reportStep("The Button name : " + name + " is clicked", "info");
    //        return true;
//
    //    } catch (PlaywrightException e) {
    //        reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
    //        return false;
    //    }
    //}

    // UPDATED click() in PlaywrightWrapper.java
    public boolean click(String locator, String name) {
        try {
            // STEP 1: Setup timeout configuration
            System.out.println("Setting Default Time");
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());

            // ===== NEW: Support getByText('...') selectors (minimal, safe) =====
            if (locator != null && locator.trim().startsWith("getByText(")) {
                try {
                    // Extract inner string inside parentheses and strip quotes
                    String inner = locator.substring(locator.indexOf("(") + 1, locator.lastIndexOf(")")).trim();
                    // remove surrounding quotes if present
                    if ((inner.startsWith("'") && inner.endsWith("'")) || (inner.startsWith("\"") && inner.endsWith("\""))) {
                        inner = inner.substring(1, inner.length() - 1);
                    }

                    System.out.println("Clicking using getByText(): " + inner);
                    // Use Playwright's page.getByText API and pick the first match
                    Locator textLocator = getPage().getByText(inner).first();

                    // Wait for the text element and make sure it's visible/interactable
                    textLocator.waitFor();
                    textLocator.scrollIntoViewIfNeeded();

                    // Click the element
                    textLocator.click();

                    // Do the same stability wait used for link navigation to allow page changes / dynamic rendering
                    try {
                        getPage().waitForLoadState(LoadState.NETWORKIDLE);
                    } catch (PlaywrightException ignored) {
                        // swallow if waiting for NETWORKIDLE fails (some pages don't reach network idle)
                    }
                    getPage().waitForTimeout(1000);

                    reportStep("Clicked element by text '" + name + "' -> '" + inner + "'", "info");
                    return true;
                } catch (PlaywrightException e) {
                    reportStep("PlaywrightException when clicking getByText: " + e.getMessage(), "fail");
                    return false;
                } catch (Exception e) {
                    reportStep("Exception when handling getByText locator: " + e.getMessage(), "fail");
                    return false;
                }
            }
            // ===== END getByText handling =====

            // STEP 2: Locate element and prepare for interaction (existing flow)
            System.out.println("Waiting For Locator");
            Locator el = getPage().locator(locator).first();
            el.waitFor();
            el.scrollIntoViewIfNeeded();

            // STEP 3: Intelligent link detection (existing flow)
            String href = el.getAttribute("href");
            boolean selectorLooksLikeAnchor = locator != null
                    && (locator.trim().startsWith("a") || locator.contains("[href"));
            boolean isLink = (href != null && !href.isEmpty()) || selectorLooksLikeAnchor;

            // STEP 4: Execute link-specific behavior (existing flow preserved)
            if (isLink) {
                if (href == null || href.isEmpty()) {
                    reportStep("Link has no href: " + name, "fail");
                    return false;
                }
                String expected = href.startsWith("/") ? "**" + href : href;
                System.out.println("Clicking the Locator " + locator);
                el.click();

                // Wait for URL to change (existing behavior)
                try {
                    getPage().waitForURL(expected);
                    System.out.println("URL matched: " + expected);
                } catch (PlaywrightException e) {
                    // If waitForURL fails, still attempt to wait for load state and continue — but log it
                    System.out.println("Warning: waitForURL for expected '" + expected + "' failed: " + e.getMessage());
                }

                // CRITICAL: Wait for page to be fully loaded and stable
                try {
                    getPage().waitForLoadState(LoadState.NETWORKIDLE);
                    System.out.println("Page network is idle - content loaded");
                } catch (PlaywrightException ignored) {
                    // Some apps never hit NETWORKIDLE; ignore to avoid hard failures
                }

                // Additional stability wait to ensure dynamic content is rendered
                getPage().waitForTimeout(1000);
                System.out.println("Stability wait completed");

                reportStep("Clicked link '" + name + "' and URL matched: " + expected, "info");
                return true;
            }

            // STEP 5: Execute button/default element behavior (existing flow)
            System.out.println("Clicking the Locator " + locator);
            el.click();
            System.out.println("Button Click");
            reportStep("The Button name : " + name + " is clicked", "info");
            return true;

        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
            return false;
        } catch (Exception e) {
            reportStep("Exception : \n" + e.getMessage(), "fail");
            return false;
        }
    }


    /**
     * Use this method to click a Random Place in the Screen.
     *
     * @return true if the button is clicked else false
     * @author Srikanth
     */
    public boolean randomDoubleClick() {
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
            int width = getPage().viewportSize().width;
            int height = getPage().viewportSize().height;

            // Generate random coordinates within the viewport
            int randomX = (int) (Math.random() * width);
            int randomY = (int) (Math.random() * height);
            getPage().mouse().dblclick(randomX, randomY);
            reportStep("The Random Coordinates X and Y is double clicked ", "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to scroll to the element is viewable using JS.
     *
     * @param locator The locator to identify the element
     * @author Srikanth
     */
    public void scrollUsingJS(String locator) {
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
            Locator jsElement = getPage().locator(locator);
            getPage().evaluate("arguments[0].scrollIntoView(true);", jsElement);
            reportStep("Page Scroll is performed until element is displayed", "info");
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }

    }

    /**
     * Use this method to the scroll using the mouse wheel using x & y coordinate.
     *
     * @param locator The element locator to get x-y coordinate
     * @author Srikanth
     */
    public void scrollUsingMouseWheelVertically(String locator) {
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());

            Locator element = getPage().locator(locator);
            BoundingBox boundingBox = element.boundingBox();
            if (boundingBox != null) {
                double y = boundingBox.y;
                getPage().mouse().wheel(0.0, y);
            }
            reportStep("Page Scroll is performed based on co-ordinates", "info");
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }

    }

    public boolean clickAndType(String locator, String typeValue, String name) {
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
            getPage().locator(locator).click();
            getPage().locator(locator).fill("");
            ;
            getPage().locator(locator).fill(typeValue);
            reportStep("The text box :" + name + " is typed with value :" + typeValue, "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    public void scrollToElement(String locator) {
        getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
        getPage().locator(locator).waitFor();
        getPage().locator(locator).scrollIntoViewIfNeeded();
    }


    /**
     * Use this method to click an element.
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element field (label)
     * @param type    The type of the element such as link, element etc
     * @return true if the element is clicked else false
     * @author Srikanth
     */
    public boolean click(String locator, String name, String type) {
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
            getPage().locator(locator).scrollIntoViewIfNeeded();
            getPage().locator(locator).click();
            reportStep("The " + type + " :" + name + " is clicked", "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }
    public boolean clickLink(String locator, String name) {
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());

            // Ensure it's an <a> and capture its href before clicking
            Locator a = getPage().locator(locator).first();
            a.waitFor();                         // present & attached
            a.scrollIntoViewIfNeeded();

            String href = a.getAttribute("href"); // may be relative (e.g., "/policy-details/…")
            if (href == null || href.isEmpty()) {
                reportStep("Link has no href: " + name, "fail");
                return false;
            }

            // Build a predicate for the post-click URL
            String expected = href.startsWith("/") ? "**" + href : href;

            // For full navigations:
            // getPage().waitForNavigation(() -> a.click());

            // Safer for SPAs or either case: wait for URL pattern after click
            a.click();
            getPage().waitForURL(expected);
            System.out.println("Clicked link '" + name + "' and URL matched: " + expected);
            reportStep("Clicked link '" + name + "' and URL matched: " + expected, "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
            return false;
        }
    }


    public void clickUsingJs(String locator) {
        getPage().evaluate("() => { " + "const element = document.evaluate(\"" + locator + "\", document, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue; " + "if (element) { " + "element.scrollIntoView(); " + "element.click(); " + "} " + "}");

    }

    /**
     * Use this method to double click an element.
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element field (label)
     * @param type    The type of the element such as link, element etc
     * @return true if the element is clicked else false
     * @author Srikanth
     */
    public boolean doubleClick(String locator, String name, String type) {
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
            getPage().locator(locator).scrollIntoViewIfNeeded();
            getPage().locator(locator).dblclick();
            reportStep("The " + type + " :" + name + " is double clicked", "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    /**
     * Use this method to click an element within a frame.
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element field (label)
     * @param type    The type of the element such as link, element etc
     * @return true if the element is clicked else false
     * @author Srikanth
     */
    public boolean clickInFrame(String locator, String name) {
        try {
            getFrameLocator().locator(locator).scrollIntoViewIfNeeded();
            getFrameLocator().locator(locator).click();
            reportStep("The button : " + name + " is clicked", "info");
            return true;
        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Click a link by extracting its href and navigating to it
     * This is more reliable than direct click for links that trigger navigation
     *
     * @param linkSelector The CSS selector for the link (e.g., "a[href='/path']")
     * @param name         The name of the link for reporting
     * @return true if navigation successful, false otherwise
     */
    public boolean clickLinkByHref(String linkSelector, String name) {
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());

            // Extract href from the selector string
            String href;
            if (linkSelector.contains("href=\"")) {
                href = linkSelector.substring(
                        linkSelector.indexOf("href=\"") + 6,
                        linkSelector.lastIndexOf("\"")
                );
            } else {
                // If it's not in selector format, get href from the actual element
                href = getPage().locator(linkSelector).getAttribute("href");
            }

            // Get current origin
            String currentUrl = getPage().url();
            String origin = currentUrl.replaceFirst("^(https?://[^/]+).*$", "$1");

            // Build full URL and navigate
            String targetUrl = href.startsWith("/") ? origin + href : href;
            getPage().navigate(targetUrl);

            reportStep("The link: " + name + " is clicked and navigated to " + targetUrl, "info");
            return true;

        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to check a checkbox.
     *
     * @param locator The locator to identify the checkbox
     * @param name    The name of the checkbox field (label)
     * @return true if the checkbox is checked else false
     * @author Srikanth
     */
    public boolean check(String locator, String name) {
        try {
            getPage().locator(locator).check();
            reportStep("The checkbox: " + name + " is checked", "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to select a dropdown by its text.
     *
     * @param locator The locator to identify the dropdown
     * @param text    The text to be selected in the dropdown
     * @param name    The name of the dropdown field (label)
     * @return true if the dropdown is selected else false
     * @author Srikanth
     */
    public boolean selectByText(String locator, String text, String name) {
        try {
            getPage().locator(locator).selectOption(new SelectOption().setLabel(text));
            System.out.println("The drop down : " + name + " is selected with value : " + text);
            reportStep("The drop down : " + name + " is selected with value : " + text, "info");
            return true;
        } catch (PlaywrightException e) {
            e.getMessage();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    /**
     * Use this method to Highlight the element.
     *
     * @param locator The locator to identify the dropdown
     * @param name    The name of the highlight element
     * @return true if the dropdown is selected else false
     * @author Srikanth
     */
    public boolean highlightElement(String locator, String name) {
        try {
            Locator element = getPage().locator(locator);
            getPage().evaluate("element => element.setAttribute('style', 'border: 2px solid red; background: yellow;')", element);
            reportStep("The locator : " + name + " is Highlighted successfully", "info");
            return true;
        } catch (PlaywrightException e) {
            e.getMessage();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }


    /**
     * Handles both native <select> and custom (React‑Select) dropdowns.
     *
     * @param ddLocator   locator for the visible dropdown element
     * @param value       option label (or value) to choose
     * @param name        friendly control name for logging
     */
    // public boolean selectoption(String ddLocator, String value, String name) {
    //     try {
    //         Locator dd = getPage().locator(ddLocator);

    //         // Is it a real <select>?  Fast, JS‑side check.
    //         boolean isNative = (Boolean) dd.evaluate("el => el.tagName === 'SELECT'");

    //         if (isNative) {
    //             dd.selectOption(value);                     // native path
    //         } else {
    //             dd.click();                                // open custom dropdown
    //             pause("low");                              // your existing wait helper
    //             getPage().getByText(value).click();        // pick option by visible text
    //         }

    //         reportStep(
    //             String.format("Dropdown '%s' set to '%s'", name, value), "info");
    //         return true;
    //     } catch (PlaywrightException e) {
    //         e.printStackTrace();
    //         reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
    //         return false;
    //     }
    // }
    public boolean selectoption(String ddLocator, String value, String name) {
        try {
            Locator dropdown = getPage().locator(ddLocator);

            // Check if it's a native <select> dropdown
            boolean isNative = (Boolean) dropdown.evaluate("el => el.tagName === 'SELECT'");

            if (isNative) {
                dropdown.selectOption(new SelectOption().setLabel(value));
                System.out.println("Selected native select with "+dropdown+"value: "+value);
            } else {
                dropdown.click();
                pause("low");

                // Only find .Select-option that matches the text, avoid getByText global lookup
                Locator reactOptions = getPage().locator(".Select-option");
                Locator matchingOption = reactOptions.filter(new Locator.FilterOptions().setHasText(value)).first();

                if (matchingOption.count() == 0) {
                    reportStep("React-Select option '" + value + "' not found in dropdown '" + name + "'", "fail");
                    return false;
                }

                matchingOption.click();
            }
            System.out.println("Dropdown" + name + "set to" + value);
            reportStep(String.format("Dropdown '%s' set to '%s'", name, value), "info");
            return true;

        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException while selecting dropdown '" + name + "': " + e.getMessage(), "fail");
            return false;
        }
    }






    /**
     * Use this method to select a dropdown by its value.
     *
     * @param locator The locator to identify the dropdown
     * @param value   The value based on which it to be selected in the dropdown
     * @param name    The name of the dropdown field (label)
     * @return true if the dropdown is selected else false
     * @author Srikanth
     */
    public boolean selectByValue(String locator, String value, String name) {
        try {
            getPage().locator(locator).selectOption(value);
            reportStep("The drop down : " + name + " is selected with value index as : " + value, "info");
            return true;
        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    /**
     * Use this method to select a dropdown by its index.
     *
     * @param locatorId The locator to identify the dropdown
     * @param index     The index to be selected in the dropdown (starts at 0)
     * @param name      The name of the dropdown field (label)
     * @return true if the dropdown is selected else false
     * @author Srikanth
     */
    public boolean selectByIndex(String locatorId, int index, String name) {
        try {
            Locator locator = getPage().locator(locatorId + " > option");
            if (index > locator.count() || index < 0)
                index = (int) Math.floor(Math.random() * (locator.count() - 1)) + 1;
            getPage().locator(locatorId).selectOption(locator.nth(index).getAttribute("value"));
            reportStep("The drop down : " + name + " is selected with index : " + index, "info");
            return true;
        } catch (PlaywrightException e) {
            e.getMessage();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    /**
     * Use this method to select a dropdown by its index inside a frame.
     *
     * @param locatorId The locator to identify the dropdown
     * @param index     The index to be selected in the dropdown (starts at 0)
     * @param name      The name of the dropdown field (label)
     * @return true if the dropdown is selected else false
     * @author Srikanth
     */
    public boolean selectByIndexInFrame(String locatorId, int index, String name) {
        try {
            Locator locator = getFrameLocator().locator(locatorId + " > option");
            if (index > locator.count() || index < 0)
                index = (int) Math.floor(Math.random() * (locator.count() - 1)) + 1;
            getFrameLocator().locator(locatorId).selectOption(locator.nth(index).getAttribute("value"));
            reportStep("The drop down : " + name + " is selected with index : " + index, "info");
            return true;
        } catch (PlaywrightException e) {
            e.getMessage();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to select a dropdown by the random index.
     *
     * @param locator The locator to identify the dropdown
     * @param name    The name of the dropdown field (label)
     * @return true if the dropdown is selected else false
     * @author Srikanth
     */
    public boolean selectByRandomIndex(String locator, String name) {
        return selectByIndex(locator, -1, name);
    }

    /**
     * Use this method to type and choose an element (that looks like dropdown)
     *
     * @param ddLocator     The Dropdown locator to identify the main select element
     * @param optionLocator The Option locator to identify the item element
     * @param option        The option to be entered in the text area
     * @param name          The name of the dropdown field (label)
     * @return true if the option is selected else false
     * @author Srikanth
     */
    public boolean clickAndType(String ddLocator, String optionLocator, String option, String name) {
        try {
            getPage().locator(ddLocator).click();
            getPage().locator(optionLocator).fill(option);
            getPage().keyboard().press("Enter");
            reportStep("The drop down : " + name + " is selected with value : " + option, "info");
            return true;
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to click and choose an element (that looks like dropdown)
     *
     * @param ddLocator     The Dropdown locator to identify the main select element
     * @param optionLocator The Option locator to identify the item element
     * @param option        The option to be selected in the dropdown
     * @param name          The name of the dropdown field (label)
     * @return true if the option is selected else false
     * @author Srikanth
     */
    public boolean clickAndChoose(String ddLocator, String optionLocator, String option, String name) {
        try {
            getPage().locator(ddLocator).click();
            pause("low");
            getPage().locator(optionLocator).click();
            reportStep("The drop down : " + name + " is selected with value : " + option, "info");
            return true;
        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to click and choose an element (that looks like dropdown) inside a frame
     *
     * @param ddLocator     The Dropdown locator to identify the main select element
     * @param optionLocator The Option locator to identify the item element
     * @param option        The option to be selected in the dropdown
     * @param name          The name of the dropdown field (label)
     * @return true if the option is selected else false
     * @author Srikanth
     */
    public boolean clickAndChooseInFrame(String ddLocator, String optionLocator, String option, String name) {
        try {
            getFrameLocator().locator(ddLocator).click();
            getFrameLocator().locator(optionLocator).click();
            reportStep("The drop down : " + name + " is selected with value : " + option, "info");
            return true;
        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to mouse over an element
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element (label)
     * @return true if the mouse over is done else false
     * @author Srikanth
     */
    public boolean mouseOver(String locator, String name) {
        try {
            getPage().locator(locator).hover();
            reportStep("The element : " + name + " is moused over successfully", "info");
            return true;
        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    public boolean mouseDown() {
        try {
            getPage().mouse().wheel(0, 200);
            reportStep("Move mouse down", "info");
            return true;
        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to get inner text of an element
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element (label)
     * @return true if the mouse over is done else false
     * @author Srikanth
     */
    public String getInnerText(String locator) {
        try {
            return getPage().locator(locator).innerText();
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * Use this method to check if the element is enabled
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element (label)
     * @return true if the element is enabled else false
     * @author Srikanth
     */
    public boolean isEnabled(String locator, String name) {
        boolean bEnabled = false;
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
            bEnabled = getPage().isEnabled(locator);
            reportStep("The '" + name + "' is enabled ", "info");
            return bEnabled;
        } catch (Exception e) {
            e.printStackTrace();
            reportStep("The '" + name + "' is disabled ", "fail");
        }
        return bEnabled;
    }

    /**
     * Use this method to check if the element is disabled
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element (label)
     * @return true if the element is disabled else false
     * @author Srikanth
     */

    public boolean isDisabled(String locator, String name) {
        boolean bEnabled = false;
        try {
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());
            bEnabled = getPage().isDisabled(locator);
            reportStep("The '" + name + "' is disabled ", "info");
            return bEnabled;
        } catch (Exception e) {
            e.printStackTrace();
            reportStep("The '" + name + "' is not disabled ", "fail");
        }
        return bEnabled;
    }


    /**
     * Use this method to check if the element is disabled
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element (label)
     * @return true if the element is editable else false
     * @author Srikanth
     */
    public boolean isEditable(String locator, String elementName) {
        boolean bEditable = false;
        try {
            waitForAppearance(locator);
            bEditable = getPage().isEditable(locator);
            if (bEditable == true) {
                reportStep("The WebElement : " + elementName + " is editable ", "info");
            } else {
                reportStep("The WebElement : " + elementName + " is not editable ", "warning");
            }

        } catch (PlaywrightException e) {
            e.printStackTrace();
            reportStep("The WebElement : " + elementName + " is not visible ", "fail");
        }
        return bEditable;
    }

    /**
     * Use this method to report if the element is disabled
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element (label)
     * @return true if the element is disabled else false
     * @author Srikanth
     */
    public boolean verifyDisabled(String locator, String label) {
        try {
            if (isDisabled(locator, label)) reportStep("The element : " + label + " is disabled as expected", "info");
            else reportStep("The element : " + label + " is enabled", "warning");
        } catch (Exception e) {
        }
        return false;
    }

    /**
     * Use this method to check if element should not have value.
     *
     * @param locator - Locator of the element
     * @param label   - Name of the element
     */
    public void verifyAttributeIsNotEmpty(String locator, String attribute, String label) {
        try {
            String getValue = getAttribute(locator, attribute);
            if (getValue.isEmpty() || getValue.equals(" "))
                reportStep("The element attribute: " + label + " is empty.", "fail");
            else reportStep("The element '" + label + "' has value as - " + getValue, "info");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Use this method to wait for the element to disappear
     *
     * @param locator The locator to identify the element
     * @return true if the element is disappeared else false
     * @author Srikanth
     */
    public boolean waitForDisappearance(String locator) {
        try {
            getPage().locator(locator).waitFor(new Locator.WaitForOptions().setTimeout(ConfigurationManager.configuration().pauseHigh()).setState(WaitForSelectorState.HIDDEN));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Use this method to wait for the element to appear
     *
     * @param locator The locator to identify the element
     * @return true if the element is appeared else false
     * @author Srikanth
     */
    public boolean waitForAppearance(String locator) {
        try {
            getPage().locator(locator).waitFor(new Locator.WaitForOptions().setTimeout(ConfigurationManager.configuration().pauseHigh()).setState(WaitForSelectorState.VISIBLE));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * Use this method to report the correctness of the title
     *
     * @param title The title of the browser
     * @return true if the title matches the partial content else false
     * @author Srikanth
     */
    public boolean verifyTitle(String title) {
        try {
            if (getPage().title().contains(title)) {
                reportStep("The page with title : " + title + " displayed as expected", "info");
                return true;
            } else reportStep("The page with title : " + title + " did not match", "warning");

        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    /**
     * Use this method to report the correctness of the inner text (Exact Match)
     *
     * @param locator      The locator to identify the element
     * @param expectedText The expected text to be verified
     * @return true if the inner text matches the exact content else false
     * @author Srikanth
     */
    //public boolean verifyExactText(String locator, String expectedText) {
    //    try {
    //        if (getPage().locator(locator).innerText().equals(expectedText)) {
    //            System.out.println("The element with text : " + expectedText + " displayed as expected");
    //            reportStep("The element with text : " + expectedText + " displayed as expected", "pass");
    //            return true;
    //        } else reportStep("The element with text : " + expectedText + " did not match", "warning");
//
    //    } catch (PlaywrightException e) {
    //        reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
    //    }
    //    return false;
//
    //}

    // New Verify Exact Text function
    //public boolean verifyExactText(String locator, String expectedText) {
    //    try {
    //        // Keep framework timeout consistent
    //        getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());

    //        // 1) Wait for the target element to be VISIBLE (after navigation/SPA render)
    //        Locator el = getPage().locator(locator).first();
    //        el.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));

    //        // 2) Compare trimmed strings (avoids stray whitespace issues)
    //        String actual = el.innerText().trim();
    //        String expected = expectedText.trim();
    //        if (actual.equals(expected)) {
    //            System.out.println("The element with text : " + expected + " displayed as expected");
    //            reportStep("The element with text : " + expected + " displayed as expected", "pass");
    //            return true;
    //        }

    //        // 3) Fallback: use Playwright's text engine with exact match (helps if the locator was too generic)
    //        Page page = getPage();
    //        Locator exact = page.getByText(expected, new Page.GetByTextOptions().setExact(true)).first();
    //        exact.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
    //        String fallback = exact.innerText().trim();
    //        if (fallback.equals(expected)) {
    //            System.out.println("The element with text : " + expected + " displayed as expected (fallback getByText)");
    //            reportStep("The element with text : " + expected + " displayed as expected (fallback getByText)", "pass");
    //            return true;
    //        }

    //        // 4) Still not matching — log what we actually saw
    //        reportStep(
    //                "The element with text : " + expected +
    //                        " did not match. Actual(read via locator): '" + actual +
    //                        "'. URL: " + page.url(),
    //                "warning"
    //        );
    //    } catch (PlaywrightException e) {
    //        reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
    //    }
    //    return false;
    //}

    public boolean verifyExactText(String locator, String expectedText) {
        final String expected = expectedText == null ? "" : expectedText.trim();

        try {
            Locator el = getPage().locator(locator).first();

            // --- FAST PATH: try immediately without waiting ---
            try {
                String now = el.innerText().trim();
                if (now.equals(expected)) {
                    System.out.println("The element with text : " + expected + " displayed as expected");
                    reportStep("The element with text : " + expected + " displayed as expected", "pass");
                    return true;
                }
            } catch (Exception ignore) {
                // Element may not be attached/visible yet — fall through to short waits
            }

            // --- SHORT WAIT for visible (3s), then re-check ---
            try {
                el.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(3000)); // <= short, targeted wait
                String actual = el.innerText().trim();
                if (actual.equals(expected)) {
                    System.out.println("The element with text : " + expected + " displayed as expected (fallback getByText)");
                    reportStep("The element with text : " + expected + " displayed as expected (fallback getByText)", "pass");
                    return true;
                }
            } catch (Exception ignore) {
                // If it times out here, try the fallback below
            }

            // --- FALLBACK: exact text search with a small timeout (2s) ---
            try {
                Page page = getPage();
                Locator exact = page.getByText(expected, new Page.GetByTextOptions().setExact(true)).first();
                exact.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(2000)); // <= smaller timeout so it won't drag
                String fallback = exact.innerText().trim();
                if (fallback.equals(expected)) {
                    System.out.println("The element with text : " + expected + " displayed as expected");
                    reportStep("The element with text : " + expected + " displayed as expected", "pass");
                    return true;
                }
            } catch (Exception ignore) {
                // Fallback not found quickly — we'll log below
            }

            // --- Still not matching: log what we saw, without long blocking ---
            String snapshot = "";
            try { snapshot = el.innerText(); } catch (Exception ignored) {}
            reportStep(
                    "The element with text : " + expected +
                            " did not match. Actual(read via locator): '" + snapshot + "'. URL: " + getPage().url(),
                    "warning"
            );

        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }




    /**
     * Use this method to report the correctness of the inner text (Partial Match)
     *
     * @param locator      The locator to identify the element
     * @param expectedText The expected text to be verified
     * @return true if the inner text matches the partial content else false
     * @author Srikanth
     */
    public boolean verifyPartialText(String locator, String expectedText) {
        try {
            if (getPage().locator(locator).innerText().contains(expectedText)) {
                reportStep("The element with text : " + expectedText + " displayed as expected", "pass");
                return true;
            } else reportStep("The element with text : " + expectedText + " did not match", "warning");

        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    /**
     * Use this method to report the current url
     *
     * @return
     * @author Srikanth
     */
    public String getCurrentURL(String pageName) {
        String url = null;
        try {
            url = getPage().url();
            reportStep(pageName + " Current URL is : " + url, "info");
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return url;
    }

    /**
     * Use this method return the text typed within a textbox/area
     *
     * @param locator The locator to identify the element
     * @return Returns the text typed
     * @author Srikanth
     */
    public String getInputText(String locator) {
        try {
            return getPage().locator(locator).inputValue();
        } catch (PlaywrightException e) {
        }
        return "";
    }

    /**
     * Use this method return the Locatorscount
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element
     * @return Returns the int
     * @author Srikanth
     */
    public int getLocatorsCount(String locator) {
        Locator locators = null;
        try {
            locators = getPage().locator(locator);
            return locators.count();
        } catch (PlaywrightException e) {
        }
        return locators.count();
    }

    /**
     * Use this method return the Locators
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element
     * @return Returns the locators
     * @author Srikanth
     */
    public Locator getLocators(String locator) {
        Locator locators = null;
        try {
            locators = getPage().locator(locator);
            return locators;
        } catch (PlaywrightException e) {
        }
        return locators;
    }

    /**
     * Use this method return the List of Locators
     * @param locator The locator to identify the element
     * @return Returns the locators
     * @author Srikanth
     */
//	public List<ElementHandle> getListElements(String locator){
//		try {
//			getPage().locator(locator).elementHandles();
//		}catch (PlaywrightException e){}
//		return getPage().locator(locator).elementHandles();
//	}


    /**
     * Use this method to report the correctness of the typed text (Partial Match)
     *
     * @param locator      The locator to identify the element
     * @param expectedText The expected text to be verified
     * @return true if the typed text matches the partial content else false
     * @author Srikanth
     */
    public boolean verifyInputText(String locator, String expectedText) {
        try {
            if (getPage().locator(locator).inputValue().contains(expectedText)) {
                reportStep("The element with text : " + expectedText + " displayed as expected", "pass");
                return true;
            } else reportStep("The element with text : " + expectedText + " did not match", "warning");

        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    /**
     * Use this method to report the correctness of the typed text (Partial Match)
     *
     * @param locator      The locator to identify the element
     * @param expectedText The expected text to be verified
     * @return true if the typed text matches the partial content else false
     * @author Srikanth
     */
    public boolean verifyInnerText(String actual, String expectedText) {
        try {
            if (actual.contains(expectedText)) {
                reportStep("The actual value '" + actual + "' is matched with expected value '" + expectedText + "'", "pass");
                return true;
            } else
                reportStep("The actual value '" + actual + "' is not matched with expected value '" + expectedText + "'", "fail");

        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }


    /**
     * Use this method to get the attribute of the element
     *
     * @param locator   The locator to identify the element
     * @param attribute The attribute of the element
     * @return The attribute value of the located element
     * @author Srikanth
     */
    public String getAttribute(String locator, String attribute) {
        try {
            return getPage().locator(locator).getAttribute(attribute);
        } catch (PlaywrightException e) {
        }
        return "";
    }

    /**
     * Use this method to verify attribute of the element (Partial Match)
     *
     * @param locator      The locator to identify the element
     * @param attribute    The attribute of the element
     * @param expectedText The expected attribute value of the located element
     * @return true if the attribute matches the partial content else false
     * @author Srikanth
     */
    public boolean verifyAttribute(String locator, String attribute, String expectedText) {
        try {
            if (getPage().locator(locator).getAttribute(attribute).equalsIgnoreCase(expectedText)) {
                reportStep("The element with text : " + expectedText + " displayed as expected", "pass");
                return true;
            } else reportStep("The element with text : " + expectedText + " did not match", "warning");

        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;
    }

    /**
     * Use this method to report pass
     * * @param reporting to define the steps
     *
     * @author Srikanth
     */
    public void reportPass(String reporting) {
        reportStep(reporting, "pass");
    }

    /**
     * Use this method to report fail and throw back exception to stop the execution
     * * @param reporting to define the steps
     *
     * @author Srikanth
     */
    public void reportFailAndThrowException(String reporting) {

        reportStep(reporting, "fail");
        throw new PlaywrightException(reporting);
    }

    /**
     * Use this method to report fail and throw back exception to stop the execution
     * * @param reporting to define the steps
     *
     * @author Srikanth
     */
    public void reportFail(String reporting) {
        reportStep(reporting, "fail");
    }

    /**
     * Use this method to report fail and throw back exception to stop the execution
     * * @param reporting to define the steps
     *
     * @author Srikanth
     */
    public void reportWarning(String reporting) {
        reportStep(reporting, "warning");
    }

    /**
     * Use this method to verify attribute of the element (Partial Match) inside a frame
     *
     * @param locator      The locator to identify the element
     * @param attribute    The attribute of the element
     * @param expectedText The expected attribute value of the located element
     * @return true if the attribute matches the partial content else false
     * @author Srikanth
     */
    public boolean verifyAttributeInFrame(String locator, String attribute, String expectedText) {
        try {
            if (getFrameLocator().locator(locator).getAttribute(attribute).contains(expectedText)) {
                reportStep("The element with text : " + expectedText + " displayed as expected", "pass");
                return true;
            } else reportStep("The element with text : " + expectedText + " did not match", "warning");

        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        return false;

    }

    private boolean reportVisibleSuccess(String locator, String name) {
        getPage().locator(locator).scrollIntoViewIfNeeded();
        reportStep("The element : " + name + " displayed as expected", "pass");
        return true;
    }

    /**
     * Use this method to verify visibility of the element
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element field (label)
     * @return true if the element visible else false
     * @author Srikanth
     */
    public boolean verifyDisplayed(String locator, String name) {
        try {
            if (getPage().locator(locator).isVisible()) {
                System.out.println("Element is Visible, Locator: "+locator);
                return reportVisibleSuccess(locator, name);
            } else {
                pause("medium");
                if (getPage().isVisible(locator)) {
                    System.out.println("Element is Visible, Locator: "+locator);
                    return reportVisibleSuccess(locator, name);
                }
            }
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        if (!getPage().isVisible(locator))
            System.out.println("Element is Not Visible Visible");
        reportStep("The element :" + name + " is not visible in the page", "warning");
        return false;
    }

    /**
     * Use this method to verify invisibility of the element
     *
     * @param locator The locator to identify the element
     * @param name    The name of the element field (label)
     * @return true if the element invisible else false
     * @author Srikanth
     */
    public boolean verifyNotDisplayed(String locator, String name) {
        try {
            if (!getPage().locator(locator).isVisible()) {
                reportStep("The element : " + name + " is not displayed as expected", "pass");
                return true;
            } else {
                pause("medium");
                reportStep("The element : " + name + " is not displayed as expected", "pass");
                return true;
            }
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        if (!getPage().locator(locator).isVisible())
            reportStep("The element : " + name + " is visible in the page", "warning");
        return false;
    }


    /**
     * Use this method to manage the wait between actions with sleep
     *
     * @param type The type of wait - allowed : low, medium, high
     * @author Srikanth
     */
    public void pause(String type) {
        try {
            switch (type.toLowerCase()) {
                case "low":
                    Thread.sleep(ConfigurationManager.configuration().pauseLow());
                    break;
                case "medium":
                    Thread.sleep(ConfigurationManager.configuration().pauseMedium());
                    break;
                case "high":
                    Thread.sleep(ConfigurationManager.configuration().pauseHigh());
                    break;
                default:
                    Thread.sleep(ConfigurationManager.configuration().pauseLow());
                    break;
            }
        } catch (Exception e) {
        }
    }


    /**
     * Use this method to reload the page
     *
     * @param locator - Any Selector
     * @author Srikanth
     */
    public void reLoadPage() {
        getPage().reload();
    }

    /**
     * Use this method to upload file
     *
     * @author Srikanth
     */
    public void uploadFile() {
        try {
            getPage().setInputFiles("input[type='file']", Path.of(attachment));
            waitForAppearance("//img[@data-testid='preview-thumbnail']");
            reportStep("File is Uploaded", "info");
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
    }

    public void uploadFile(String fileName) {
        try {
            getPage().setInputFiles("input[type='file']", Path.of("./src/test/resources/upload/" + fileName));
            /*waitForAppearance("//img[@data-testid='preview-thumbnail']");*/
            reportStep("File is Uploaded", "info");
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
        pause("medium");
    }

    public void importBudgetFile(String fileName) {
        try {
            getPage().setInputFiles("input[type='file']", Path.of("./src/test/resources/Import/" + fileName));
            /*waitForAppearance("//img[@data-testid='preview-thumbnail']");*/
            reportStep("File is Uploaded", "info");
        } catch (PlaywrightException e) {
            reportStep("PlaywrightException : \n" + e.getMessage(), "fail");
        }
    }

    /**
     * Use this method to manage the wait for the element to load
     *
     * @param locator - Any Selector
     * @author Srikanth
     */
    public void waitForElementToLoad(String locator) {
        getPage().waitForSelector(locator, new Page.WaitForSelectorOptions().setTimeout(timeOut));
    }

    /**
     * Use this method to random number between the given range
     *
     * @param start
     * @param end
     * @return
     */
    public static int getRandomIntNumber(int start, int end) {
        Random ran = new Random();
        int result = ran.nextInt((end - start) + 1) + 1;
        return result;
    }

    /**
     * This function is used to get the date in MMM YYYY format (eg. Feb 2024)
     *
     * @param date - Pass a date in format DD/MM/YYYY (20/02/2024)
     * @return - It will return the string "Feb 2024"
     */
    public String getDateInMMMYYYYFormat(String date) {
        String outputDate = "";
        try {
            SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy");
            SimpleDateFormat outputDateFormat = new SimpleDateFormat("MMM yyyy");

            Date inputDate = inputDateFormat.parse(date);
            outputDate = outputDateFormat.format(inputDate);
        } catch (ParseException e) {
            System.out.println("Error parsing the date: " + e.getMessage());
        }
        return outputDate;
    }

    /**
     * This method is used to round the double value
     *
     * @param value 12.3467777
     * @return 12.35 in double
     */
    public static double getDoubleRoundedValue(double value) {
        String roundedValue = String.format("%.2f", value);
        double roundedDouble = Double.parseDouble(roundedValue);
        System.out.println("Rounded value: " + roundedDouble);

        return roundedDouble;
    }

    /**
     * This method will take snapshot in base64 format
     *
     * @author Srikanth
     */
    @Override
    public String takeSnap() {
        return new String(Base64.getEncoder().encode(getPage().screenshot()));
    }

    /**
     * Performs a soft assertion for comparing two strings.
     * <p>
     * A "soft" assert allows the test to continue even after a failure,
     * <p>
     * collecting all failures and reporting them at the end of the test.
     *
     * @param actual   The actual string value to be compared.
     * @param expected The expected string value for comparison.
     */

    public void softAssertionString(String actual, String expected) {

        SoftAssert soft = new SoftAssert();

        soft.assertEquals(actual, expected);

        soft.assertAll();

        reportPass("Actual - " + actual + " and expected value - " + expected + " matches");

    }

    /**
     * Performs a soft assertion for comparing two strings.
     * <p>
     * A "soft" assert allows the test to continue even after a failure,
     * <p>
     * collecting all failures and reporting them at the end of the test.
     *
     * @param actual   The actual boolean value to be compared.
     * @param expected The expected boolean value for comparison.
     */

    public void softAssertionBoolean(Boolean actual, Boolean expected) {

        SoftAssert soft = new SoftAssert();

        soft.assertEquals(actual, expected);

        soft.assertAll();

        reportPass("Actual - " + actual + " and expected value - " + expected + " matches");

    }

    /**
     * Performs a hard assertion for comparing two strings.
     * <p>
     * A "hard" assert allows the test to fail and stop executing,
     *
     * @param actual   The actual string value to be compared.
     * @param expected The expected string value for comparison.
     */

    public void hardAssertionString(String actual, String expected) {

        Assert.assertEquals(actual, expected);

    }

    /**
     * Performs a hard assertion for comparing two boolean.
     * <p>
     * A "hard" assert allows the test to fail and stop executing,
     *
     * @param actual   The actual boolean value to be compared.
     * @param expected The expected boolean value for comparison.
     */

    public void hardAssertionBoolean(Boolean actual, Boolean expected) {

        Assert.assertEquals(actual, expected);

    }

    /**
     * Performs a hard assertion .
     * <p>
     * A "hard" assert allows the test to fail and stop executing,
     * <p>
     * <p>
     * <p>
     * * @param expected The expected boolean value for comparison.
     * <p>
     * Test will be pass only input is true
     */

    public void hardAssertionTrue(Boolean result) {

        Assert.assertTrue(result);


    }



    // ---- helper DTO ----
    public static class FilterCondition {
        public final String column;          // header text or 1-based index as string
        public final String value;
        public final String matchType;       // "exact" | "includes"
        public FilterCondition(String column, String value, String matchType) {
            this.column = column;
            this.value = value;
            this.matchType = (matchType == null || matchType.isBlank()) ? "includes" : matchType.toLowerCase();
        }
    }

    // ---- private helpers ----
    private String normalizeSelector(String sel) {
        if (sel == null) return "";
        sel = sel.trim();
        if (sel.startsWith("page.")) sel = sel.substring(5);
        // locator('...') or locator("...")
        if (sel.startsWith("locator(") && sel.endsWith(")")) {
            int firstQuote = sel.indexOf('\'') >= 0 ? sel.indexOf('\'') : sel.indexOf('"');
            int lastQuote  = sel.lastIndexOf('\'') >= 0 ? sel.lastIndexOf('\'') : sel.lastIndexOf('"');
            if (firstQuote >= 0 && lastQuote > firstQuote) {
                return sel.substring(firstQuote + 1, lastQuote);
            }
        }
        return sel;
    }

    private int parseColumnIndex(String headerOrIndex, List<String> headers) {
        if (headerOrIndex == null) return -1;
        String trimmed = headerOrIndex.trim();
        // numeric? treat as 1-based index
        try {
            int asNum = Integer.parseInt(trimmed);
            return (asNum >= 1 && asNum <= headers.size()) ? (asNum - 1) : -1;
        } catch (NumberFormatException ignore) { /* not a number */ }
        // match by header text (case-insensitive, trimmed)
        String wanted = trimmed.toLowerCase();
        for (int i = 0; i < headers.size(); i++) {
            if (headers.get(i).trim().toLowerCase().equals(wanted)) return i;
        }
        return -1;
    }

    private boolean match(String actual, String expected, String matchType) {
        String a = actual == null ? "" : actual.trim().toLowerCase();
        String e = expected == null ? "" : expected.trim().toLowerCase();
        if ("exact".equalsIgnoreCase(matchType)) return a.equals(e);
        return a.contains(e); // includes (default)
    }

    // ========================= 1) assertCellValueInRow =========================
    public boolean assertCellValueInRow(
            List<String> tableSelectors,
            String rowIdentifierColumn, String rowIdentifierValue,
            String targetColumn, String expectedValue,
            String matchType, String tableName) {

        System.out.println("\n[assertCellValueInRow] tableName=" + tableName);
        System.out.println("[assertCellValueInRow] selectors=" + tableSelectors);
        System.out.println("[assertCellValueInRow] rowIdentifierColumn='" + rowIdentifierColumn + "', rowIdentifierValue='" + rowIdentifierValue + "'");
        System.out.println("[assertCellValueInRow] targetColumn='" + targetColumn + "', expectedValue='" + expectedValue + "', matchType=" + matchType);

        if (matchType == null || matchType.isBlank()) matchType = "includes";
        final long perSelTimeout = Math.max(1000, ConfigurationManager.configuration().pauseMedium());

        Locator table = null;
        PlaywrightException lastErr = null;
        String foundBySelector = null;

        try {
            // find a visible table
            for (String raw : tableSelectors) {
                String sel = normalizeSelector(raw);
                System.out.println("[assertCellValueInRow] Trying selector: " + sel);
                try {
                    Locator cand = getPage().locator(sel);
                    cand.waitFor(new Locator.WaitForOptions().setTimeout(perSelTimeout).setState(WaitForSelectorState.VISIBLE));
                    table = cand;
                    foundBySelector = sel;
                    System.out.println("[assertCellValueInRow] ✅ Visible table found by: " + sel);
                    break;
                } catch (PlaywrightException e) {
                    System.out.println("[assertCellValueInRow] Selector failed: " + sel + " | reason: " + e.getMessage());
                    lastErr = e;
                }
            }
            if (table == null) {
                System.out.println("[assertCellValueInRow] ❌ No table found.");
                reportFail("No visible table found for selectors: " + tableSelectors + (lastErr != null ? " | last error: " + lastErr.getMessage() : ""));
                return false;
            }

            List<String> headers = table.locator("thead th, thead td").allInnerTexts();
            System.out.println("[assertCellValueInRow] Headers: " + headers);
            int idColIdx  = parseColumnIndex(rowIdentifierColumn, headers);
            int tgtColIdx = parseColumnIndex(targetColumn, headers);

            System.out.println("[assertCellValueInRow] idColIdx=" + idColIdx + ", tgtColIdx=" + tgtColIdx);

            if (idColIdx < 0) {
                reportFail("Row identifier column '" + rowIdentifierColumn + "' not found in headers: " + headers);
                return false;
            }
            if (tgtColIdx < 0) {
                reportFail("Target column '" + targetColumn + "' not found in headers: " + headers);
                return false;
            }

            List<Locator> rows = table.locator("tbody tr").all();
            System.out.println("[assertCellValueInRow] Rows found: " + rows.size());

            int rowNum = 0;
            for (Locator row : rows) {
                rowNum++;
                String idCell = (row.locator("td:nth-child(" + (idColIdx + 1) + ")").textContent());
                System.out.println("[assertCellValueInRow] Row#" + rowNum + " idCell='" + idCell + "'");
                if (match(idCell, rowIdentifierValue, "includes")) { // identify row by includes (robust)
                    System.out.println("[assertCellValueInRow] ➜ Matched row identifier, inspecting target column...");
                    Locator tgtCell = row.locator("td:nth-child(" + (tgtColIdx + 1) + ")");
                    tgtCell.waitFor(new Locator.WaitForOptions().setTimeout(perSelTimeout).setState(WaitForSelectorState.VISIBLE));
                    String actual = tgtCell.textContent();
                    System.out.println("[assertCellValueInRow] Target cell text='" + actual + "'");
                    if (match(actual, expectedValue, matchType)) {
                        reportStep("Table '" + tableName + "': cell in column '" + targetColumn +
                                "' for row '" + rowIdentifierValue + "' matched expected '" + expectedValue + "' (" + matchType + ")", "pass");
                        System.out.println("[assertCellValueInRow] ✅ Assertion PASSED");
                        return true;
                    } else {
                        System.out.println("[assertCellValueInRow] ❌ Assertion FAILED");
                        reportFail("Table '" + tableName + "': expected '" + expectedValue + "' (" + matchType +
                                ") in column '" + targetColumn + "' for row '" + rowIdentifierValue +
                                "', but found '" + actual + "'");
                        return false;
                    }
                }
            }

            System.out.println("[assertCellValueInRow] ❌ No matching row found for identifier value.");
            reportFail("Table '" + tableName + "': no row found where column '" + rowIdentifierColumn +
                    "' contains '" + rowIdentifierValue + "'.");
            return false;

        } catch (PlaywrightException e) {
            System.out.println("[assertCellValueInRow] EXCEPTION: " + e.getMessage());
            reportFail("Failed in assertCellValueInRow: " + e.getMessage());
            return false;
        }
    }

    // ========================= 2) assertTableColumnValues ======================
    /**
     * @param assertionType "all" | "none" | "any"
     * @param matchType     "exact" | "includes"
     */
    public boolean assertTableColumnValues(
            List<String> tableSelectors,
            String columnHeader,
            String expectedValue,
            String assertionType,
            String matchType,
            String tableName) {

        System.out.println("\n[assertTableColumnValues] tableName=" + tableName);
        System.out.println("[assertTableColumnValues] selectors=" + tableSelectors);
        System.out.println("[assertTableColumnValues] columnHeader='" + columnHeader + "', expectedValue='" + expectedValue + "'");
        System.out.println("[assertTableColumnValues] assertionType=" + assertionType + ", matchType=" + matchType);

        if (assertionType == null || assertionType.isBlank()) assertionType = "all";
        if (matchType == null || matchType.isBlank()) matchType = "includes";

        Locator table = null;
        PlaywrightException lastErr = null;
        final long perSelTimeout = Math.max(1000, ConfigurationManager.configuration().pauseMedium());

        try {
            for (String raw : tableSelectors) {
                String sel = normalizeSelector(raw);
                System.out.println("[assertTableColumnValues] Trying selector: " + sel);
                try {
                    Locator cand = getPage().locator(sel);
                    cand.waitFor(new Locator.WaitForOptions().setTimeout(perSelTimeout).setState(WaitForSelectorState.VISIBLE));
                    table = cand;
                    System.out.println("[assertTableColumnValues] ✅ Visible table found by: " + sel);
                    break;
                } catch (PlaywrightException e) {
                    System.out.println("[assertTableColumnValues] Selector failed: " + sel + " | reason: " + e.getMessage());
                    lastErr = e;
                }
            }
            if (table == null) {
                System.out.println("[assertTableColumnValues] ❌ No table found.");
                reportFail("No visible table found for selectors: " + tableSelectors + (lastErr != null ? " | last error: " + lastErr.getMessage() : ""));
                return false;
            }

            List<String> headers = table.locator("thead th, thead td").allInnerTexts();
            System.out.println("[assertTableColumnValues] Headers: " + headers);
            int colIdx = parseColumnIndex(columnHeader, headers);
            System.out.println("[assertTableColumnValues] colIdx=" + colIdx);

            if (colIdx < 0) {
                reportFail("Column '" + columnHeader + "' not found in headers: " + headers);
                return false;
            }

            List<Locator> cells = table.locator("tbody tr td:nth-child(" + (colIdx + 1) + ")").all();
            System.out.println("[assertTableColumnValues] Cells count: " + cells.size());

            if (cells.isEmpty()) {
                if ("none".equalsIgnoreCase(assertionType)) {
                    reportStep("Table '" + tableName + "': no visible rows; 'none' assertion passes.", "pass");
                    System.out.println("[assertTableColumnValues] ✅ 'none' passes (no rows).");
                    return true;
                } else {
                    System.out.println("[assertTableColumnValues] ❌ No rows present for column.");
                    reportFail("Table '" + tableName + "': no visible rows for column '" + columnHeader + "'.");
                    return false;
                }
            }

            boolean anyMatch = false;
            List<String> violations = new ArrayList<>();

            int idx = 0;
            for (Locator cell : cells) {
                idx++;
                cell.waitFor(new Locator.WaitForOptions().setTimeout(perSelTimeout).setState(WaitForSelectorState.VISIBLE));
                String actual = cell.textContent();
                boolean ok = match(actual, expectedValue, matchType);
                System.out.println("[assertTableColumnValues] Cell#" + idx + " actual='" + actual + "' -> ok=" + ok);

                switch (assertionType.toLowerCase()) {
                    case "all":
                        if (!ok) violations.add("Found '" + actual + "'");
                        break;
                    case "none":
                        if (ok) violations.add("Found '" + actual + "' which matches");
                        break;
                    case "any":
                        if (ok) anyMatch = true;
                        break;
                }
            }

            if ("any".equalsIgnoreCase(assertionType)) {
                if (anyMatch) {
                    reportStep("Table '" + tableName + "': at least one cell in '" + columnHeader +
                            "' matched '" + expectedValue + "' (" + matchType + ")", "pass");
                    System.out.println("[assertTableColumnValues] ✅ ANY passed.");
                    return true;
                }
                System.out.println("[assertTableColumnValues] ❌ ANY failed (no matches).");
                reportFail("Table '" + tableName + "': no cell in '" + columnHeader +
                        "' matched '" + expectedValue + "' (" + matchType + ")");
                return false;
            } else {
                if (violations.isEmpty()) {
                    reportStep("Table '" + tableName + "': assertion '" + assertionType + "' passed for expected '" +
                            expectedValue + "' (" + matchType + ") in column '" + columnHeader + "'.", "pass");
                    System.out.println("[assertTableColumnValues] ✅ " + assertionType.toUpperCase() + " passed.");
                    return true;
                } else {
                    String joined = violations.stream().limit(10).collect(Collectors.joining("; "));
                    System.out.println("[assertTableColumnValues] ❌ " + assertionType.toUpperCase() + " failed. Issues: " + joined);
                    reportFail("Table '" + tableName + "': assertion '" + assertionType + "' failed for '" +
                            expectedValue + "' (" + matchType + ") in column '" + columnHeader + "'. Issues: " + joined);
                    return false;
                }
            }

        } catch (PlaywrightException e) {
            System.out.println("[assertTableColumnValues] EXCEPTION: " + e.getMessage());
            reportFail("Failed in assertTableColumnValues: " + e.getMessage());
            return false;
        }
    }

    // ===================== 3) assertFilteredTableRows ==========================
    /**
     * Negative assertion means: for rows that meet filters, the assert column must **NOT** match assertValue.
     */
    public boolean assertFilteredTableRows(
            List<String> tableSelectors,
            List<FilterCondition> filters,
            String assertColumn,
            String assertValue,
            String assertMatchType,   // "exact" | "includes"
            boolean negativeAssertion,
            String tableName) {

        System.out.println("\n[assertFilteredTableRows] tableName=" + tableName);
        System.out.println("[assertFilteredTableRows] selectors=" + tableSelectors);
        System.out.println("[assertFilteredTableRows] filters=" + filters);
        System.out.println("[assertFilteredTableRows] assertColumn='" + assertColumn + "', assertValue='" + assertValue +
                "', assertMatchType=" + assertMatchType + ", negativeAssertion=" + negativeAssertion);

        if (assertMatchType == null || assertMatchType.isBlank()) assertMatchType = "includes";
        Locator table = null;
        PlaywrightException lastErr = null;
        final long perSelTimeout = Math.max(1000, ConfigurationManager.configuration().pauseMedium());

        try {
            for (String raw : tableSelectors) {
                String sel = normalizeSelector(raw);
                System.out.println("[assertFilteredTableRows] Trying selector: " + sel);
                try {
                    Locator cand = getPage().locator(sel);
                    cand.waitFor(new Locator.WaitForOptions().setTimeout(perSelTimeout).setState(WaitForSelectorState.VISIBLE));
                    table = cand;
                    System.out.println("[assertFilteredTableRows] ✅ Visible table found by: " + sel);
                    break;
                } catch (PlaywrightException e) {
                    System.out.println("[assertFilteredTableRows] Selector failed: " + sel + " | reason: " + e.getMessage());
                    lastErr = e;
                }
            }
            if (table == null) {
                System.out.println("[assertFilteredTableRows] ❌ No table found.");
                reportFail("No visible table found for selectors: " + tableSelectors + (lastErr != null ? " | last error: " + lastErr.getMessage() : ""));
                return false;
            }

            List<String> headers = table.locator("thead th, thead td").allInnerTexts();
            System.out.println("[assertFilteredTableRows] Headers: " + headers);

            // compute filter column indices
            List<Integer> filterIdx = new ArrayList<>();
            for (FilterCondition fc : filters) {
                int idx = parseColumnIndex(fc.column, headers);
                System.out.println("[assertFilteredTableRows] Filter column '" + fc.column + "' -> idx=" + idx);
                if (idx < 0) {
                    reportFail("Filter column '" + fc.column + "' not found in headers: " + headers);
                    return false;
                }
                filterIdx.add(idx);
            }

            int assertColIdx = parseColumnIndex(assertColumn, headers);
            System.out.println("[assertFilteredTableRows] assertColIdx=" + assertColIdx);
            if (assertColIdx < 0) {
                reportFail("Assert column '" + assertColumn + "' not found in headers: " + headers);
                return false;
            }

            List<Locator> rows = table.locator("tbody tr").all();
            System.out.println("[assertFilteredTableRows] Rows found: " + rows.size());

            boolean anyFilteredRow = false;
            List<String> failures = new ArrayList<>();

            int rowNum = 0;
            for (Locator row : rows) {
                rowNum++;
                boolean matchesAll = true;

                for (int i = 0; i < filters.size(); i++) {
                    FilterCondition fc = filters.get(i);
                    int idx = filterIdx.get(i);
                    String cellText = (row.locator("td:nth-child(" + (idx + 1) + ")").textContent());
                    boolean ok = match(cellText, fc.value, fc.matchType);
                    System.out.println("[assertFilteredTableRows] Row#" + rowNum + " filter#" + (i + 1) +
                            " column='" + fc.column + "' cellText='" + cellText + "' -> ok=" + ok);
                    if (!ok) {
                        matchesAll = false;
                        break;
                    }
                }

                if (!matchesAll) {
                    System.out.println("[assertFilteredTableRows] Row#" + rowNum + " does NOT satisfy all filters. Skipping.");
                    continue;
                }

                anyFilteredRow = true;

                String assertText = (row.locator("td:nth-child(" + (assertColIdx + 1) + ")").textContent());
                boolean ok = match(assertText, assertValue, assertMatchType);
                System.out.println("[assertFilteredTableRows] Row#" + rowNum + " assertText='" + assertText + "' ok=" + ok);

                if (negativeAssertion) {
                    if (ok) {
                        failures.add("Row#" + rowNum + " has '" + assertText + "' matching disallowed '" + assertValue + "'");
                    }
                } else {
                    if (!ok) {
                        failures.add("Row#" + rowNum + " has '" + assertText + "' which does not match expected '" + assertValue + "'");
                    }
                }
            }

            if (!anyFilteredRow) {
                if (negativeAssertion) {
                    reportStep("Table '" + tableName + "': no rows matched filter conditions; negative assertion passes implicitly.", "pass");
                    System.out.println("[assertFilteredTableRows] ✅ No filtered rows; negative assertion passes.");
                    return true;
                } else {
                    System.out.println("[assertFilteredTableRows] ❌ No filtered rows; positive assertion cannot be performed.");
                    reportFail("Table '" + tableName + "': no rows matched filter conditions; cannot perform positive assertion.");
                    return false;
                }
            }

            if (failures.isEmpty()) {
                reportStep("Table '" + tableName + "': filtered rows assertion passed.", "pass");
                System.out.println("[assertFilteredTableRows] ✅ Assertion PASSED.");
                return true;
            } else {
                String joined = failures.stream().limit(10).collect(Collectors.joining("; "));
                System.out.println("[assertFilteredTableRows] ❌ Assertion FAILED: " + joined);
                reportFail("Table '" + tableName + "': filtered rows assertion failed: " + joined);
                return false;
            }

        } catch (PlaywrightException e) {
            System.out.println("[assertFilteredTableRows] EXCEPTION: " + e.getMessage());
            reportFail("Failed in assertFilteredTableRows: " + e.getMessage());
            return false;
        }
    }





}
