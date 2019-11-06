package se.alten.schoolproject.entity;


import org.junit.Test;
import static org.junit.Assert.*;

public class StudentTest {

    @Test
    public void toEntity() {
        String jsonString = "{\n" +
                "  \"forename\":\"hej\",\n" +
                "  \"lastname\":\"hej2\",\n" +
                "  \"email\": \"yo5@gmail\"\n" +
                "  }";

        Student student = new Student();
        Student result = student.toEntity(jsonString);
        System.out.println(result);

    }

}