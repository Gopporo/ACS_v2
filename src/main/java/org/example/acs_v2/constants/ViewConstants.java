package org.example.acs_v2.constants;

/**
 * Константы для имен представлений (view names)
 */
public final class ViewConstants {

    private ViewConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Admin views
    public static final String ADMIN_USERS = "admin-users";
    public static final String ADMIN_ZONES = "admin-zones";
    public static final String ADMIN_DEPARTMENTS = "admin-departments";
    public static final String ADMIN_CHANGE_ROLE = "admin-changeRole";
    public static final String ADMIN_PREREGISTRATION = "admin-preregistration";
    public static final String ADD_USER = "addUser";
    public static final String ADD_ZONE = "addZone";
    public static final String ADD_DEPARTMENT = "addDepartment";

    // Director views
    public static final String DIRECTOR_USERS = "director-users";
    public static final String DIRECTOR_APPLICATIONS = "director-applications";
    public static final String DIRECTOR_REPORTS = "director-reports";
    public static final String DIRECTOR_EDIT_USER = "director-edit-user";
    public static final String DIRECTOR_EDIT_APPLICATION = "director-edit-application";
    public static final String DIRECTOR_REPORT_INFO = "director-report-info";

    // Employee views
    public static final String EMPLOYEE_APPLICATIONS = "employee-applications";
    public static final String EMPLOYEE_MY_APPLICATIONS = "employee-my-applications";
    public static final String EMPLOYEE_REPORTS = "employee-reports";
    public static final String EMPLOYEE_ADD_REPORT = "employee-addReport";
    public static final String EMPLOYEE_REPORT_INFO = "employee-report-info";

    // Common views
    public static final String INDEX = "index";
    public static final String LOGIN = "login";
    public static final String REGISTRATION = "registration";
    public static final String USER_INFO = "user-info";
    public static final String PROFILE = "profile";
    public static final String PROFILE_EDIT = "profile-edit";
    public static final String ADD_APPLICATION = "addApplication";
    public static final String ERROR_403 = "403";
    public static final String ERROR = "error";
}
