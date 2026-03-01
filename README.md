# SecuritEase Technical Assessment

## Overview

This is a java based console application for my SecuritEase technical assessment. It determines which call centre around the world can take a users call based on shared languages and current business hours.

## How it works

- The program takes two inputs from a user, their country name and current time in 24H format
- The country name must match the official name from the API
- It then returns a list of countries that share at least a single language with the user's country and are currently in business hours between 9 AM and 5 PM

## Running the application

### Needed

- Java 17+
- Maven
- Internet connection for API calling

### Running the app

The application:

```bash
mvn compile exec:java "-Dexec.mainClass=App"
```

Or in VS code the play button.

Testing:

```bash
mvn test
```

### Assumptions made

- Business hour are between 9 AM and 5 PM inclusively
- Timezone handling - If a country has multiple timezones the first time zoned returned by the API is the one that is used. Also if a single countries time zone qualifies to have a call routed to them then they will be added to the eligible list.
- Country names are on the official names not common names
- A callers own country will appear in results if it is currently within business hours
- The API returns 250 countries total, I use this as my assumed country count

### Design decisions

- I chose to use HashSets for languages and timezones to guarentee uniqueness and improve efficiency
- My findCentre method returns a hashMap so that I was able to implement unit tests for it

## Tests

- countryTotal - verified the API returns 250 countries
- saslOfficial - South African Sign Language is not currently in the API and this confirms
- fieldCheck - Validates if the 10 field maximum is enforced
- antartica - Should have no returns since no languages are returned by the API
- midnight - Day handling for countries who are on the next day or after 2359
- nine - Boundary test for 9 AM exactly
- five - Boundary test for 5 PM exactly
- ownCountry - Home country appears in results within business hours
=======
Overview: This is a java based console application for my SecuritEase technical assessment. It determines which call centre around the world can take a users call based on shared languages and current business hours.

How it works:
  The program takes two inputs from a user, their country name and current time in 24H format
    The country name must match the official name from the API
  It then returns a list of countries that share at least a single language with the user's country and are currently in business hours between 9 AM and 5 PM
Running the application
  Needed
    Java 17+
    Maven
    Internet connection for API calling
  Running the app
    The application: mvn compile exec:java "-Dexec.mainClass=App" (or in VS code the play button)
    Testing: mvn test
  Assumptions made
    Business hour are between 9 AM and 5 PM inclusively 
    Timezone handling - If a country has multiple timezones the first time zoned returned by the API is the one that is used. Also if a single countries time zone qualifies to have a call routed to them then they will be added to the eligible list. 
    Country names are on the official names not common names
    A callers own country will appear in results if it is currently within business hours
    The API returns 250 countries total, I use this as my assumed country count
  Design decisions
    I chose to use HashSets for languages and timezones to guarentee uniqueness and improve efficiency 
    My findCentre method returns a hashMap so that I was able to implement unit tests for it
Tests
  countryTotal - verified the API returns 250 countries
  saslOfficial - South African Sign Language is not currently in the API and this confirms 
  fieldCheck - Validates if the 10 field maximum is enforced 
  antartica - Should have no returns since no languages are returned by the API
  midnight - Day handling for countries who are on the next day or after 2359
  nine - Boundary test for 9 AM exactly
  five - Boundary test for 5 PM exactly 
  ownCountry - Home country appears in results within business hours
