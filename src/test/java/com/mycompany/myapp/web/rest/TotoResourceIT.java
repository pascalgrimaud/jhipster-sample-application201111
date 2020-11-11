package com.mycompany.myapp.web.rest;

import com.mycompany.myapp.JhipsterSampleApplicationApp;
import com.mycompany.myapp.domain.Toto;
import com.mycompany.myapp.repository.TotoRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the {@link TotoResource} REST controller.
 */
@SpringBootTest(classes = JhipsterSampleApplicationApp.class)
@AutoConfigureMockMvc
@WithMockUser
public class TotoResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    @Autowired
    private TotoRepository totoRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restTotoMockMvc;

    private Toto toto;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Toto createEntity(EntityManager em) {
        Toto toto = new Toto()
            .name(DEFAULT_NAME);
        return toto;
    }
    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Toto createUpdatedEntity(EntityManager em) {
        Toto toto = new Toto()
            .name(UPDATED_NAME);
        return toto;
    }

    @BeforeEach
    public void initTest() {
        toto = createEntity(em);
    }

    @Test
    @Transactional
    public void createToto() throws Exception {
        int databaseSizeBeforeCreate = totoRepository.findAll().size();
        // Create the Toto
        restTotoMockMvc.perform(post("/api/totos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(toto)))
            .andExpect(status().isCreated());

        // Validate the Toto in the database
        List<Toto> totoList = totoRepository.findAll();
        assertThat(totoList).hasSize(databaseSizeBeforeCreate + 1);
        Toto testToto = totoList.get(totoList.size() - 1);
        assertThat(testToto.getName()).isEqualTo(DEFAULT_NAME);
    }

    @Test
    @Transactional
    public void createTotoWithExistingId() throws Exception {
        int databaseSizeBeforeCreate = totoRepository.findAll().size();

        // Create the Toto with an existing ID
        toto.setId(1L);

        // An entity with an existing ID cannot be created, so this API call must fail
        restTotoMockMvc.perform(post("/api/totos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(toto)))
            .andExpect(status().isBadRequest());

        // Validate the Toto in the database
        List<Toto> totoList = totoRepository.findAll();
        assertThat(totoList).hasSize(databaseSizeBeforeCreate);
    }


    @Test
    @Transactional
    public void getAllTotos() throws Exception {
        // Initialize the database
        totoRepository.saveAndFlush(toto);

        // Get all the totoList
        restTotoMockMvc.perform(get("/api/totos?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(toto.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)));
    }
    
    @Test
    @Transactional
    public void getToto() throws Exception {
        // Initialize the database
        totoRepository.saveAndFlush(toto);

        // Get the toto
        restTotoMockMvc.perform(get("/api/totos/{id}", toto.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(toto.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME));
    }
    @Test
    @Transactional
    public void getNonExistingToto() throws Exception {
        // Get the toto
        restTotoMockMvc.perform(get("/api/totos/{id}", Long.MAX_VALUE))
            .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    public void updateToto() throws Exception {
        // Initialize the database
        totoRepository.saveAndFlush(toto);

        int databaseSizeBeforeUpdate = totoRepository.findAll().size();

        // Update the toto
        Toto updatedToto = totoRepository.findById(toto.getId()).get();
        // Disconnect from session so that the updates on updatedToto are not directly saved in db
        em.detach(updatedToto);
        updatedToto
            .name(UPDATED_NAME);

        restTotoMockMvc.perform(put("/api/totos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(updatedToto)))
            .andExpect(status().isOk());

        // Validate the Toto in the database
        List<Toto> totoList = totoRepository.findAll();
        assertThat(totoList).hasSize(databaseSizeBeforeUpdate);
        Toto testToto = totoList.get(totoList.size() - 1);
        assertThat(testToto.getName()).isEqualTo(UPDATED_NAME);
    }

    @Test
    @Transactional
    public void updateNonExistingToto() throws Exception {
        int databaseSizeBeforeUpdate = totoRepository.findAll().size();

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restTotoMockMvc.perform(put("/api/totos")
            .contentType(MediaType.APPLICATION_JSON)
            .content(TestUtil.convertObjectToJsonBytes(toto)))
            .andExpect(status().isBadRequest());

        // Validate the Toto in the database
        List<Toto> totoList = totoRepository.findAll();
        assertThat(totoList).hasSize(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    public void deleteToto() throws Exception {
        // Initialize the database
        totoRepository.saveAndFlush(toto);

        int databaseSizeBeforeDelete = totoRepository.findAll().size();

        // Delete the toto
        restTotoMockMvc.perform(delete("/api/totos/{id}", toto.getId())
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        List<Toto> totoList = totoRepository.findAll();
        assertThat(totoList).hasSize(databaseSizeBeforeDelete - 1);
    }
}
