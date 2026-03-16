# Country Akinator

Country Akinator is a CLI Clojure project inspired by Akinator.  
The goal of the application is to guess a **UN member state** that the user has in mind.

The user thinks of a country, and the program asks a sequence of **yes/no questions** in the console. Based on the answers, the program filters the remaining candidate countries and selects the next best question.

The objective is to guess the correct country in a small number of questions.

---

# Project Idea

The application uses a **MySQL database** that contains data about all UN member states.

The database contains the following information:

- country attributes (`countries`)
- organization memberships (`country_organizations`)
- regional classifications (`country_regions`)
- neighboring countries (`country_neighbors`)

Questions are generated automatically from these datasets.

Examples of possible questions:

- Is the country in Europe?
- Is the country a member of NATO?
- Is the country landlocked?
- Does the country border Serbia?
- Does the country's flag contain a star?

The algorithm evaluates all possible questions and selects the ones that best divide the remaining candidate countries.

From the **top 5 best questions**, one is chosen randomly to avoid deterministic gameplay.

---

# Tech Stack

The project is implemented using the following technologies:

- Clojure
- Leiningen
- Midje (testing framework)
- MySQL
- IntelliJ IDEA

## Database

The project uses a MySQL database containing all UN member states and their attributes.

Main tables:

- `countries`
- `country_regions`
- `country_organizations`
- `country_neighbors`

These tables allow the application to generate questions dynamically and evaluate how well they split the remaining candidate countries.

---

# Project Structure

```
country-akinator
│
├── project.clj
├── README.md
│
├── resources
│   └── config.edn
│
├── src
│   └── country_akinator
│       ├── core.clj
│       ├── config.clj
│       └── db.clj
│
└── test
    └── country_akinator
        └── core_test.clj
```

# Running the Project

Run the application:

lein run

Run the tests:

lein midje

---

# Development Progress

## Part 1 — Project Setup

The first stage of the project focused on setting up the basic application structure.

Completed tasks:

- Created a new Leiningen project
- Configured project dependencies in `project.clj`
- Implemented application configuration file `config.edn`
- Implemented configuration loader (`config.clj`)
- Implemented database specification builder (`db.clj`)
- Added a basic CLI entry point in `core.clj`
- Added initial Midje tests

This part establishes the basic infrastructure needed for the rest of the project.

## Part 2 — Database integration

In this stage the application was connected to the MySQL database.

Implemented features:

- database connection via `clojure.java.jdbc`
- first SQL queries
- repository layer for accessing countries
- loading all countries from the database
- loading a single country by id
- building a map `{id -> country}` for fast lookup

## Part 3 – Question Model and Basic Game Logic

Implemented:
- answer model (`YES`, `NO`, `DONT KNOW`)
- automatic question generation from country attributes
- question text generation
- question evaluation for enum and boolean questions
- candidate filtering based on user answers
- Midje tests for question generation and filtering

---



# License

Copyright © 2026 Aleksandar Ljubic


