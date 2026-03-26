package org.example.acs_v2.constants;

/**
 * Константы для редиректов
 */
public final class RedirectConstants {

    private RedirectConstants() {
        throw new UnsupportedOperationException("Utility class");
    }

    // Admin redirects
    public static final String REDIRECT_ADMIN_USERS = "redirect:/admin/users";
    public static final String REDIRECT_ADMIN_ZONES = "redirect:/admin/zones";
    public static final String REDIRECT_ADMIN_DEPARTMENTS = "redirect:/admin/departments";
    public static final String REDIRECT_ADMIN_PREREGISTRATION = "redirect:/admin/preregistration";

    // Director redirects
    public static final String REDIRECT_DIRECTOR_USERS = "redirect:/director/users";
    public static final String REDIRECT_DIRECTOR_APPLICATIONS = "redirect:/director/applications";

    // Employee redirects
    public static final String REDIRECT_EMPLOYEE_APPLICATIONS = "redirect:/employee/applications";
    public static final String REDIRECT_EMPLOYEE_MY_APPLICATIONS = "redirect:/employee/applications/my";
    public static final String REDIRECT_EMPLOYEE_REPORTS = "redirect:/employee/reports";

    // Common redirects
    public static final String REDIRECT_INDEX = "redirect:/index";
    public static final String REDIRECT_LOGIN = "redirect:/login";
    public static final String REDIRECT_LOGIN_LOGOUT = "redirect:/login?logout";
    public static final String REDIRECT_LOGOUT = "redirect:/logout";
}
