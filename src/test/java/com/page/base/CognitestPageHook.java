package com.page.base;

import java.nio.file.Paths;

import org.testng.annotations.*;

import com.config.ConfigurationManager;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.Browser.NewContextOptions;
import com.microsoft.playwright.Tracing;
import com.ui.base.ProjectHooks;

public class CognitestPageHook extends ProjectHooks {

    public static String userLevel = "";
    public static String password = "";
    public static final ThreadLocal<String> clientCompanyName = new ThreadLocal<>();
    public static final ThreadLocal<String> userFirstName = new ThreadLocal<>();
    public static final ThreadLocal<String> userLastName = new ThreadLocal<>();
    public static final ThreadLocal<String> userMailId = new ThreadLocal<>();
    public static final ThreadLocal<String> userMailIdText = new ThreadLocal<>();
    public static final ThreadLocal<String> ruleName = new ThreadLocal<>();
    public static final ThreadLocal<String> newListName = new ThreadLocal<>();
    public static final ThreadLocal<String> analysisName = new ThreadLocal<>();
    public static final ThreadLocal<String> reportName = new ThreadLocal<>();
    public static final ThreadLocal<String> recipientEmail = new ThreadLocal<>();
    public static final ThreadLocal<String> titleone = new ThreadLocal<>();
    public static final ThreadLocal<String> descriptionOne = new ThreadLocal<>();

    public static final ThreadLocal<String> titleTwo = new ThreadLocal<>();
    public static final ThreadLocal<String> descriptionTwo = new ThreadLocal<>();
    public static final ThreadLocal<String> fileName = new ThreadLocal<>();


    public static void setUserLevel() {

        switch (xmlUserLevel) {
            case "Tier1Admin":
                userLevel = ConfigurationManager.configuration().appUserNameTier1Admin();
                password = ConfigurationManager.configuration().appPasswordTier1Admin();
                break;

            case "Tier1User":
                userLevel = ConfigurationManager.configuration().appUserNameTier1User();
                password = ConfigurationManager.configuration().appPasswordTier1User();
                break;
            case "Tier2Admin":
                userLevel = ConfigurationManager.configuration().appUserNameTier2Admin();
                password = ConfigurationManager.configuration().appPasswordTier2Admin();
                break;
            case "Tier2User":
                userLevel = ConfigurationManager.configuration().appUserNameTier2User();
                password = ConfigurationManager.configuration().appPasswordTier2User();
                break;
            case "Tier3EndUser":
                userLevel = ConfigurationManager.configuration().appUserNameTier3EndUser();
                password = ConfigurationManager.configuration().appPasswordTier3EndUser();
                break;

            default:
                userLevel = ConfigurationManager.configuration().appUserNameTier1Admin();
                password = ConfigurationManager.configuration().appPasswordTier1Admin();
                break;
        }
    }

    /**
     * Will be invoked before once for every test case execution and
     * a) will launch the browser based on config
     * b) create reporting structure
     * c) store login state (if configured)
     * d) create context, page
     * e) set default time out based on config
     * f) maximize and load the given URL
     *
     * @author Srikanth
     */
    @Parameters("Browser")
    @BeforeClass(groups = {"Tier1Admin", "Tier1User", "Tier2Admin", "Tier2User", "Tier3EndUser"})//beforemethod
    public void initBrowserAndDoLogin(@Optional("chrome") String browser) {
        try {
            setUserLevel();
            startTestCase();
            // Launch the browser (based on configuration) in head(less) mode (based on configuration)
            setBrowser(browser, ConfigurationManager.configuration().headless());

            // Set the extent report node for the test
            setNode();
            NewContextOptions newContext;
            // Default Settings
            if (!ConfigurationManager.configuration().browser().equalsIgnoreCase("cloud")) {
                newContext = new Browser.NewContextOptions()
                        .setIgnoreHTTPSErrors(true)
                        .setRecordVideoDir(Paths.get(folderName));
            } else {

                newContext = new Browser.NewContextOptions()
                        .setIgnoreHTTPSErrors(true);
            }


            // Auto Login if enabled
            if (ConfigurationManager.configuration().autoLogin()) {
                newContext.setStorageStatePath(Paths.get("storage/login.json"));
            }

            // Store for Auto Login, Set the video recording ON using context
            context.set(getBrowser().newContext(newContext));

            getContext().setDefaultNavigationTimeout(ConfigurationManager.configuration().timeout());

            // Create a new page and assign to the thread local
            page.set(getContext().newPage());

            // Set the timeout based on the configuration
            getPage().setDefaultTimeout(ConfigurationManager.configuration().timeout());

            // 	enable Tracing
            if (ConfigurationManager.configuration().enableTracing())
                getContext().tracing().start(new Tracing.StartOptions().setName(testcaseName).setSnapshots(true).setTitle(testcaseName));

            // Get the screen size and maximize
            //maximize();

            // Load the page with URL based on configuration
            navigate(ConfigurationManager.configuration().baseStagingUrl());
//            new MenuPage().doLogin(userLevel, password);

        } catch (Exception e) {
            reportStep("The browser and/or the URL could not be loaded as expected", "fail");
        }
    }


}
