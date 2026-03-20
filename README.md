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
- `regions`
- `country_organizations`
- `organizations`

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

## Part 4 – Question Scoring and Selection

Implemented:

* question scoring based on how well a question splits the remaining countries
* counting YES and NO distributions for each question
* ranking questions by score
* selecting the top N questions
* random selection of one question from the top 5

In this stage, the project gained the core logic for choosing better questions instead of asking them in arbitrary order.

A question is scored based on how balanced the YES/NO split is among the remaining candidate countries.  
Questions that divide the candidates more evenly receive a better score.

After scoring all generated questions, the algorithm ranks them, keeps the best ones, and randomly selects one from the top 5.  
This improves gameplay and avoids always asking the exact same sequence of questions.

## Part 5 – Game Loop and Integration with Database

Implemented:

* interactive game loop for asking questions and processing user input
* integration with MySQL database (loading all countries at the start of the game)
* dynamic selection of the next question using the scoring algorithm
* filtering candidate countries based on user answers
* tracking already asked questions to avoid repetition
* stopping conditions when the number of candidate countries becomes small

In this stage, the application became a fully functional CLI game.
At the start of the game, all countries are loaded from the database and treated as possible candidates.

The system then repeatedly:

* selects the next best question using the scoring mechanism from Part 4
* asks the user the question
* processes the answer (YES / NO / DONT KNOW)
* filters the candidate countries accordingly
* The game continues until only one or a small number of countries remain.

At the end of the game:

* if exactly one country remains, the system outputs the final guess
* if multiple countries remain, the system outputs the best candidates
* This completes the core functionality of the Country Akinator application and connects all previously implemented components into a working system.

## Part 6 – Improved Question Selection and Game Ending

In this part, the game flow is improved so that the application continues asking questions until only one country remains.

Question selection is also refined by introducing a minimum score threshold. Only questions with score greater than 0 are considered for the top ranked questions. If more than five such questions exist, the best five are taken and one of them is selected randomly. If fewer than five questions satisfy the condition, all of them are considered.

If no remaining question can reduce the current set of candidate countries, the game stops and displays the remaining countries as the best possible matches.

## Part 7 – Numeric Fallback Questions

In this part, numeric fallback questions are introduced for situations where only a small number of useful regular questions remain.

The application now generates fallback questions based on the median values of the remaining candidate countries for population, area, and number of bordering countries. One random numeric fallback question is added when fewer than five regular questions have a positive score.

This allows the game to continue narrowing down the candidate set even in later rounds, when boolean and enum questions may no longer provide enough useful distinctions.

The game now continues until only one country remains. If no remaining question can reduce the current set of candidate countries, the application stops and displays the remaining countries as the best possible matches.

## Part 8 – Region and Organization Membership Questions

In this part, the set of regular questions is extended with questions based on country regions and international organizations.

The application now loads additional data from the database through the country_regions, regions, country_organizations, and organizations tables. Each country is enriched with its corresponding regions and organization memberships.

Based on this data, the system automatically generates new regular questions such as whether a country belongs to a certain region or whether it is a member of a certain international organization.

These questions are treated the same as other regular questions: they are scored, ranked, and considered during the selection of the next question. This improves the ability of the game to distinguish between countries using additional relational data from the database.

## Part 9 – Improved Final Output and Replay Option

In this part, the console output is improved to make the game result clearer and more informative.

When the application successfully narrows the candidate set down to one country, it now prints a short summary of that country, including its continent, population, area, number of bordering countries, major religion, and main language family. If available, the output also includes the regions the country belongs to and the international organizations of which it is a member.

In addition, the application now allows the user to start another round after the game ends. This makes the program easier to use and improves the overall command-line experience.

---



# License

Copyright © 2026 Aleksandar Ljubic


