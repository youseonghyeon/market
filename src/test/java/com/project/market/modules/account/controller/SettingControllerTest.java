package com.project.market.modules.account.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.market.WithAccount;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.account.repository.ZoneRepository;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.Zone;
import com.project.market.modules.item.repository.TagRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class SettingControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    TagRepository tagRepository;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    ZoneRepository zoneRepository;

    @AfterEach
    public void afterEach() {
        accountRepository.deleteAll();
    }

    @Test
    void passwordInitBinder() {

    }

    @Test
    @WithAccount("testUser")
    @DisplayName("????????? ?????? ???")
    void profileEditForm() throws Exception {
        mockMvc.perform(get("/profile/edit"))
                .andExpect(model().attributeExists("profileForm"))
                .andExpect(view().name("account/settings/profile-edit"))
                .andExpect(status().isOk());
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("????????? ??????")
    void profileEdit() throws Exception {

        mockMvc.perform(post("/profile/edit")
                        .param("nickname", "PickMe")
                        .param("phone", "01011882299")
                        .param("email", "testmodify@mail.com")
                        .param("profileImage", "")
                        .with(csrf()))
                .andExpect(redirectedUrl("/profile"))
                .andExpect(flash().attributeExists("message"))
                .andExpect(status().is3xxRedirection());

        Account findAccount = accountRepository.findByLoginId("testUser");
        assertEquals(findAccount.getNickname(), "PickMe");
        assertEquals(findAccount.getPhone(), "01011882299");
        assertEquals(findAccount.getEmail(), "testmodify@mail.com");
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("???????????? ?????? ???")
    void passwordModifyForm() throws Exception {
        mockMvc.perform(get("/password"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("passwordForm"))
                .andExpect(view().name("account/settings/password"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("???????????? ??????")
    void passwordModify() throws Exception {
        mockMvc.perform(post("/password")
                        .param("password", "testpass")
                        .param("newPassword", "modifiedPass")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        Account findAccount = accountRepository.findByLoginId("testUser");
        assertTrue(passwordEncoder.matches("modifiedPass", findAccount.getPassword()));
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

//    @Test
//    @WithAccount("testUser")
//    @DisplayName("?????? ?????? ???")
//    void tagSettingForm() throws Exception {
//        mockMvc.perform(get("/profile/tag"))
//                .andExpect(status().isOk())
//                .andExpect(model().attributeExists("tagList"))
//                .andExpect(model().attributeExists("whiteList"))
//                .andExpect(view().name("account/settings/tag"));
//    }
//
//
//    @Test
//    @WithAccount("testUser")
//    @DisplayName("?????? ??????")
//    void tagSetting() throws Exception {
//        TagDto tagDto = new TagDto();
//        tagDto.setTag("?????????");
//        mockMvc.perform(post("/profile/tag")
//                        .param("newTag", "?????????")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(tagDto))
//                        .with(csrf()))
//                .andExpect(status().isOk());
//        Tag tag = tagRepository.findByTitle("?????????");
//        Account account = accountRepository.findByLoginId("testUser");
//        Set<Tag> tagContainer = account.getTags();
//        assertTrue(tagContainer.contains(tag));
//    }

    @Test
    @WithAccount("testUser")
    @DisplayName("?????? ?????? ???")
    void zoneSettingFrom() throws Exception {
        mockMvc.perform(get("/profile/zone"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("zoneList"))
                .andExpect(model().attributeExists("whiteList"))
                .andExpect(view().name("account/settings/zone"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("?????? ??????")
    void zoneSetting() throws Exception {
        List<Zone> zoneList = zoneRepository.findAll();
        if (zoneList.isEmpty()) {
            throw new IllegalStateException();
        }
        Zone zone = zoneList.get(0);
        mockMvc.perform(post("/profile/zone")
                        .param("new-zone", zone.getCity())
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/profile/zone"));

        Account account = accountRepository.findByLoginId("testUser");
        Set<Zone> zoneContainer = account.getZones();
        assertTrue(zoneContainer.contains(zone));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("????????? ?????? ?????? ???")
    void addressSettingForm() throws Exception {
        mockMvc.perform(get("/profile/address"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("addressForm"))
                .andExpect(view().name("account/settings/address"));
    }

    @Test
    @WithAccount("testUser")
    @DisplayName("????????? ?????? ??????")
    void addressSetting() throws Exception {
        mockMvc.perform(post("/profile/address")
                        .param("zoneCode", "11111")
                        .param("roadAddress", "??????????????? 111")
                        .param("addressDetail", "111??? 111???")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("message"))
                .andExpect(redirectedUrl("/profile"));

        Account account = accountRepository.findByLoginId("testUser");
        assertEquals(account.getZoneCode(), "11111");
        assertEquals(account.getRoadAddress(), "??????????????? 111");
        assertEquals(account.getAddressDetail(), "111??? 111???");
    }
}
