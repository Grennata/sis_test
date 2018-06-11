package sis.football.demo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import sis.football.demo.controllers.MainController;
import sis.football.demo.model.FootballTeam;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class DemoApplicationTestsSimple {

    @Autowired
    private MainController controller;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @Test
    public void contextLoads() {
        assertThat(controller).isNotNull();
    }

    @Test
    public void returns_empy_List_when_dataBase_isEmpty() throws Exception {
        this.mockMvc.perform(get("/show_all")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @After
    public void deleteTeams() throws Exception {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FootballTeamsPU");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM FootballTeam").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    static FootballTeam getTestNewFootballTeam() {
        FootballTeam rv = new FootballTeam();
        rv.setName("MK Dons");
        rv.setOwner("Despair");
        rv.setStadiumCapacity(423);
        rv.setNumberOfPlayers(94);
        rv.setDateOfCreation(new Date(2018, 06, 05));
        rv.setCompetition("Nobody");
        rv.setCity("Milton Keynes");
        return rv;
    }

    @Test
    public void returns_value_when_db_has_value() throws Exception {
        FootballTeam team = getTestNewFootballTeam();
        String json = mapper.writeValueAsString(team);

        mockMvc.perform(post("/create_football_team")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());



        this.mockMvc.perform(get("/show_all")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[{\"name\":\"MK Dons\",\"city\":\"Milton Keynes\",\"owner\":\"Despair\",\"stadiumCapacity\":423,\"competition\":\"Nobody\",\"numberOfPlayers\":94,\"dateOfCreation\":\"3918-07-04T23:00:00.000+0000\"}]"));
    }

    @Test
    public void returns_values_when_db_has_values() throws Exception {
        FootballTeam team1 = getTestNewFootballTeam();
        FootballTeam team2 = getTestNewFootballTeam();

        team2.setName("Hello world");
        team2.setStadiumCapacity(10240);

        mockMvc.perform(post("/create_football_team")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(team1))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/create_football_team")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(team2))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        this.mockMvc.perform(get("/show_all")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[\n" +
                        "   {\n" +
                        "      \"name\":\"MK Dons\",\n" +
                        "      \"city\":\"Milton Keynes\",\n" +
                        "      \"owner\":\"Despair\",\n" +
                        "      \"stadiumCapacity\":423,\n" +
                        "      \"competition\":\"Nobody\",\n" +
                        "      \"numberOfPlayers\":94,\n" +
                        "      \"dateOfCreation\":\"3918-07-04T23:00:00.000+0000\"\n" +
                        "   },\n" +
                        "   {\n" +
                        "      \"name\":\"Hello world\",\n" +
                        "      \"city\":\"Milton Keynes\",\n" +
                        "      \"owner\":\"Despair\",\n" +
                        "      \"stadiumCapacity\":10240,\n" +
                        "      \"competition\":\"Nobody\",\n" +
                        "      \"numberOfPlayers\":94,\n" +
                        "      \"dateOfCreation\":\"3918-07-04T23:00:00.000+0000\"\n" +
                        "   }\n" +
                        "]"
                ));
    }

    @Test
    public void returns_sorted_values_when_db_has_values() throws Exception {
        FootballTeam team1 = getTestNewFootballTeam();
        FootballTeam team2 = getTestNewFootballTeam();

        team2.setName("Hello sorting");
        team2.setStadiumCapacity(10240);

        mockMvc.perform(post("/create_football_team")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(team1))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        mockMvc.perform(post("/create_football_team")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(team2))
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        this.mockMvc.perform(get("/show_all_sorted")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().json("[\n" +
                        "   {\n" +
                        "      \"name\":\"MK Dons\",\n" +
                        "      \"city\":\"Milton Keynes\",\n" +
                        "      \"owner\":\"Despair\",\n" +
                        "      \"stadiumCapacity\":423,\n" +
                        "      \"competition\":\"Nobody\",\n" +
                        "      \"numberOfPlayers\":94,\n" +
                        "      \"dateOfCreation\":\"3918-07-04T23:00:00.000+0000\"\n" +
                        "   },\n" +
                        "   {\n" +
                        "      \"name\":\"Hello sorting\",\n" +
                        "      \"city\":\"Milton Keynes\",\n" +
                        "      \"owner\":\"Despair\",\n" +
                        "      \"stadiumCapacity\":10240,\n" +
                        "      \"competition\":\"Nobody\",\n" +
                        "      \"numberOfPlayers\":94,\n" +
                        "      \"dateOfCreation\":\"3918-07-04T23:00:00.000+0000\"\n" +
                        "   }\n" +
                        "]"
                ));
    }


}
