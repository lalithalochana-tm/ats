package com.data.dynamic;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

import com.enums.Country;

import static com.config.ConfigurationManager.configuration;

import net.datafaker.Faker;

public class FakerDataFactory {

    private static final Faker faker = new Faker(new Locale(configuration().faker()));
    private static final Set<String> generatedNames = new HashSet<>();

    private FakerDataFactory() {

    }

    public static String getCompanyName() {
        return faker.company().name().replaceAll("[^a-zA-Z ]", "");
    }

    public static String getCompanyNameWithoutSpace() {
        return faker.company().name().replaceAll("[^a-zA-Z ]", "").replaceAll("[\\s]", "") + getTimeStamp();
    }

    public static String getUrl() {
        return faker.company().url();
    }

    public static String getAddress() {
        return faker.address().streetAddress();
    }

    public static String getFullAddress() {
        return faker.address().fullAddress();
    }

    public static String getCity() {
        return faker.address().city();
    }

    public static String getCountry() {
        return Country.getRandom().get().toString();
    }

//    public static String getFirstName() {
//        return faker.name().firstName().replaceAll("[^a-zA-Z ]", "");
//    }

    public static String getFirstName() {
        String firstName;
        do {
            firstName = faker.name().firstName().replaceAll("[^a-zA-Z ]", "");
        } while (!generatedNames.add(firstName));
        return firstName;
    }

    public static String getRoleName() {
        return faker.name().firstName().replaceAll("[^a-zA-Z ]", "") + getTimeStamp();
    }

    public static String getMiddleName() {
        return faker.name().nameWithMiddle();
    }

    public static String getTimeStamp() {
        Date date = new Date();
        SimpleDateFormat time = new SimpleDateFormat("HHmmss");
        return time.format(date);
    }

    public static String getLastName() {
        return faker.name().lastName().replaceAll("[^a-zA-Z ]", "");
    }

    public static String getPastDate() {
        Date today = new Date();
        Date pastWeek = faker.date().past(7, TimeUnit.DAYS, today);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(pastWeek);
    }

    public static String getFutureDate() {
        Date today = new Date();
        Date futureWeek = faker.date().future(7, TimeUnit.DAYS, today);
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        return dateFormat.format(futureWeek);
    }

    public static String getAnalysisName() {
        String name = faker.name().firstName();
        String analysis = "Analysis";
        return name + analysis.replaceAll("[^a-zA-Z ]", "");
    }

    public static String getReportsName() {
        String name = faker.name().firstName();
        String reports = "Reports";
        return name + reports.replaceAll("[^a-zA-Z ]", "");
    }


    public static String getEmailAddress() {
        return faker.internet().emailAddress();
    }


    public static String getContactNumber() {
        return faker.phoneNumber().cellPhone();
    }

    public static String getBankAccountNumber() {
        return "" + faker.number().randomNumber(10, false);
    }

    public static String getAlphanumeric() {
        return faker.number().randomNumber(6, false) + "lkjhhh";
    }

    public static String getTitle() {
        return faker.book().title();
    }

    public static String getDescription() {
        // Generate a description of approximately 50 words
        StringBuilder description = new StringBuilder();
        while (description.toString().split(" ").length < 50) {
            description.append(faker.lorem().sentence());
            description.append(" ");
        }
        return description.toString().trim();
    }


    public static String get12DigitsBankAccountNumber() {
        return "" + faker.number().randomNumber(12, false);
    }

    public static String getFTPFolderAcronym() {
        return "" + faker.number().randomNumber(4, false);
    }

    public static String getTaxNumber() {
        return "" + faker.number().randomNumber(7, false);
    }

    public static String getPostalCode() {
        return "" + faker.number().randomNumber(5, false);
    }

    public static String getDateFormatting() {
        return faker.options().option("MM/DD/YYYY", "DD/MM/YYYY", "YYYY-MM-DD");
    }

    public static String getTimeFormatting() {
        return faker.options().option("12-hour", "24-hour");
    }

    public static String getRuleDescription() {
        String word1 = faker.lorem().word();
        String word2 = faker.lorem().word();
        return word1 + " " + word2;
    }

    public static String getFieldName() {
        String buzzword = faker.company().buzzword(); // Generate a random buzzword
        String timeStamp = getTimeStamp(); // Get the current time in HH:mm:ss format
        return buzzword + "-" + timeStamp;
    }

    public static String getBrokerNameWOTimeStamp() {
        return faker.company().industry();
    }

    public static String getListName() {
        String buzzword = faker.company().buzzword(); // Generate a random buzzword
        String timeStamp = getTimeStamp(); // Get the current time in HH:mm:ss format
        return buzzword + "-" + timeStamp;
    }


    public static List<String> getListOfItems(int count) {
        List<String> listItems = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            listItems.add(FakerDataFactory.getListItem());
        }
        return listItems;
    }

    public static List<String> recipientNames(int count) {
        List<String> recipientNames = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            recipientNames.add(FakerDataFactory.getListItem());
        }
        return recipientNames;
    }

    public static String getListItem() {
        String buzzword = faker.company().buzzword();
        return buzzword.split(" ")[0];
    }

    public static String getDataMapName() {
        return faker.name().title().replaceAll("[^a-zA-Z ]", "");
    }

    public static List<String> getSystemColumns() {
        String[] columns = {"Broker Name", "Trade Date", "Settlement Date"}; //"Base Gross Commission","Local Gross Commission","Base Executed Value","Trade ID","Account Name"
        List<String> Items = new ArrayList<>(List.of(columns));
        return Items;
    }

    public static String getNameWithTimeStamp(String type) {
        String name = faker.name().firstName();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
        String formattedTime = sdf.format(date);
        return name + "-" + type + "-" + formattedTime;

    }

}
