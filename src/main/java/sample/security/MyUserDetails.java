package sample.security;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import sample.model.UserInfo;

/**
 * 認証情報を設定するためのクラス
 *
 * @author a-numadate
 *
 */
public class MyUserDetails extends User {
    private final String orgName;

    public MyUserDetails(UserInfo userInfo, List<? extends GrantedAuthority> authorityList) {
        super(userInfo.getUserId(), userInfo.getPassword(), true, true, true, true, authorityList);
        this.orgName = userInfo.getOrganizationName();
    }

    public String getOrgName() {
        return orgName;
    }
}
