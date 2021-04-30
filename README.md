# Jira time fetcher (woof)
## Prerequisites
- Java 11
- Jira API key: You can generate one here:  https://id.atlassian.com/manage-profile/security/api-tokens
- In the application folder you need the config in `fetchJiraTime-config.json` (see `fetchJiraTime-config_template.json`)

## Run with gradle (easiest) 

    gradlew run --args="...options..."

## Run with JVM  (a bit more work)
### Build
For run with JVM you first need to build the jar file:

    gradlew build
    
### Start command via JVM
- Windows: `java -jar build\libs\fetchJiraTime-0.0.1-SNAPSHOT.jar ...options...`
- Linux: `java -jar build/libs/fetchJiraTime-0.0.1-SNAPSHOT.jar ...options...`

## Run options
````
 Usage: <main class> [command] [command options]
  Commands:
    allForSprint      Fetch all for sprint
      Usage: allForSprint [options]
        Options:
        * -s
            The sprint ID

    oneUser      Fetch data for one user
      Usage: oneUser [options]
        Options:
        * -f, --from
            From date (format 2021-04-24)
          -t, --till
            Till date inclusive(format 2021-04-24 - defaults to from date)
          -u, --username
            UserID (defaults to connecting user)
````
### Command examples
Get data for sprint with id **21**:

    # with gradle:
    gradlew run --args="allForSprint -s 21"

    # with JVM:
    java -jar build/libs/fetchJiraTime-0.0.1-SNAPSHOT.jar allForSprint -s 21


Get data for user **user@server.com** for date **2021-04-28**

    # with gradle:
    gradlew run --args="oneUser -f 2021-04-28 -u user@server.com"

    # with JVM:
    java -jar build/libs/fetchJiraTime-0.0.1-SNAPSHOT.jar oneUser -f 2021-04-28 -u user@server.com
