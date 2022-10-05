# RushHour - The Appointment Platform

![Rush Hour](https://github.com/NMItools/RushHour/blob/main/rushhour.jpg)

## Overview

Rush Hour is appointment scheduling software. It can be used in various areas, i.e., medical services, beauty and wellness, sport, etc. It supports business accounts for providers of service, and individual ones - for the clients who can make appointments.

## Speciﬁcation

Rush Hour is a RESTful API with a layered architecture, using a relational database for its persistence layer. The required layers are: 
- data access, 
- business, 
- presentation 
but could not be limited to these layers only. All the business logic should be tested with unit tests, and optionally - integration tests.

## Entities

### [Provider]

Represents the service provider that is subscribed to Rush Hour. Consists of:
- Name
- Website
- Business domain
- Phone
- Start time of the working day
- End time of the working day
- Working days

### [Account]

Represents the account of all users who can access the system (provider’s employees and clients). Consists of:
- Email
- Full name
- Password
- Role

### [Employee]

Represents an employee in a service provider company. Consists of:
- Title
- Phone
- Rate per hour
- Provider
- Account

### [Client]

Represents a client of the service provider. One client can have multiple appointments for multiple service providers. Consists of:
- Phone
- Address
- Account

### [Role]

Represents the account’s role. Consists of:
- Name
- Account

3 roles are supported in RushHour: **Provider Administrator, Employee and Client**

### [Activity]

Represents an activity that can be part of an appointment. Consists of:
- Name
- Price
- Duration
- Provider
- Employees

### [Appointment]

Represents an appointment. Consists of:
- Start date
- End date
- Employee
- Client
- Activities
- Price

# Application Skeleton

- N-layered architecture (data access, business, presentation layers, and tests). 
- documentation tool (Swagger/Open API) 
- logging tool

_**[Accounts Management]**_
- create, read, update and delete options on all accounts.
- log in the system and access the resources based on their roles.
- jwt token authentication.
- The provider/administrator create a provider, then create accounts for employees.
 
_**[CRUD functionality]**_
- providers, 
- employees, 
- clients, 
- activities
- appointments.

_**[Reporting Engine]**_

Reports for provider administrator:

- Productivity report
- Availability report
- Income per month/quarter/year

Reports for employees:

- Availability report (day/week)
- Most/Least booked time of the day/week
- Top 10 clients who spent the most money/time per month/quarter/year

Reports for clients:

- Top 3 favorite providers for the last month/quarter/year
- Top 5 favorite activities for the last month/quarter/year
- Expenses report (week/month/quarter/year)
  ○ Per provider
  ○ Per business domain
  ○ Per activity

_**[Integrations]**_
- Google Calendar
- Microsoft Calendar
