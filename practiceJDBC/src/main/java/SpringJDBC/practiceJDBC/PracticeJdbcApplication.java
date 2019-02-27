package SpringJDBC.practiceJDBC;

import SpringJDBC.practiceJDBC.DAO.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class PracticeJdbcApplication implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(PracticeJdbcApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(PracticeJdbcApplication.class, args);
    }

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        jdbcTemplate.execute("DROP TABLE customers if exists");
        jdbcTemplate.execute("CREATE TABLE customers(id SERIAL , first_name VARCHAR(255), last_name VARCHAR(255))");


        //SPLIT NAMES EXAMPLE FOR GATHERED SINGLE STRING FROM FRONT
        List<Object[]> splitNames = Stream.of("fname1 lname1", "fname2 lname2", "fname3 lname3").map(name -> name.split(" ")).collect(Collectors.toList());

//        splitNames.forEach(names -> log.info(String.format("INSERT INTO customers(first_name, last_name) VALUES(?,?)", names[0], names[1])));

        // Uses JdbcTemplate's batchUpdate operation to bulk load data
        jdbcTemplate.batchUpdate("INSERT INTO customers(first_name, last_name) VALUES (?,?)", splitNames);



        jdbcTemplate.query("SELECT id, first_name, last_name FROM customers WHERE first_name = ?",
                new Object[] {"fname2"},(rs, rowNum) -> new Customer(rs.getLong("id"),
                        rs.getString("first_name"), rs.getString("last_name"))).forEach(customer -> log.info(customer.toString()));

        jdbcTemplate.query("SELECT * FROM customers", (ResultSetExtractor<Map>) rs -> {
            HashMap<String,String> mapRet= new HashMap<>();
            while(rs.next()){
                mapRet.put(rs.getString("first_name"),rs.getString("last_name"));
            }
            return mapRet;
        });
        jdbcTemplate.query("SELECT id, first_name, last_name FROM customers",
                (rs, rowNum) -> new Customer(rs.getInt("id"),
                        rs.getString("first_name"), rs.getString("last_name"))).forEach(customer -> log.info(customer.toString()));

    }
}

