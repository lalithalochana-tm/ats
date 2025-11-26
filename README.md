
    # West-Minster Framework Overview

    The West-Minster framework offers a comprehensive set of tools and utilities designed for application development, particularly focusing on configuration management, data handling, UI interactions, and testing. Below are some key components and their functionalities:

    ## Key Components

    ### Configuration

    - **Configuration.java**: Manages application configuration settings.
    - **ConfigurationManager.java**: Handles loading and accessing configuration properties.

    ### Data Management

    - **FakerDataFactory.java**: Generates dynamic test data using Faker library.

    ### Enumerations

    - **Country.java**: Defines country-related enumerations used across the application.

    ### Listeners

    - **TestListener.java**: Implements test listeners for monitoring test execution and events.

    ### UI Base

    - **DriverFactory.java**: Manages WebDriver instances for browser interactions.
    - **PlaywrightWrapper.java**: Provides a wrapper around Playwright for browser automation.
    - **ProjectHooks.java**: Contains setup and teardown methods for test execution.

    ### Utilities

    - **DateUtils.java**: Utility methods for handling date and time operations.
    - **ExcelReader.java**: Reads data from Excel files for use in tests.
    - **Reporter.java**: Generates test execution reports.
    - **RetryTestCase.java**: Implements retry logic for failed test cases.
    - **StringUtils.java**: String manipulation utilities.
    - **TestGroups.java**: Defines test groupings for organizing test execution.
    - **XMLReaderUtil.java**: Utility for reading and processing XML files.

    ### Resources

    - **app.properties**: Application configuration properties.
    - **local.properties**: Local environment configuration properties.
    - **report.properties**: Reporting configuration properties.

    ### Page Objects

    - **DashboardPage.java**: Represents the dashboard page of the application.
    - **MenuPage.java**: Represents the menu navigation in the application.
    - **WestMinsterPageHook.java**: Hooks for page object initialization.

    ### Budgeting Pages

    - **AllBudgetsPage.java**: Handles interactions with the 'All Budgets' page.
    - **CreateNewBudgetPage.java**: Manages the creation of new budgets.
    - **ImportNewBudgetPage.java**: Handles importing new budgets.
    - **EditBudgetPage.java**: Manages editing existing budgets.
    - **ImportBudgetPage.java**: Handles budget import functionalities.

    ### Test Cases

    - **DataProviders.java**: Provides data for parameterized tests.
    - **admin/TC_DataManager.java**: Test cases for data management.
    - **admin/TC_FileManager.java**: Test cases for file management.
    - **admin/TC_ListManager.java**: Test cases for list management.
    - **admin/TC_RulesManager.java**: Test cases for rules management.
    - **admin/TC_UsersAndSecurity.java**: Test cases for user and security management.
    - **analytics/TC_Analytics.java**: Test cases for analytics functionalities.
    - **budgeting/TC_Budgeting.java**: Test cases for budgeting features.
    - **report/TC_Reports.java**: Test cases for reporting functionalities.

    ### Test Resources

    - **Import/Budget_Import-Sample.csv**: Sample data for budget import testing.
    - **upload/Budget_Import_Chrome.csv**: Sample file for budget import in Chrome.
    - **upload/SampleFile_AU_Chrome.csv**: Sample file for testing file upload in Chrome.
    - **upload/SampleFile_AU_Edge.csv**: Sample file for testing file upload in Edge.

    ---

    This README provides a high-level overview of the framework and helps in navigating through its various components efficiently.
    