package com.ui.utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.aventstack.extentreports.reporter.configuration.ViewName;
import com.config.ConfigurationManager;
import com.ui.base.DriverFactory;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;


public abstract class Reporter extends DriverFactory {

    private static ExtentSparkReporter oSpark;
    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> parentTest = new ThreadLocal<ExtentTest>();
    private static final ThreadLocal<ExtentTest> test = new ThreadLocal<ExtentTest>();
    protected static ThreadLocal<String> testName = new ThreadLocal<String>();


    private String fileName = "result.html";
    private static String pattern = "dd-MMM-yyyy HH-mm-ss";

    public String testcaseName, testDescription, authors, category, dataFileType, dataFileName;

    public static String folderName = "";

    public static String createFolder(String folderName) {
        String date = new SimpleDateFormat(pattern).format(new Date());
        folderName = folderName + "/" + date;

        File folder = new File("./" + folderName);
        if (!folder.exists()) {
            folder.mkdir();
        }
        return folderName;
    }

    public synchronized void startReport() {
    	folderName = createFolder("reports");
		List<ViewName> viewList = new ArrayList<ViewName>();
		viewList.add(ViewName.DASHBOARD);
		viewList.add(ViewName.TEST);
		viewList.add(ViewName.AUTHOR);
		viewList.add(ViewName.CATEGORY);
		//ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("./" + folderName + "/" + fileName);
		oSpark = new ExtentSparkReporter("./" + folderName + "/" + fileName).viewConfigurer().viewOrder().as(viewList).apply();
//		oSpark.config().setTestViewChartLocation(ChartLocation.BOTTOM);
//		oSpark.config().setChartVisibilityOnOpen(!true);
		oSpark.config().setTheme(Theme.STANDARD);
		if(ConfigurationManager.configuration().reportTheme().equalsIgnoreCase("dark")) {
			oSpark.config().setTheme(Theme.DARK);
		}
		oSpark.config().setDocumentTitle(ConfigurationManager.configuration().reportTitle());
		oSpark.config().setEncoding("utf-8");
		oSpark.config().setReportName(ConfigurationManager.configuration().reportName());
		extent = new ExtentReports();
		extent.attachReporter(oSpark);
    }

    public synchronized void startTestCase() {
        ExtentTest parent = extent.createTest(testcaseName, testDescription);
        parent.assignCategory(category);
        // parent.assignAuthor(authors);
        parentTest.set(parent);
        testName.set(testcaseName);
    }

    public synchronized void setNode() {
        ExtentTest child = parentTest.get().createNode(getTestName());
        test.set(child);
    }

    public abstract String takeSnap();

    public void reportStep(String desc, String status, boolean bSnap) {
    	synchronized (test) {

            // Define the custom CSS styles
            String style =  "style='white-space: normal !important; text-align: left !important; display: block !important;'";

            // Start reporting the step and snapshot
			if (bSnap && !(status.equalsIgnoreCase("INFO") || status.equalsIgnoreCase("skipped"))) {
				 MediaEntityBuilder.createScreenCaptureFromBase64String(takeSnap()).build();
			}

            // Wrap the description in a styled span element
            String styledDesc = "<span " + style + ">" + desc + "</span>";

			if (status.equalsIgnoreCase("pass")) {
				test.get().pass(MarkupHelper.createLabel(styledDesc, ExtentColor.GREEN));
			} else if (status.equalsIgnoreCase("fail")) { 
				test.get().fail(MarkupHelper.createLabel(styledDesc, ExtentColor.RED));
				test.get().fail(styledDesc,MediaEntityBuilder.createScreenCaptureFromBase64String(takeSnap()).build());
			} else if (status.equalsIgnoreCase("warning")) {
				test.get().warning(MarkupHelper.createLabel(styledDesc, ExtentColor.YELLOW));
				test.get().warning(styledDesc, MediaEntityBuilder.createScreenCaptureFromBase64String(takeSnap()).build());
			} else if (status.equalsIgnoreCase("skipped")) {
				test.get().skip("The test is skipped due to dependency failure");
			} else if (status.equalsIgnoreCase("INFO")) {
				test.get().info(MarkupHelper.createLabel(styledDesc, ExtentColor.INDIGO));
			}
		}
    }

    public void reportStep(String desc, String status) {
        reportStep(desc, status, true);
    }



    public synchronized void endResult() {
        extent.flush();
    }


    public String getTestName() {
        return testName.get();
    }

    public Status getTestStatus() {
        return parentTest.get().getModel().getStatus();
    }


}
