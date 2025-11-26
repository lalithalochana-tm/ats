/*
 * MIT License
 *
 * Copyright (c) 2022 Mathan
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

import java.nio.file.Paths;
import java.util.List;

import org.testng.annotations.*;

import com.config.ConfigurationManager;
import com.microsoft.playwright.Tracing;
import com.microsoft.playwright.Video;
import com.ui.utilities.XMLReaderUtil;


public class ProjectHooks extends PlaywrightWrapper {

    // This is for the email that you may need to use within test automation
    protected static final ThreadLocal<String> email = new ThreadLocal<String>();
    public static int count = 0;
    //public static Map<String,String> newEmails;
    public static String videoFolderName = "videos/";
    public static String tracesFolderName = "videos/";
    public static List<String> groupNames = XMLReaderUtil.readXMLAttributeValue("include");
	public static String xmlUserLevel = groupNames.get(count);


    /**
     * Will be invoked before once for every test suite execution and create video folder and the reporting
     *
     * @author Srikanth
     */
    @BeforeSuite(groups = {"Tier1Admin", "Tier1User", "Tier2Admin", "Tier2User", "Tier3EndUser"})
    public void initSuite() {
        videoFolderName = createFolder("videos");
        tracesFolderName = createFolder("traces");
        startReport();
        xmlUserLevel = groupNames.get(count);
    	count++;
    }


    //    @BeforeClass(alwaysRun = true)
    public void beginTestcaseReporting() {
        startTestCase();
    }

    /**
     * Will be invoked after once for every test case execution and
     * a) video & tracing will be created in the given folder
     * b) result will be published
     *
     * @author Srikanth
     */
    @AfterClass(alwaysRun = true, groups = {"Tier1Admin", "Tier1User", "Tier2Admin", "Tier2User", "Tier3EndUser"})
    public void tearDown() {
        try {
            System.out.println("tear down start");
// End tracing
            if (!ConfigurationManager.configuration().browser().equalsIgnoreCase("cloud")) {
                getContext().tracing().stop(new Tracing.StopOptions().setPath(Paths.get(tracesFolderName + "/" + testcaseName + ".zip")));
                Video video = getPage().video();
                getPage().close();
                video.saveAs(Paths.get(videoFolderName + "/" + testcaseName + ".webm"));
                getContext().close(); // video will be saved
                video.delete();
                getPlaywright().close();
            } else {
                //endResult();
                getPage().close();
                //video.saveAs(Paths.get(videoFolderName+"/"+testcaseName+".webm"));
                getContext().close(); // video will be saved
                //video.delete();
                getPlaywright().close();
            }
        } catch (Exception e) {
            //endResult();
            e.printStackTrace();
        }
    }

    @AfterSuite(alwaysRun = true, groups = {"Tier1Admin", "Tier1User", "Tier2Admin", "Tier2User", "Tier3EndUser"})
    public void endReport() {
        try {
            endResult();
            System.out.println("After Suite");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            endResult();
            System.out.println("After Suite");
        }
    }


//    @DataProvider(name = "TestData")
//    public Object[][] getData() {
//        return ExcelReader.readExcelData(dataFileName);
//    }


}
