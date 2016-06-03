# WhereIsHome

## Features

Commute list:

* Display human readable week total
* Change a commute
* Update commuting times when home address is changed

Add commute activity:

- Preview commute time before accepting
- Use Citymapper API to calculate commuting time

## Planned features

Multiple scenarios:

- Create scenario
- Read scenarios: list as tabs or view pager
- Update a scenario
- Delete a scenario
- Clone a scenario

Commute list:

- None

Add commute activity:

- Number of commutes
    - Value empty by default
    - Choose value per month
- Place picker
    - Lower zoom level when modifying a place

## Known issues

- No spinner when waiting for distance calculation service response
- "You're about to add a new commute" should be "This will be your new home" when changing home address
- Margins are too small in the address in "You're about to add a new commute" screen
- Delay when opening place picker in flight mode
- "Error during CityMapper call" should be "Error during distance calculation"
* Missing automated functional test