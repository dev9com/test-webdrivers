# Test Webdrivers 
[![Build Status](https://travis-ci.org/dev9com/test-webdrivers.svg?branch=master)]
(https://travis-ci.org/dev9com/test-webdrivers)

This project contains easy to use webdriver implementations for both TestNG and JUnit testing frameworks. These 
libraries remove the initialization and teardown concerns, allowing the user to focus on 
writing UI tests.

## Common Settings

1. Add dependency to pom (or download and add to classpath) [Maven Central](http://mvnrepository.com/artifact/com.dev9)
2. Create a .conf file and define browser configuration in your resource directory  
    * Create config file: ```src/test/resources/application.conf```
    * Add the default browser:
    
        webdriver {
            browser: firefox
            type:    local
        }
        
    * Add any additional browser configurations by wrapping the webdriver object with a unique name:
        
        local-chrome {
            webdriver {
                browser:  chrome
                type:     local
            }
        }
        
    * The webdriver projects support remote jobs through SauceLabs. For this to work you must first define 2 
    environment variables  ```SAUCELABS_USER``` assigned your SauceLabs username and ```SAUCELABS_KEY``` assigned 
    your SauceLabs key. Once that is complete you can define a new webdriver config using the ```type: remote``` flag:
        
        remote-iexplore {
            webdriver {
                browser:  iexplore
                type:     remote
                version:  8
                platform: windows
            }
        }

    * To select a profile you would use the flag flag: ```-Dwebtest.profile=``` and give it your configuration name:
    ```-Dwebtest.profile=local-chrome``` or ```-Dwebtest.profile=remote-iexplore```
    

## Framework Specific
See: [TestNG](https://github.com/dev9com/test-webdrivers/tree/master/testng-webdriver)
 or 
[JUnit](https://github.com/dev9com/test-webdrivers/tree/master/junit-webdriver)