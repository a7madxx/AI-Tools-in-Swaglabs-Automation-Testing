# Swag Labs Test Automation Project

This is a test automation framework for the [Swag Labs](https://www.saucedemo.com/) demo website, designed to automate and validate the core e-commerce functionality.

## ✨ Features

*   **Page Object Model (POM):** Test cases are separated from the UI locators and interactions, making the framework more maintainable and readable.
*   **Data-Driven Testing:** Test data is externalized into JSON files (`src/test/resources/TestData`), allowing for easy modification and extension of test cases without changing the code.
*   **Selenium WebDriver:** Automates browser interactions to simulate user actions.
*   **TestNG:** Manages the test execution lifecycle, including assertions, test suites, and parallel execution.
*   **Allure Framework:** Generates clear and interactive test reports.
*   **Log4j:** Provides detailed logging for debugging and traceability.
*   **Maven:** Handles project dependencies and the build lifecycle.

##  Prerequisites

Before you begin, ensure you have the following installed:
*   [Java JDK](https://www.oracle.com/java/technologies/downloads/) (Version 11 or higher)
*   [Apache Maven](https://maven.apache.org/download.cgi)
*   A supported web browser (e.g., Google Chrome, Firefox)

## 🚀 How to Run Tests

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/el5DraGy/Automation-Project_SwagLabs.git
    cd Automation-Project_SwagLabs
    ```

2.  **Run all tests:**
    To execute the full regression suite, run the following Maven command from the project root directory:
    ```bash
    mvn clean test
    ```
    This command will clean the project, install dependencies, and run the tests defined in `RegressionSuite.xml`.

3.  **Run a specific test suite:**
    You can run a specific suite (e.g., `LoginSuite.xml`) using the `surefire.suiteXmlFiles` property.
    ```bash
    mvn test -Dsurefire.suiteXmlFiles=TestRunner/LoginSuite.xml
    ```

## 📊 How to View Reports

The project uses Allure to generate test execution reports.

1.  **Generate the report:**
    After running the tests, the results will be available in the `target/allure-results` directory. To generate and view the HTML report, run:
    ```bash
    allure serve target/allure-results
    ```
    This command will start a local web server and open the report in your default browser.
