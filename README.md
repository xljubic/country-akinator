# Country Akinator

Country Akinator is a CLI Clojure project inspired by Akinator.  
The goal of the application is to guess a **UN member state** that the user has in mind.

The user thinks of a country, and the program asks a sequence of **yes/no questions** in the console. Based on the answers, the program filters the remaining candidate countries and selects the next best question.

The objective is to guess the correct country in a small number of questions.

---

# Project Idea

Country Akinator is a command-line game inspired by Akinator, whose goal is to guess a UN member state chosen by the user.

At the start of the game, all countries from the database are loaded and treated as possible candidates. Each country is represented in memory as a Clojure map containing its direct attributes from the `countries` table, together with additional data about regions and international organizations.

The user thinks of one country and answers the questions asked by the application using one of the following answers:

* `YES`
* `NO`
* `DONT KNOW`

The application does not rely on a fixed sequence of manually written questions. Instead, it generates questions automatically from the data currently available for the remaining candidate countries.

The project currently supports four groups of questions:

* **enum questions** – based on categorical attributes such as continent, majority religion, and main language family
* **boolean questions** – based on true/false attributes such as landlocked status, island status, monarchy, federation, nuclear weapons, coastlines, and flag features
* **membership questions** – based on region memberships and organization memberships
* **numeric fallback questions** – based on median values of the remaining candidate countries for population, area, and number of bordering countries

In every round, the application evaluates all currently available questions and estimates how useful each of them would be for reducing the current candidate set.

Questions that split the remaining countries more evenly are considered better. After scoring all useful questions, the application keeps up to the five best ones and randomly selects one of them. This avoids deterministic gameplay while still preferring informative questions.

If, in later rounds, too few useful regular questions remain, the application can introduce one numeric fallback question. This helps the game continue narrowing down the candidate set even when many standard questions no longer provide enough information.

After each user answer, the candidate set is updated:

* `YES` keeps only countries that match the asked question
* `NO` removes countries that match the asked question
* `DONT KNOW` keeps the candidate set unchanged, but the same question is not asked again

The game continues until only one country remains or until no remaining question can further reduce the current set of candidates. In that case, the application outputs the remaining best matches.

---

# Tech Stack

The project is implemented using the following technologies:

- Clojure
- Leiningen
- Midje (testing framework)
- MySQL
- IntelliJ IDEA

## Database

The project uses a MySQL database that stores data about UN member states and the additional relational data needed for dynamic question generation.

The application currently relies on the following tables:

* `countries`
* `country_regions`
* `regions`
* `country_organizations`
* `organizations`

The `countries` table stores the main descriptive attributes of each country, including categorical, boolean, and numeric attributes such as continent, religion, language family, population, area, and number of bordering countries.

The `country_regions` and `regions` tables are used to assign one or more regional classifications to each country.

The `country_organizations` and `organizations` tables are used to assign one or more international organization memberships to each country.

When the application starts, countries are first loaded from the `countries` table. After that, each country is enriched in memory with:

* a set of regions
* a set of organizations

This allows the rest of the application to work on a single unified in-memory representation of a country and to generate questions dynamically from both direct country attributes and relational membership data.

---

# Project Structure

The project is organized into source files responsible for configuration, database access, question generation, scoring, core game logic, and interactive gameplay.
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
    │       ├── answers.clj
    │       ├── config.clj
    │       ├── core.clj
    │       ├── db.clj
    │       ├── game.clj
    │       ├── play.clj
    │       ├── questions.clj
    │       ├── repository.clj
    │       └── scoring.clj
    │
    └── test
        └── country_akinator
            ├── core_test.clj
            ├── game_test.clj
            ├── play_test.clj
            ├── questions_test.clj
            └── scoring_test.clj
```

### File Responsibilities

* `project.clj`  
  Contains project metadata, dependencies, and Leiningen configuration.

* `resources/config.edn`  
  Stores configuration values used for the database connection.

* `config.clj`  
  Loads configuration values from `config.edn`.

* `db.clj`  
  Builds the database specification used by `clojure.java.jdbc`.

* `repository.clj`  
  Loads all countries from the database and enriches them with region and organization memberships.

* `answers.clj`  
  Defines the answer model used during gameplay.

* `questions.clj`  
  Generates all supported question types and converts question maps into human-readable text shown in the console.

* `game.clj`  
  Contains the core logic for checking whether a country matches a given question and for filtering the candidate set after an answer.

* `scoring.clj`  
  Calculates question scores, ranks questions, removes useless ones, and chooses one question from the best-scored group.

* `play.clj`  
  Implements the interactive game loop: selecting the next question, reading user input, updating candidates, and printing the result.

* `core.clj`  
  Entry point of the application. It loads all countries and starts the game.

* `*_test.clj` files  
  Contain Midje tests for question generation, scoring, filtering, fallback logic, and the interactive game flow.

---

## Algorithm Overview

This section explains how the application works step by step.

### 1. Loading and Preparing Data

At the beginning of the program, all countries are loaded from the `countries` table.

After that, each country is enriched with additional relational data:

* its regions
* its organization memberships

This means that each country is represented in memory as one Clojure map that contains:

* direct country attributes
* a set of regions
* a set of organizations

This unified structure makes it possible to evaluate all question types in a consistent way.

### 2. Initial Candidate Set

At the start of the game, all loaded countries are considered possible answers.

So the initial candidate set is simply:

`all countries from the database`

As the game progresses, this set becomes smaller after every useful answer.

### 3. Question Generation

For the current candidate set, the application generates regular questions automatically.

#### 3.1 Enum Questions

Enum questions are generated from categorical attributes such as:

* `continent`
* `religion_majority`
* `main_language_family`

For every distinct value that appears among the remaining countries, one question is created.

Example:

* Is your country in Europe?
* Is Christianity the major religion in your country?
* Is Romance the main language family of your country?

#### 3.2 Boolean Questions

Boolean questions are generated from predefined true/false attributes such as:

* `landlocked`
* `an_island_or_archipelago`
* `a_monarchy`
* `a_federation`
* `have_nuclear_weapons`
* `horizontal_tricolor`
* `star_flag`

These questions ask whether a certain property is true for the target country.

Example:

* Is your country landlocked?
* Does your country have nuclear weapons?
* Does your country's flag have a star?

#### 3.3 Membership Questions

Membership questions are generated from relational data stored in:

* `regions`
* `organizations`

For every distinct region and every distinct organization that appears among the remaining countries, one question is created.

Example:

* Is your country in the South America region?
* Is your country a member of BRICS?

#### 3.4 Numeric Fallback Questions

Numeric fallback questions are not part of the standard question pool in every round.

They are generated only when the number of useful regular questions becomes too small.

These fallback questions are based on the current median values of the remaining candidate countries for:

* `population`
* `area`
* `number_of_bordering_countries`

For each of these attributes, the application can generate two questions:

* greater than median
* smaller than median

Example:

* Is the population of your country greater than 23 million?
* Is the area of your country smaller than 900 km²?
* Does your country border more than 3 countries?

### 4. Question Evaluation

For every generated question, the application checks how many of the remaining countries would answer `YES` and how many would answer `NO`.

This is done through the matching logic:

* enum question matches if the country attribute equals the question value
* boolean question matches if the attribute is `true`
* membership question matches if the country belongs to the given region or organization
* numeric question matches if the numeric value is greater than or smaller than the threshold, depending on the operator

### 5. Question Score

For each question, two numbers are calculated:

* `yes-count` = number of remaining countries that match the question
* `no-count` = number of remaining countries that do not match the question

The score is then computed as:

`score(question) = min(yes-count, no-count)`

This means:

* a question is good if it splits the remaining countries into two reasonably balanced groups
* a question is useless if all remaining countries would answer the same way

Examples:

* split `50 / 50` → score is `50`
* split `80 / 20` → score is `20`
* split `100 / 0` → score is `0`

So the higher the score, the more useful the question is for reducing the candidate set.

### 6. Choosing the Next Question

After all scores are calculated, the application proceeds as follows:

1. It sorts questions by score in descending order.
2. It removes all questions with score lower than `1`.
3. It keeps at most the top `5` useful questions.
4. It randomly selects one of those questions.

This approach provides a good balance between:

* asking informative questions
* avoiding the exact same deterministic sequence in every game

### 7. When Fallback Questions Are Used

If the number of useful regular questions is smaller than `5`, the application generates one random numeric fallback question and adds it to the current question pool.

This means:

* if there are many useful regular questions, the game uses only regular questions
* if useful regular questions become too rare, one numeric fallback question is introduced to help continue narrowing down the candidates

This is especially helpful in later rounds, when many regular questions become too weak or completely useless.

### 8. Transition From One Round to the Next

In each round, one question is asked and the user responds with:

* `YES`
* `NO`
* `DONT KNOW`

Then the candidate set is updated.

#### If the answer is `YES`

Only countries that match the question remain.

#### If the answer is `NO`

All countries that match the question are removed.

#### If the answer is `DONT KNOW`

The candidate set remains unchanged.

In all cases, the question is marked as already asked and will not be repeated later.

### 9. Stop Condition

The game stops in one of the following cases:

#### Case 1 — Exactly one country remains

The application outputs the final guess.

#### Case 2 — No remaining question can reduce the candidate set

If no useful next question exists, the application stops and prints the remaining countries as the best possible matches.

### 10. Full Game Flow

The complete flow of the application can be summarized as follows:

1. Load all countries from the database.
2. Enrich each country with regions and organizations.
3. Initialize the candidate set with all countries.
4. Generate regular questions for the current candidate set.
5. Score and rank those questions.
6. If too few useful regular questions exist, add one numeric fallback question.
7. Choose one question from the useful top-ranked set.
8. Ask the user the selected question.
9. Update the candidate set based on the answer.
10. Mark the question as already asked.
11. Repeat until one country remains or no useful reduction is possible.
12. Print the final result.

---

# Running the Project

Before running the project, make sure that:

* MySQL is installed and running
* the database is created by importing the `baza_akinator.sql` file
* the database connection settings in `resources/config.edn` are correct

## 1. Import the Database

First, import the `baza_akinator.sql` file into MySQL.

After that, verify that the database contains the required tables and data.

## 2. Check Configuration

Open `resources/config.edn` and make sure that the database connection parameters are correct:

* database name
* username
* password
* host
* port

## 3. Run the Application:
```
lein run
```
## 4. Run the Tests:
```
lein midje
```

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


