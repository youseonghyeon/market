package com.project.market.modules.account.dao;

import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.entity.Zone;
import com.project.market.modules.account.form.AddressForm;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.item.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityManager em;

    public void saveNewAccount(SignupForm signupForm) {
        Account account = Account.createNewAccount(signupForm);
        passwordEncoding(account, signupForm.getPassword());

        accountRepository.save(account);
    }

    private void passwordEncoding(Account account, String password) {
        String encodedPassword = passwordEncoder.encode(password);
        account.modifyPassword(encodedPassword);
    }

    public void editProfile(Account account, ProfileForm profileForm) {
        account.modifyProfile(profileForm);
        accountRepository.save(account);
    }

    public void modifyPassword(Account account, String newPassword) {
        String encodedPassword = passwordEncoder.encode(newPassword);
        account.modifyPassword(encodedPassword);
        accountRepository.save(account);
    }

    @Transactional(readOnly = true)
    public Set<Tag> findTags(Account account) {
        Account findAccount = accountRepository.findAccountWithTagsById(account.getId());
        return findAccount.getTags();
    }

    @Transactional(readOnly = true)
    public Set<Zone> findZones(Account account) {
        Account findAccount = accountRepository.findAccountWithZonesById(account.getId());
        return findAccount.getZones();
    }

    public void saveNewTag(Account account, Tag tag) {
        account.getTags().add(tag);
    }

    public void saveNewZone(Account account, Zone zone) {
        Account findAccount = accountRepository.findAccountWithZonesById(account.getId());
        // TODO ##ERROR## 의도하지 않은 delete쿼리문이 나감 && 중복제거 해야함
        findAccount.getZones().add(zone);
    }

    public String createPasswordToken(Account account) {
        String token = createNewPasswordToken();
        account.savePasswordToken(token);
        accountRepository.save(account);
        return token;
    }

    private String createNewPasswordToken() {
        return UUID.randomUUID().toString();
    }

    public void destroyPasswordToken(Account account) {
        account.savePasswordToken(null);
    }

    public void modifyRole(Account account, String role) {
        account.modifyRole(role);
    }

    public void modifyAddress(Account account, AddressForm addressForm) {
        account.modifyAddress(addressForm);
        accountRepository.save(account);
    }

    public void deleteTag(Account account, Tag tag) {
        account.getTags().remove(tag);
    }

    public void deleteAccount(Account account) {
        account.withdrawal();
        accountRepository.save(account);
    }
}
