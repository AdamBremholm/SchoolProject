# SchoolProject
by Adam Bremholm

### Installation

Requires:
Wildfly
JEE 8
Java 11
Git
Maven
Mariadb/MySQL

### Wildfly configuration
Install wildfly 18
Add a user, and place the school.cli script under the bin folder.
Create database school. The script will need a mysql connector under C:\Users or ~/ to work.

The script is predefined for mysql.connector-java-8.0.12.jar. Change location and version for your own liking.

Start Wildfly, and once running, open a new prompt, and go to the bin folder.
Write:
```
 jboss-cli -c --file=school.cli
```
It should say outcome success. Write 
```
jboss-cli -c --command=:reload and run
```
to restart the server.
go into the /bin folder and run:

```
standalone -c standalone-full.xml
```
to start the wildfly server.

In project root:

```
wildfly:undeploy clean:clean wildfly:deploy
```
To make a clean deploy of the .war file to the wildfly server.

### Endpoints:

GET ```/school/students``` --> returns a list of all students
Can also be queried by name: ```/school/students?name=xxx``` --> returns list of all student where first or lastname matches parameter in name.

GET ```/school/students/{id}``` ---> returns the student with id



POST ```/school/students``` --> adds a new student
example body:
```
{
  "forename": "sven",
  "lastname": "svensson",
  "email" : "sven@hotmail.com"
}
```

PUT ```/school/students/{id}```--> replaces student with id. Need all fields.
example body:
```
{
  "forename": "Johan",
  "lastname": "svensson",
  "email" : "sven@hotmail.com"
}
```

PATCH ```/school/students/{id}``` --> updates student with id.
example body:
```
{
  "forename": "Johan",
  "email" : "johan@hotmail.com"
}
```

DELETE ```/school/students/{id}``` --> deletes student with id.

###Adam 

####completed tasks:

1. Change the api design to be more resource based (for example: post /students instead off students/add)

2. Rewrote Student transaction class. Simplified JPA transactions with entity manager methods.

3. Reworked the update methods.

4. Made controller handle parsing from incoming json directly to Student bean by using Student as in parameter. 

5. Made update methods return the updated object.

6. Added set id field in toModel. 

7. Added ListNullOrEmptyValues in ReflectionUtils --> removed switch statements that sets "empty" on empty etc. Created Exceptions annotated @ApplicationException and throws to method in controller for handling. This simplifies the flow and we believe it is better to 
handle an exception directly instead of passing strings around around the program. 

8. Changed list all method to return instances of model instead so we dont expose the Student class. 

9. Generified Interfaces to allow reuse with other objects.

10. Remove instantiation of Student and supermodel in SchoolDataAccess, replaces toModel with static method in StudentModel.

11. Delete by id instead of email because this felt more secure.

12. Add find by email method to be able to return newly created objects. (This way we get their id, before the program returned a studentModel without id)








