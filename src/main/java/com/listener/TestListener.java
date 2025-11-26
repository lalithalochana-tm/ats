package com.listener;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.testng.ITestListener;
import org.testng.ITestResult;
 
import com.ui.base.PlaywrightWrapper;

public class TestListener extends PlaywrightWrapper implements ITestListener {
	
	public void  onTestFailure(ITestResult result) {
		if(result.getThrowable() != null) {
			System.out.println("ITest Listener Execution");
			StringWriter writer = new StringWriter(); 
			PrintWriter printWriter = new PrintWriter(writer);
			result.getThrowable().printStackTrace(printWriter);
			System.out.println(writer.toString());
			reportFail(writer.toString());
		} 
	  }

}