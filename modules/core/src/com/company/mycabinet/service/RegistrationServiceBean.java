package com.company.mycabinet.service;

import com.company.mycabinet.config.RolesConfig;
import com.company.mycabinet.entity.BusinessCategory;
import com.company.mycabinet.entity.ExtUser;
import com.company.mycabinet.entity.UserType;
import com.haulmont.cuba.core.global.*;
import com.haulmont.cuba.security.entity.Group;
import com.haulmont.cuba.security.entity.Role;
import com.haulmont.cuba.security.entity.User;
import com.haulmont.cuba.security.entity.UserRole;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service(RegistrationService.NAME)
public class RegistrationServiceBean implements RegistrationService {

    @Inject
    protected DataManager dataManager;
    @Inject
    protected Metadata metadata;
    @Inject
    protected PasswordEncryption passwordEncryption;

    @Inject
    protected RolesConfig rolesConfig;


    @Override
    public void registerStandartUser(Map<String, Object> paramsMap) {
        ExtUser user = (ExtUser) metadata.create(ExtUser.class);
        user.setActive(true);
        user.setUserType((UserType) paramsMap.get("userType"));
        user.setLogin((String) paramsMap.get("login"));
        user.setFirstName((String) paramsMap.get("firstName"));
        user.setMiddleName((String) paramsMap.get("middleName"));
        user.setLastName((String) paramsMap.get("surName"));
        //user.setBirthday((Date) paramsMap.get("birthday"));
        user.setOrganizationName((String) paramsMap.get("orgName"));
        if (UserType.CUSTOMER.equals(user.getUserType()))
            user.setActivityType((String) paramsMap.get("activityName"));
        user.setLegalAddress((String) paramsMap.get("legalAddress"));
        user.setActualAddress((String) paramsMap.get("actualAddress"));
        user.setBusinessCategory((BusinessCategory) paramsMap.get("businessCategory"));
        //for manufacturer
        if (paramsMap.get("firmAge") != null)
            user.setFirmAge(Integer.valueOf((String) paramsMap.get("firmAge")));
        user.setProductionVolume((String) paramsMap.get("productVolume"));
        user.setGoodsCategory((String) paramsMap.get("goodsCategory"));

        if (StringUtils.isNotEmpty((String) paramsMap.get("password"))) {
            String passwordHash = passwordEncryption.getPasswordHash(user.getId(), (String) paramsMap.get("password"));
            user.setPassword(passwordHash);
        }
        initUserGroup(user);
        dataManager.commit(user, "extUser-view");
        addDefaultRoles(user);
    }

    @Override
    public boolean isUniqueUserLogin(String login) {
        LoadContext<ExtUser> extlc = new LoadContext(ExtUser.class).setView("extUser-view");
        extlc.setQueryString("select t from mycabinet$ExtUser t where t.login = :login or t.loginLowerCase = :lowerLogin")
                .setParameter("login", login)
                .setParameter("lowerLogin", login.toLowerCase())
                .setMaxResults(1);
        ExtUser extUser = dataManager.load(extlc);

        LoadContext<User> lc = new LoadContext(User.class).setView("_local");
        lc.setQueryString("select t from sec$User t where t.loginLowerCase = :lowerLogin")
                .setParameter("lowerLogin", login.toLowerCase())
                .setMaxResults(1);
        User user = dataManager.load(lc);

        return user == null && extUser == null ? true : false;
    }

    protected void addDefaultRoles(ExtUser user) {
        String roleId = "";
        if (user.getUserType().equals(UserType.CUSTOMER)) {
            roleId = rolesConfig.getCustomerRole();
        }

        if (user.getUserType().equals(UserType.MANUFACTURER)) {
            roleId = rolesConfig.getManufacturerRole();
        }

        LoadContext<Role> ctx = new LoadContext<>(Role.class);
        ctx.setQueryString("select r from sec$Role r where r.id = :id")
                .setParameter("id", UUID.fromString(roleId));
        List<Role> defaultRoles = dataManager.loadList(ctx);

        List<UserRole> newRoles = new ArrayList<>();
        if (user.getUserRoles() != null) {
            newRoles.addAll(user.getUserRoles());
        }

        CommitContext commitContext = new CommitContext();
        for (Role role : defaultRoles) {
            UserRole userRole = metadata.create(UserRole.class);
            userRole.setRole(role);
            userRole.setUser(user);
            commitContext.addInstanceToCommit(userRole);
            newRoles.add(userRole);
        }

        user.setUserRoles(newRoles);
        dataManager.commit(commitContext);
    }

    protected void initUserGroup(User user) {
        LoadContext<Group> ctx = new LoadContext<>(Group.class);
        ctx.setQueryString("select g from sec$Group g");
        ctx.setView(View.MINIMAL);
        List<Group> groups = dataManager.loadList(ctx);
        if (groups.size() == 1) {
            user.setGroup(groups.get(0));
        }
    }
}