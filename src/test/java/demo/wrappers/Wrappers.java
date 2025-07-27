package demo.wrappers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.text.NumberFormat;
import java.time.Duration;

public class Wrappers {
    /*
     * Write your selenium wrappers here
     */
    public static void enterTextWrapper(WebDriver driver, By locator, String textToEnter) {
        System.out.println("Sending Keys");
        try {
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
            WebElement inputBox = driver.findElement(locator);
            inputBox.clear();
            inputBox.sendKeys(textToEnter);
            inputBox.sendKeys(Keys.ENTER);
        } catch (Exception e) {
            System.out.println("Exception Occured! " + e.getMessage());
        }
    }

   public static void clickOnElementWrapper(WebDriver driver, By locator) {
    System.out.println("Clicking");
    try {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        WebElement clickableElement = wait.until(ExpectedConditions.elementToBeClickable(locator));
        clickableElement.click();
    } catch (Exception e) {
        System.out.println(":: Exception Occured! " + e.getMessage());
        e.printStackTrace();
    }
}

 public static Boolean searchStarRatingAndPrintCount(WebDriver driver, By locator, double starRating) {
    int washingMachineCount = 0;
    Boolean success;
    try {
        List<WebElement> starRatingElements = driver.findElements(locator); // initial list

        for (int i = 0; i < starRatingElements.size(); i++) {
            try {
                // Re-locate the element to avoid stale reference
                WebElement starRatingElement = driver.findElements(locator).get(i);
                String ratingText = starRatingElement.getText().trim();

                if (!ratingText.isEmpty()) {
                    double rating = Double.parseDouble(ratingText);
                    if (rating <= starRating) {
                        washingMachineCount++;
                    }
                }
            } catch (StaleElementReferenceException sere) {
                System.out.println("Stale element, skipping index: " + i);
                continue; // or retry if needed
            }
        }

        System.out.println("Count of washing machines with rating <= " + starRating + " = " + washingMachineCount);
        success = true;
    } catch (Exception e) {
        System.out.println(":: Exception Occurred! ");
        e.printStackTrace();
        success = false;
    }
    return success;
}

    public static Boolean printTitleAndDiscountIphone(WebDriver driver, By discountLocator, int discountThreshold) {
    boolean success = false;
    try {
        // List of discount elements (e.g., "20% off")
        List<WebElement> discountElements = driver.findElements(discountLocator);

        // Iterate over all discounts
        for (WebElement discountElement : discountElements) {
            String discountText = discountElement.getText().trim();
            if (discountText.isEmpty()) continue;

            // Extract only numbers from "20% off"
            int discountValue = Integer.parseInt(discountText.replaceAll("[^0-9]", ""));
            if (discountValue > discountThreshold) {

                // Locate product title relative to the discount element
                WebElement productCard = discountElement.findElement(By.xpath("ancestor::div[@class='tUxRFH']"));
                String productTitle = productCard.findElement(By.xpath(".//div[@class='KzDlHZ']")).getText().trim();

                System.out.println("iPhone Title: " + productTitle + " | Discount: " + discountValue + "%");
                success = true;
            }
        }

        if (!success) {
            System.out.println("No iPhone found with discount > " + discountThreshold + "%");
        }
    } catch (Exception e) {
        System.out.println("Exception Occurred: " + e.getMessage());
        success = false;
    }
    return success;
}


  public static Boolean printTitleAndImageUrlOfCoffeeMug(WebDriver driver, By locator) {
    Boolean success;

    try {
        List<WebElement> userReviewElements = driver.findElements(locator); // Coffee mugs reviews
        if (userReviewElements.isEmpty()) {
            System.out.println("No review elements found.");
            return false;
        }

        Set<Integer> userReviewSet = new HashSet<>();

        for (WebElement userReviewElement : userReviewElements) {
            String reviewText = userReviewElement.getText().replaceAll("[^\\d]", "");
            if (!reviewText.isEmpty()) {
                userReviewSet.add(Integer.parseInt(reviewText));
            }
        }

        List<Integer> userReviewCountList = new ArrayList<>(userReviewSet);
        Collections.sort(userReviewCountList, Collections.reverseOrder());
        System.out.println(userReviewCountList);

        LinkedHashMap<String, String> productDetailsMap = new LinkedHashMap<>();
        int limit = Math.min(5, userReviewCountList.size());

        for (int i = 0; i < limit; i++) {
            String formattedUserReviewCount = "(" + NumberFormat.getNumberInstance(Locale.US)
                    .format(userReviewCountList.get(i)) + ")";

            try {
                String productTitle = driver.findElement(By.xpath("//div[@class='s1AVV4']/span[contains(text(),'"
                        + formattedUserReviewCount + "')]/../../a[@class='wjcEIp']")).getText();

                String productImgURL = driver.findElement(By.xpath("//div[@class='s1AVV4']/span[contains(text(),'"
                        + formattedUserReviewCount + "')]/../../img[@class='DByu4f']")).getAttribute("src");

                String key = (i + 1) + " highest review count: " + formattedUserReviewCount + " Title: " + productTitle;
                productDetailsMap.put(key, productImgURL);

            } catch (Exception e) {
                System.out.println("Could not fetch title or image for review count: " + formattedUserReviewCount);
            }
        }

        for (Map.Entry<String, String> productDetails : productDetailsMap.entrySet()) {
            System.out.println(productDetails.getKey() + " and Product image url: " + productDetails.getValue());
        }

        success = true;

    } catch (Exception e) {
        System.out.println("Exception occurred ");
        e.printStackTrace();
        success = false;
    }

    return success;
}
}