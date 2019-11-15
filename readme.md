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
####Subjects:

p.s. all id's below refer to the field uuid, not the primary key id. 

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
You also remove previously assigned subjects by not
providing them in array. However if you dont include the subjects field in body no subjects will be removed. 
The example will remove all subjects except rocket science and math and add them if it is not yet in db.

example body:
```
{
  "forename": "Johan",
  "lastname": "svensson",
  "email" : "sven@hotmail.com",
  "subjects" ["rocket science", "math"]
}
```

PATCH ```/school/students/{id}``` --> updates student with id.
You also remove previously assigned subjects by not
providing them in the subjects array. However if you dont include the subjects field in body no subjects will be removed.
The example will remove all subjects except swedish or add swedish if it is not yet in db.

example body:
```
{
  "forename": "Johan",
  "email" : "johan@hotmail.com",
  "subjects" ["swedish"]
}
```

DELETE ```/school/students/{id}``` --> deletes student with id.


####Subjects:

(you do not add students or teachers when creating or modifying subjects. This is done through the student and teacher endpoints)

GET ```/school/subjects``` --> returns a list of all subjects

POST ```/school/subjects``` --> adds a new subject
example body:
```
{
  "title" : "english"
}
```

DELETE ```/school/subjects/{id}``` --> deletes subject with id.

####Teachers:

GET ```/school/teachers``` --> returns a list of all students


GET ```/school/teachers/{id}``` ---> returns the student with id


POST ```/school/teachers``` --> adds a new teachers
example body:
```
{
  "forename": "Sven",
  "lastname": "Johansson",
  "subjects": ["rocket science", "physics"]
}
```


PATCH ```/school/teachers/{id}``` --> updates teacher with id. You also remove previously assigned subjects by not
providing them in subjects array. However if you dont include the subjects field in body no subjects will be removed.
The example will remove all subjects except swedish or add swedish if it is not yet in db.
example body:
```
{
"forename": "Fredrik",
"subjects": ["swedish"]
}
```

DELETE ```/school/teachers/{id}``` --> deletes teacher with id.



####Changes :

1. Added uuid to all entities so that we dont query for primary key in db any more. 
Safer than using the email for api actions for students.

2. Changed to fetchtype Lazy and use join fetch when making some queries

3. Add teacher entity. Subjects are manyToMany bidirectional with teacher. The teacher can find their students through their subjects.

4. Changed so that you dont need to add subjects before adding students or teachers. The program checks 
if the subject already exists in db and if not creates it.
Works on both create and updates for Student and teacher.

#### Test run :

(The id in the endpoints is value of the uuid field)

1. Drop all tables in school database.
2. Deploy project to wildfly
3. Create a student

POST ```/school/students``` 
```
{
  "forename": "sven",
  "lastname": "svensson",
  "email" : "sven@hotmail.com",
  "subjects" : ["swedish"]  
}
```

4. Verify that the student has been created:

GET ```/school/students``` 

5. Verify that the subject "swedish" has been created and that it holds the student:

GET ```/school/subjects``` 

6. Add a new subject :

POST ```/school/subjects``` 
```
{
  "title" : "english"
}
```
7. Verify that the subject "english" has been created and that no students or teachers are assigned:

GET ```/school/subjects``` 

8. Update the student sven to drop out of swedish and start to take english:

PATCH ```/school/students/{id}``` 

```
{
  "subjects" : ["english"]
} 
```
9. Verify that the subject english now holds the student and that swedish no longer has a any students:

GET ```/school/subjects``` 

10. Add a teacher who teaches math and english

POST ```/school/teachers``` --> 
```
{
  "forename": "Sven",
  "lastname": "Johansson",
  "subjects": ["math", "english"]
}
```

11. Verify that math is created in 

GET ```/school/subjects``` 

12. Verify that you can see what students the Sven has per subject at: 

GET ```/school/teachers``` 

13. Update so that Sven no longer teaches math:

PATCH ```/school/teachers/{id}``` 
```
{
"subjects": ["english"]
}
```

14. Assert that he no is the teacher in charge of math at:

GET ```/school/subjects``` 

15. Fredrik decides to quit. Delete him at:

DELETE ```/school/teachers/{id}``` 

16. Ensure that he is removed from his remaining subject:

GET ```/school/subjects``` 

17. Its no longer possible to take english at this school:

DELETE ```/school/subjects/{id}```

18. The student that took english is no longer registered on that course:

GET ```/school/students``` 