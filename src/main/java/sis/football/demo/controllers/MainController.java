package sis.football.demo.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sis.football.demo.model.FootballTeam;

import javax.naming.NamingException;
import javax.persistence.*;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;


@Controller
public class MainController {

    static Logger log = Logger.getLogger(MainController.class.getName());

    protected EntityManager getEntityManager() throws NamingException {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("FootballTeamsPU");
        return emf.createEntityManager();
    }

    @RequestMapping("/show_all")
    @ResponseBody
    Object returnAll() throws NamingException {
        log.info("Returning all data");
        EntityManager em = this.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM FootballTeam l").getResultList();
        } finally {
            em.close();
        }
    }

    @RequestMapping("/show_all_sorted")
    @ResponseBody
    Object returnAllSorted() throws NamingException {
        log.info("Returning all  sorted");
        EntityManager em = this.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM FootballTeam l ORDER BY stadiumCapacity").getResultList();
        } finally {
            em.close();
        }
    }

    @RequestMapping("/find")
    @ResponseBody
    Object findFootballTeam() throws NamingException {
        log.info("find ALL");
        EntityManager em = this.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM FootballTeam l ORDER BY stadiumCapacity").getResultList();
        } finally {
            em.close();
        }
    }

    @RequestMapping(value="/find/{id}", method = RequestMethod.GET)
    public @ResponseBody Object getItem(@PathVariable("id") Long itemId) throws NamingException {
        log.info("find filtered");
        EntityManager em = this.getEntityManager();
        try {
            TypedQuery<FootballTeam> query = em.createQuery(
                    "SELECT ft FROM FootballTeam ft WHERE ft.id = :itemId", FootballTeam.class);

            return query.setParameter("itemId", itemId).getSingleResult();
        }catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @PostMapping("/create_football_team")
    public @ResponseBody Object postController (
            @RequestBody FootballTeam footballTeam) throws NamingException{
        log.info("Creating team");
        EntityManager em = this.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(footballTeam);
            em.getTransaction().commit();
            return footballTeam;
        }catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    @ExceptionHandler(RollbackException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public Object processValidationError(HttpServletRequest req, Exception ex) {
        log.error("RprocessValidationError() called");
        return "Did you submit duplicate name?";
    }
}
