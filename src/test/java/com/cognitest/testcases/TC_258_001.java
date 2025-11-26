package com.cognitest.testcases;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import com.listener.TestListener;
import com.page.base.MenuPage;
import com.page.base.CognitestPageHook;
import java.io.FileReader;
import com.google.gson.JsonParser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Listeners(TestListener.class)
public class TC_258_001 extends CognitestPageHook {

    @BeforeTest
    public void setReportValues() {
        testcaseName = "TC-258-001 User successfully logs in and performs a search with multiple fields populated.";
        testDescription = "User successfully logs in and performs a search with multiple fields populated.";
        authors = "Cognitest";
        category = "MCP Automation";
    }

    @DataProvider(name = "testData")
    public Object[][] getTestData() {
        String testDataPath = "src/test/java/com/cognitest/testcases/TestData/testData_TC-258-001.json"; 
        List<Object[]> data = new ArrayList<>();
        try (FileReader reader = new FileReader(testDataPath)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            JsonArray tcDataArray = jsonObject.getAsJsonArray("TestData");
            
            Map<String, String> currentRunData = new HashMap<>();

            for (int i = 0; i < tcDataArray.size(); i++) {
                JsonObject item = tcDataArray.get(i).getAsJsonObject();
                String fieldName = item.get("fieldName").getAsString();
                String value = item.get("value").getAsString();
                currentRunData.put(fieldName, value);
            }
            data.add(new Object[]{currentRunData}); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data.toArray(new Object[0][0]);
    }

    @Test(dataProvider = "testData")
    public void dynamicTestMethodName(Map<String, String> testData) throws InterruptedException {
        new MenuPage()
            .navigateToHomePage()
            .verifyTickingMindsText()
            .fillEmail(testData.get("EMAIL"))
            .verifyEmail(testData.get("EMAIL"))
            .fillPassword(testData.get("PASSWORD"))
            .verifyPassword(testData.get("PASSWORD"))
            .clickLoginButton()
            .verifyInsuranceSearchText()
            .fillPolicyNumber(testData.get("POLICY_NUMBER"))
            .verifyPolicyNumber(testData.get("POLICY_NUMBER"))
            .fillLoanNumber(testData.get("LOAN_NUMBER"))
            .verifyLoanNumber(testData.get("LOAN_NUMBER"))
            .clickSearchButton()
            .verifySearchResultsText()
            .clickTransactionLink(testData.get("POLICY_NUMBER"))
            .verifyPolicyInformationText();
        
        System.out.println("ATS created successfully for " + testcaseName);
    }
} // chnage made here 