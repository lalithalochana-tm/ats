package com.page.base;

public class MenuPage extends CognitestPageHook {

    // Locator for Email Field
    protected String emailField = "#email";
    // Locator for Password Field
    protected String passwordField = "#password";
    // Locator for Login Button
    protected String loginButton = "#loginBtn";
    // Locator for Transaction ID Field
    protected String transactionIdField = "#transaction_id";
    // Locator for Search Button
    protected String searchButton = "#searchBtn";
    // Locator for TickingMinds_Test Text
    protected String tickingMindsText = "text=TickingMinds_Test";
    // Locator for Insurance Search Text
    protected String insuranceSearchText = "text=Insurance Search";
    // Locator for Search Results Text
    protected String searchResultsText = "text=Search Results";
    // Locator for Transaction Information Text
    protected String transactionInformationText = "text=Transaction Information";
    // Locator for Loan Number Field
    protected String loanNumberField = "#loan_number";
    // Locator for Loan Summary Text
    protected String loanSummaryText = "text=Loan Summary";
    // Locator for Policy Number Field
    protected String policyNumberField = "#policy_number";
    // Locator for Policy Information Text
    protected String policyInformationText = "text=Policy Information";
    // Locator for Borrower First Name Field
    protected String borrowerFirstNameField = "#borrower_first_name";

    public MenuPage navigateToHomePage() throws InterruptedException {
        Thread.sleep(5000);
        navigate("http://3.82.99.137:3000/");
        return this;
    }

    public MenuPage verifyTickingMindsText() throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(tickingMindsText, "TickingMinds_Test");
        return this;
    }

    public MenuPage fillEmail(String email) throws InterruptedException {
        Thread.sleep(5000);
        type(emailField, email, "Email");
        return this;
    }

    public MenuPage verifyEmail(String email) throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(emailField, email);
        return this;
    }

    public MenuPage fillPassword(String password) throws InterruptedException {
        Thread.sleep(5000);
        type(passwordField, password, "Password");
        return this;
    }

    public MenuPage verifyPassword(String password) throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(passwordField, password);
        return this;
    }

    public MenuPage clickLoginButton() throws InterruptedException {
        Thread.sleep(5000);
        click(loginButton, "Login Button");
        return this;
    }

    public MenuPage verifyInsuranceSearchText() throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(insuranceSearchText, "Insurance Search");
        return this;
    }

    public MenuPage fillTransactionId(String transactionId) throws InterruptedException {
        Thread.sleep(5000);
        type(transactionIdField, transactionId, "Transaction ID");
        return this;
    }

    public MenuPage verifyTransactionId(String transactionId) throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(transactionIdField, transactionId);
        return this;
    }

    public MenuPage clickSearchButton() throws InterruptedException {
        Thread.sleep(5000);
        click(searchButton, "Search Button");
        return this;
    }

    public MenuPage verifySearchResultsText() throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(searchResultsText, "Search Results");
        return this;
    }

    public MenuPage clickTransactionLink(String transactionId) throws InterruptedException {
        Thread.sleep(5000);
        click("a[href=\"/transaction-details/" + transactionId + "\"]", "Transaction Link");
        return this;
    }

    public MenuPage verifyTransactionInformationText() throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(transactionInformationText, "Transaction Information");
        return this;
    }

    // New method to fill Borrower First Name
    public MenuPage fillBorrowerFirstName(String borrowerFirstName) throws InterruptedException {
        Thread.sleep(5000);
        type(borrowerFirstNameField, borrowerFirstName, "Borrower First Name");
        return this;
    }

    // New method to verify Borrower First Name
    public MenuPage verifyBorrowerFirstName(String borrowerFirstName) throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(borrowerFirstNameField, borrowerFirstName);
        return this;
    }

    // New method to fill Loan Number
    public MenuPage fillLoanNumber(String loanNumber) throws InterruptedException {
        Thread.sleep(5000);
        type(loanNumberField, loanNumber, "Loan Number");
        return this;
    }

    // New method to verify Loan Number
    public MenuPage verifyLoanNumber(String loanNumber) throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(loanNumberField, loanNumber);
        return this;
    }

    // New method to fill Policy Number
    public MenuPage fillPolicyNumber(String policyNumber) throws InterruptedException {
        Thread.sleep(5000);
        type(policyNumberField, policyNumber, "Policy Number");
        return this;
    }

    // New method to verify Policy Number
    public MenuPage verifyPolicyNumber(String policyNumber) throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(policyNumberField, policyNumber);
        return this;
    }

    // New method to verify Loan Summary text
    public MenuPage verifyLoanSummaryText() throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(loanSummaryText, "Loan Summary");
        return this;
    }

    // New method to verify Policy Information text
    public MenuPage verifyPolicyInformationText() throws InterruptedException {
        Thread.sleep(5000);
        verifyExactText(policyInformationText, "Policy Information");
        return this;
    }
}