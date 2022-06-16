package com.project.market.modules.account.service;

import com.project.market.infra.fileupload.AwsS3Service;
import com.project.market.modules.account.entity.Account;
import com.project.market.modules.account.form.AddressForm;
import com.project.market.modules.account.form.ProfileForm;
import com.project.market.modules.account.form.SignupForm;
import com.project.market.modules.account.repository.AccountRepository;
import com.project.market.modules.item.entity.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3Service awsS3Service;
    private final EntityManager em;

    public void saveNewAccount(SignupForm signupForm) {
        // 비밀번호 암호화
        String encode = passwordEncoder.encode(signupForm.getPassword());
        signupForm.setEncodedPassword(encode);

        Account account = Account.createNewAccount(signupForm);

        accountRepository.save(account);
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
    public List<Tag> findTags(Account account) {
        Account findAccount = accountRepository.findAccountWithTagsById(account.getId());
        return findAccount.getTags();
    }


    public void saveNewTag(Account account, Tag tag) {
        account.getTags().add(tag);
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

    public void modifyProfileImageAndNickName(Account account, String nickname, MultipartFile profileImage) {
        String dir = "account/profile/" + account.getId() + "/";
        if (profileImage != null) {
            String url = awsS3Service.uploadFile(dir, profileImage);
            account.modifyProfileImageAndNickname(nickname, url);
        } else {
            account.modifyNickname(nickname);
        }
    }
}
